package pl.ksr;

import pl.ksr.extractor.FeatureExtractor;
import pl.ksr.model.Article;
import pl.ksr.reader.ArticleReader;

import java.util.List;

import static com.typesafe.config.ConfigFactory.load;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        AppConfig configuration = AppConfig.fromRootConfig(load());
        ArticleReader reader = new ArticleReader(configuration.readerConfig());
        List<Article> articles = reader.getArticles();
        FeatureExtractor featureExtractor = new FeatureExtractor(configuration.featureExtractorConfig());
        featureExtractor.extractFeatures(articles);
        long stopTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stopTime - startTime) / 1000 + " ms");
    }
}