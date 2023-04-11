package pl.ksr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksr.extractor.FeatureExtractor;
import pl.ksr.metric.EuclideanMetric;
import pl.ksr.metric.Metric;
import pl.ksr.model.Article;
import pl.ksr.model.Country;
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


        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals(Country.USA)).toList().size());
        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals(Country.UK)).toList().size());
        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals(Country.Canada)).toList().size());
        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals(Country.West_Germany)).toList().size());
        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals(Country.Japan)).toList().size());
        System.out.println(featureVectors.stream().filter(f -> f.getCountry().equals(Country.France)).toList().size());


        List<FeatureVector> teachingVectors = featureVectors.subList(0, (int) (featureVectors.size() * 0.5));
        List<FeatureVector> toClassification = featureVectors.subList(teachingVectors.size(), featureVectors.size());
        Metric metric = new EuclideanMetric();


        AtomicInteger good = new AtomicInteger(0);
        AtomicInteger count = new AtomicInteger(0);

        toClassification.parallelStream().forEach(vector -> {
            count.incrementAndGet();
            LOGGER.info("Classification of vector number: {}", count);
            Country classifiedClass = KNN.classify(5, vector, teachingVectors, metric);
            if (vector.getCountry().equals(classifiedClass)) {
                good.incrementAndGet();
            }
        });

        System.out.println("Wynik: " + ((float) good.get() / toClassification.size()));
        long stopTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stopTime - startTime) / 1000 + " ms");
    }
}