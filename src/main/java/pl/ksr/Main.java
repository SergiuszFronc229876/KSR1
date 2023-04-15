package pl.ksr;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksr.extractor.FeatureExtractor;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.typesafe.config.ConfigFactory.load;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        AppConfig configuration = AppConfig.fromRootConfig(load());
        ArticleReader reader = new ArticleReader(configuration.readerConfig());
        FeatureExtractor featureExtractor = new FeatureExtractor(configuration.featureExtractorConfig());

        List<Article> articles = reader.getArticles();
        List<FeatureVector> featureVectors = featureExtractor.extractFeatures(articles);
        featureExtractor.normaliseFeatures(featureVectors);

        List<FeatureVector> trainingVectors = featureVectors.subList(0, (int) (configuration.percentageOfTheTrainingSet() * featureVectors.size()));
        List<FeatureVector> testVectors = featureVectors.subList(trainingVectors.size(), featureVectors.size());
        Metric metric = configuration.metric();
        ConfusionMatrix confusionMatrix = new ConfusionMatrix(configuration.readerConfig().places().size());


        testVectors.parallelStream().forEach(vector -> {
            Country predictedCountry = KNN.classify(configuration.neighbors(), vector, trainingVectors, metric);
            confusionMatrix.add(vector.getCountry(), predictedCountry);
        });

        String[] header = {"Accuracy", "Precision", "PrecisionUsa", "PrecisionJapan", "PrecisionWestGermany", "PrecisionCanada", "PrecisionUK", "PrecisionFrance",
                "Recall", "RecallUsa", "RecallJapan", "RecallWestGermany", "RecallCanada", "RecallUK", "RecallFrance",
                "F1", "F1Usa", "F1Japan", "F1WestGermany", "F1Canada", "F1UK", "F1France"};
        List<String[]> csvData = new ArrayList<>();
        csvData.add(header);
        List<String> toSave = new ArrayList<>();

        DecimalFormat df = new DecimalFormat("0.00");

        double accuracy = ClassificationQuality.calculateAccuracy(confusionMatrix);
        toSave.add(Double.toString(accuracy));
        LOG.info("Accuracy – dla całego zbioru dokumentów:  {}", accuracy);

        double precisionForAll = ClassificationQuality.calculatePrecision(confusionMatrix);
        toSave.add(Double.toString(precisionForAll));

        LOG.info("Precision – dla całego zbioru dokumentów: {}", precisionForAll);

        for (Country c : configuration.readerConfig().places()) {
            double precision = ClassificationQuality.calculatePrecisionForCountry(confusionMatrix, c);
            toSave.add(Double.toString(precision));
            LOG.info("Precision – dla zbioru dokumentów z kraju {} wynosi: {}", c.getCountryString(), precision);
        }


        double recallForAll = ClassificationQuality.calculateRecall(confusionMatrix);
        toSave.add(Double.toString(recallForAll));
        LOG.info("Recall – dla całego zbioru dokumentów oraz dla wybranych klas: {}", recallForAll);


        for (Country c : configuration.readerConfig().places()) {
            double recall = ClassificationQuality.calculatePrecisionForCountry(confusionMatrix, c);
            toSave.add(Double.toString(recall));
            LOG.info("Recall – dla zbioru dokumentów z kraju {} wynosi: {}", c.getCountryString(), recall);
        }


        for (Country c : configuration.readerConfig().places()) {
            double f1 = ClassificationQuality.calculateF1ForCountry(confusionMatrix, c);
            toSave.add(Double.toString(f1));
            LOG.info("Miara F1 – dla zbioru dokumentów z kraju {} wynosi: {}", c.getCountryString(), f1);
        }
        double f1ForAll = ClassificationQuality.calculateF1(confusionMatrix);
        toSave.add(Double.toString(f1ForAll));
        LOG.info("Miara F1 – dla całego zbioru dokumentów oraz dla wybranych klas: {}", f1ForAll);

        long stopTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stopTime - startTime) / 1000 + " s");

        File file = new File(configuration.csvDir());
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            csvData.add(toSave.toArray(String[]::new));
            writer.writeAll(csvData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}