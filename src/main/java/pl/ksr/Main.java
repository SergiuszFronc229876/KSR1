package pl.ksr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksr.extractor.FeatureExtractor;
import pl.ksr.metrics.EuclideanMetric;
import pl.ksr.metrics.Metric;
import pl.ksr.model.Article;
import pl.ksr.model.FeatureVector;
import pl.ksr.reader.ArticleReader;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.typesafe.config.ConfigFactory.load;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        AppConfig configuration = AppConfig.fromRootConfig(load());
        ArticleReader reader = new ArticleReader(configuration.readerConfig());
        List<Article> articles = reader.getArticles();
        FeatureExtractor featureExtractor = new FeatureExtractor(configuration.featureExtractorConfig());
        List<FeatureVector> featureVectors = featureExtractor.extractFeatures(articles);
        featureExtractor.normaliseFeatures(featureVectors);


        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals("usa")).toList().size());
        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals("uk")).toList().size());
        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals("canada")).toList().size());
        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals("west-germany")).toList().size());
        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals("japan")).toList().size());
        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals("france")).toList().size());


        List<FeatureVector> teachingVectors = featureVectors.subList(0, (int) (featureVectors.size() * 0.5));
        List<FeatureVector> toClassification = featureVectors.subList(teachingVectors.size(), featureVectors.size());
        Metric metric = new EuclideanMetric();


        AtomicInteger good = new AtomicInteger(0);
        AtomicInteger count = new AtomicInteger(0);

        toClassification.parallelStream().forEach(vector -> {
            count.incrementAndGet();
            LOGGER.info("Classification of vector number: {}", count);
            String classify = KNN.classify(5, vector, teachingVectors, metric);
            if (vector.getCountry().equals(classify)) {
                good.incrementAndGet();
            }
        });

        System.out.println("Wynik: " + ((float) good.get() / toClassification.size()));
        long stopTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stopTime - startTime) / 1000 + " ms");
    }
}