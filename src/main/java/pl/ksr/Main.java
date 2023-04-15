package pl.ksr;

import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksr.extractor.FeatureExtractor;
import pl.ksr.extractor.FeatureExtractorConfig;
import pl.ksr.extractor.ImmutableFeatureExtractorConfig;
import pl.ksr.metric.ChebyshevMetric;
import pl.ksr.metric.EuclideanMetric;
import pl.ksr.metric.ManhattanMetric;
import pl.ksr.metric.Metric;
import pl.ksr.model.Article;
import pl.ksr.model.Country;
import pl.ksr.model.FeatureVector;
import pl.ksr.qualificationQuality.ClassificationQuality;
import pl.ksr.qualificationQuality.ConfusionMatrix;
import pl.ksr.reader.ArticleReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.typesafe.config.ConfigFactory.load;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        AppConfig config = AppConfig.fromRootConfig(load());

        if (config.guiMode()) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("""
                    Wybierz metrykę [Domyślnie euklidesowa]:
                    1. Euklidesowa.
                    2. Miejska.
                    3. Czebyszewa""");

            Metric metrik = switch (scanner.nextLine()) {
                case "1", "" -> new EuclideanMetric();
                case "2" -> new ManhattanMetric();
                case "3" -> new ChebyshevMetric();
                default -> throw new IllegalStateException("Nieprawidłowa wartość metryki.");
            };

            System.out.println("Podaj wartość k: [Domyślnie 5]");
            String k = scanner.nextLine();
            try {
                if (k.isEmpty()) {
                    k = "5";
                } else {
                    int val = Integer.parseInt(k);
                    if (val < 0) {
                        throw new IllegalStateException("Nieprawidłowa wartość k");
                    }
                }
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Nieprawidłowa wartość k");
            }

            System.out.println("Podaj jaki procent artykułów ma zostać wektorami uczącymi [Domyślnie 50%]:");
            String trainingSetPercentage = scanner.nextLine();
            try {
                if (trainingSetPercentage.isEmpty()) {
                    trainingSetPercentage = "50";
                } else {
                    float val = Float.parseFloat(trainingSetPercentage);
                    if (val < 0 || val > 100) {
                        throw new IllegalStateException("Nieprawidłowa wartość procentu zbioru uczącego");
                    }
                }
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Nieprawidłowa wartość procentu zbioru uczącego");
            }

            System.out.println("""
                    Podaj po przecinku numery cech które mają zostać wykorzystane [Domyślnie wszystkie]:
                     1. Kraj dla którego słowo kluczowe ze słownika walut wystąpi jako pierwsze.
                     2. Kraj dla które nazwisko znanej osoby ze słownika znanych osób wystąpi najwięcej razy.
                     3  Kraj dla którego miasto ze słownika miast wystąpi jako pierwsze.
                     4. Kraj dla którego nazwa firmy ze słownika firm wystąpi jako pierwsza.
                     5. Kraj z którego występuje najwięcej miast słownika miast (6 wartości).
                     6. Kraj z którego występuje najwięcej nazwisk ze słownika sławnych ludzi.
                     7. Kraj z którego występuje najwięcej słów kluczowych ze słownika walut.
                     8. Liczba słów w tekście.
                     9. Pierwsza występująca jednostka miary
                    10. Liczba jednostek występujących dla systemu metrycznego i imperialnego (2 wartości).""");
            String features = scanner.nextLine();
            if (features.isEmpty()) {
                features = "1,2,3,4,5,6,7,8,9,10";
            } else {
                if (!features.matches("^(10|[1-9])(,(10|[1-9]))*$")) {
                    throw new IllegalStateException("Nieprawidłowa wartość procentu zbioru uczącego");
                }
            }

            FeatureExtractorConfig guiExtractorConfig = ImmutableFeatureExtractorConfig.copyOf(config.featureExtractorConfig())
                    .withFeatures(Arrays.stream(features.split(",")).map(Integer::parseInt).toList());

            config = ImmutableAppConfig.copyOf(config)
                    .withMetric(metrik)
                    .withNeighbors(Integer.parseInt(k))
                    .withPercentageOfTheTrainingSet(Float.parseFloat(trainingSetPercentage) / 100)
                    .withFeatureExtractorConfig(guiExtractorConfig);
        }

        LOG.debug("Config: {}", config);

        ArticleReader reader = new ArticleReader(config.readerConfig());
        FeatureExtractor featureExtractor = new FeatureExtractor(config.featureExtractorConfig());

        List<Article> articles = reader.getArticles();
        List<FeatureVector> featureVectors = featureExtractor.extractFeatures(articles);
        featureExtractor.normaliseFeatures(featureVectors);

        List<FeatureVector> trainingVectors = featureVectors.subList(0, (int) (config.percentageOfTheTrainingSet() * featureVectors.size()));
        List<FeatureVector> testVectors = featureVectors.subList(trainingVectors.size(), featureVectors.size());
        Metric metric = config.metric();
        ConfusionMatrix confusionMatrix = new ConfusionMatrix(config.readerConfig().places().size());


        int neighbors = config.neighbors();
        testVectors.parallelStream().forEach(vector -> {
            Country predictedCountry = KNN.classify(neighbors, vector, trainingVectors, metric);
            confusionMatrix.add(vector.getCountry(), predictedCountry);
        });

        List<String[]> csvData = new ArrayList<>();

        double accuracy = ClassificationQuality.calculateAccuracy(confusionMatrix);
        csvData.add(new String[]{"Accuracy", Double.toString(accuracy)});
        LOG.info("Accuracy – dla całego zbioru dokumentów:  {}", accuracy);

        double precisionForAll = ClassificationQuality.calculatePrecision(confusionMatrix);
        csvData.add(new String[]{"Precision", Double.toString(precisionForAll)});
        LOG.info("Precision – dla całego zbioru dokumentów: {}", precisionForAll);

        for (Country c : config.readerConfig().places()) {
            double precision = ClassificationQuality.calculatePrecisionForCountry(confusionMatrix, c);
            csvData.add(new String[]{String.format("Precision %s", StringUtils.capitalize(c.getCountryString())), Double.toString(precision)});
            LOG.info("Precision – dla zbioru dokumentów z kraju {} wynosi: {}", c.getCountryString(), precision);
        }


        double recallForAll = ClassificationQuality.calculateRecall(confusionMatrix);
        csvData.add(new String[]{"Recall", Double.toString(recallForAll)});
        LOG.info("Recall – dla całego zbioru dokumentów oraz dla wybranych klas: {}", recallForAll);


        for (Country c : config.readerConfig().places()) {
            double recall = ClassificationQuality.calculatePrecisionForCountry(confusionMatrix, c);
            csvData.add(new String[]{String.format("Recall %s", StringUtils.capitalize(c.getCountryString())), Double.toString(recall)});
            LOG.info("Recall – dla zbioru dokumentów z kraju {} wynosi: {}", c.getCountryString(), recall);
        }


        for (Country c : config.readerConfig().places()) {
            double f1 = ClassificationQuality.calculateF1ForCountry(confusionMatrix, c);
            csvData.add(new String[]{String.format("F1 %s", StringUtils.capitalize(c.getCountryString())), Double.toString(f1)});
            LOG.info("Miara F1 – dla zbioru dokumentów z kraju {} wynosi: {}", c.getCountryString(), f1);
        }
        double f1ForAll = ClassificationQuality.calculateF1(confusionMatrix);
        csvData.add(new String[]{"F1", Double.toString(f1ForAll)});
        LOG.info("Miara F1 – dla całego zbioru dokumentów oraz dla wybranych klas: {}", f1ForAll);

        long stopTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stopTime - startTime) / 1000f + " s");

        File file = new File(config.csvDir());
        file.getParentFile().mkdirs();
        try (CSVWriter writer = new CSVWriter(new FileWriter(file), ';', CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            writer.writeAll(csvData, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}