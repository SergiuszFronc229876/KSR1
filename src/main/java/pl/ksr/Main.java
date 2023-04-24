package pl.ksr;

import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.typesafe.config.ConfigFactory.load;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        AppConfig config = AppConfig.fromRootConfig(load());
        if (config.guiMode()) {

            System.out.println("""
                    ┌──────────────────────────────────────────────────────────────────────┐
                    │                                                                      │
                    │   Program do klasyfikacji zbioru dokumentów tekstowych metodą k-N.   │
                    │   Autorzy:                                                           │
                    │     Sergiusz Fronc 229876                                            │
                    │     Patryk Nowacki 229970                                            │
                    │                                                                      │
                    └──────────────────────────────────────────────────────────────────────┘
                    """);

            Scanner scanner = new Scanner(System.in);

             // PARAMETR K
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

            // ZBIÓR UCZĄCY
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

            // CECHY
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

            // METRYKA
            System.out.println("""
                    Wybierz metrykę [Domyślnie euklidesowa]:
                    1. Euklidesowa.
                    2. Miejska.
                    3. Czebyszewa""");

            Metric _metric = switch (scanner.nextLine()) {
                case "1", "" -> new EuclideanMetric();
                case "2" -> new ManhattanMetric();
                case "3" -> new ChebyshevMetric();
                default -> throw new IllegalStateException("Nieprawidłowa wartość metryki.");
            };

            FeatureExtractorConfig guiExtractorConfig = ImmutableFeatureExtractorConfig.copyOf(config.featureExtractorConfig())
                    .withFeatures(Arrays.stream(features.split(",")).map(Integer::parseInt).toList());

            config = ImmutableAppConfig.copyOf(config)
                    .withMetric(_metric)
                    .withNeighbors(Integer.parseInt(k))
                    .withPercentageOfTheTrainingSet(Float.parseFloat(trainingSetPercentage) / 100)
                    .withFeatureExtractorConfig(guiExtractorConfig);
        }

        LOG.debug("Config: {}", config);

        Thread thread = new Thread(() -> {
            int i = 0;
            System.out.print("Oczekiwanie na skończenie wczytania artykułów");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500);
                    i++;
                    System.out.print(".");
                    if (i % 4 == 0) {
                        System.out.print("\b\b\b\b    \b\b\b\b");
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.start();

        long startTime = System.currentTimeMillis();
        ArticleReader reader = new ArticleReader(config.readerConfig());
        FeatureExtractor featureExtractor = new FeatureExtractor(config.featureExtractorConfig());

        List<Article> articles = reader.getArticles();
        Map<String, Long> countedArticles = articles.stream().collect(Collectors.groupingBy(Article::getPlace, Collectors.counting()));

        thread.interrupt();
        System.out.println("\n");
        LOG.info("Liczba artykułów z USA: {}", countedArticles.get("usa"));
        LOG.info("Liczba artykułów z Japonii: {}", countedArticles.get("japan"));
        LOG.info("Liczba artykułów z Zachodnich Niemiec: {}", countedArticles.get("west-germany"));
        LOG.info("Liczba artykułów z Kanady: {}", countedArticles.get("canada"));
        LOG.info("Liczba artykułów z UK: {}", countedArticles.get("uk"));
        LOG.info("Liczba artykułów z Francji: {}", countedArticles.get("france"));
        System.out.println("\n");

        thread = new Thread(() -> {
            int i = 0;
            System.out.print("Oczekiwanie na skończenie klasyfikacji");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500);
                    i++;
                    System.out.print(".");
                    if (i % 4 == 0) {
                        System.out.print("\b\b\b\b    \b\b\b\b");
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.start();

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

        thread.interrupt();

        List<String[]> csvData = new ArrayList<>();
        System.out.println("\n");

        drawConfusionMatrix(confusionMatrix);

        // ACCURACY
        double accuracy = ClassificationQuality.calculateAccuracy(confusionMatrix);
        csvData.add(new String[]{"Accuracy", Double.toString(accuracy)});
        LOG.info("Wartość Accuracy dla wszystkich klas: {}", accuracy);
        System.out.println("--------------------------------------------");

        // PRECISION
        LOG.info("Wartość Precision dla klas:");
        for (Country c : config.readerConfig().places()) {
            double precision = ClassificationQuality.calculatePrecisionForCountry(confusionMatrix, c);
            csvData.add(new String[]{String.format("Precision %s", StringUtils.capitalize(c.getCountryString())), Double.toString(precision)});
            LOG.info(" - {}: {}", c.getCountryString(), precision);
        }
        double precisionForAll = ClassificationQuality.calculatePrecision(confusionMatrix);
        csvData.add(new String[]{"Precision", Double.toString(precisionForAll)});
        LOG.info("Dla wszystkich krajów: {}", precisionForAll);
        LOG.info("--------------------------------------------");

        // RECALL
        LOG.info("Wartość Recall dla klas:");
        for (Country c : config.readerConfig().places()) {
            double recall = ClassificationQuality.calculateRecallForCountry(confusionMatrix, c);
            csvData.add(new String[]{String.format("Recall %s", StringUtils.capitalize(c.getCountryString())), Double.toString(recall)});
            LOG.info(" - {}: {}", c.getCountryString(), recall);
        }
        double recallForAll = ClassificationQuality.calculateRecall(confusionMatrix);
        csvData.add(new String[]{"Recall", Double.toString(recallForAll)});
        LOG.info("Dla wszystkich klas: {}", recallForAll);
        LOG.info("--------------------------------------------");

        // F1
        LOG.info("Wartość F1 dla klas:");
        for (Country c : config.readerConfig().places()) {
            double f1 = ClassificationQuality.calculateF1ForCountry(confusionMatrix, c);
            csvData.add(new String[]{String.format("F1 %s", StringUtils.capitalize(c.getCountryString())), Double.toString(f1)});
            LOG.info(" - {}: {}", c.getCountryString(), f1);
        }
        double f1ForAll = ClassificationQuality.calculateF1(confusionMatrix);
        csvData.add(new String[]{"F1", Double.toString(f1ForAll)});
        LOG.info("Dla wszystkich klas: {}", f1ForAll);


        long stopTime = System.currentTimeMillis();
        System.out.println("\nCzas przetwarzania: " + (stopTime - startTime) / 1000f + " s");

        File file = new File(config.csvDir());
        file.getParentFile().mkdirs();
        try (CSVWriter writer = new CSVWriter(new FileWriter(file), ';', CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            writer.writeAll(csvData, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOG.debug("Wyniki zapisane do: {}", file.getAbsolutePath());
    }

    private static void drawConfusionMatrix(ConfusionMatrix matrix) {
        int numCountries = Country.values().length;

        // Determine column width based on the longest country code plus 2 characters
        int colWidth = 5;
        for (Country country : Country.values()) {
            colWidth = Math.max(colWidth, country.getCode().length() + 2);
        }

        // Print confusion matrix top border
        System.out.print("┌" + "─".repeat(colWidth));
        for (int i = 0; i < numCountries; i++) {
            System.out.print("┬" + "─".repeat(colWidth));
        }
        System.out.println("┐");

        // Print confusion matrix headers
        System.out.format("│%-" + colWidth + "s", " ");
        for (int i = 0; i < numCountries; i++) {
            System.out.format("│%-" + colWidth + "s", " " + Country.values()[i].getCode() + " ");
        }
        System.out.println("│");

        // Print confusion matrix separator
        System.out.print("├" + "─".repeat(colWidth));
        for (int i = 0; i < numCountries; i++) {
            System.out.print("┼" + "─".repeat(colWidth));
        }
        System.out.println("┤");

        // Print confusion matrix data
        for (int row = 0; row < matrix.getMatrix().length; row++) {
            System.out.format("│%-" + colWidth + "s", " " + Country.values()[row].getCode() + " ");
            for (int col = 0; col < matrix.getMatrix()[row].length; col++) {
                System.out.format("│%" + (colWidth - 1) + "d ", matrix.getMatrix()[row][col]);
            }
            System.out.println("│");
            if (row < matrix.getMatrix().length - 1) {
                System.out.print("├" + "─".repeat(colWidth));
                for (int i = 0; i < numCountries; i++) {
                    System.out.print("┼" + "─".repeat(colWidth));
                }
                System.out.println("┤");
            }
        }

        // Print confusion matrix bottom border
        System.out.print("└" + "─".repeat(colWidth));
        for (int i = 0; i < numCountries; i++) {
            System.out.print("┴" + "─".repeat(colWidth));
        }
        System.out.println("┘");
    }
}