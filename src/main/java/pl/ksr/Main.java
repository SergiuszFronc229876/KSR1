package pl.ksr;

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


        LOG.info("Accuracy – dla całego zbioru dokumentów:  {}", ClassificationQuality.calculateAccuracy(confusionMatrix));

        LOG.info("Precision – dla całego zbioru dokumentów: {}", ClassificationQuality.calculatePrecision(confusionMatrix));
        for (Country c : configuration.readerConfig().places()) {
            LOG.info("Precision – dla zbioru dokumentów z kraju {} wynosi: {}", c.getCountryString(), ClassificationQuality.calculatePrecisionForCountry(confusionMatrix, c));
        }

        LOG.info("Recall – dla całego zbioru dokumentów oraz dla wybranych klas: {}", ClassificationQuality.calculateRecall(confusionMatrix));
        for (Country c : configuration.readerConfig().places()) {
            LOG.info("Recall – dla zbioru dokumentów z kraju {} wynosi: {}", c.getCountryString(), ClassificationQuality.calculatePrecisionForCountry(confusionMatrix, c));
        }

        for (Country c : configuration.readerConfig().places()) {
            LOG.info("Miara F1 – dla zbioru dokumentów z kraju {} wynosi: {}", c.getCountryString(), ClassificationQuality.calculateF1ForCountry(confusionMatrix, c));
        }
        LOG.info("Miara F1 – dla całego zbioru dokumentów oraz dla wybranych klas: {}", ClassificationQuality.calculateF1(confusionMatrix));

        long stopTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stopTime - startTime) / 1000 + " s");
    }
}