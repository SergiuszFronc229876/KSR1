package pl.ksr;

import pl.ksr.model.Article;
import pl.ksr.reader.ArticleReader;

import java.util.List;

import static com.typesafe.config.ConfigFactory.load;

public class Main {

    public static void main(String[] args) {
        AppConfig configuration = AppConfig.fromRootConfig(load());
        ArticleReader reader = new ArticleReader(configuration.readerConfig());
        List<Article> articles = reader.getArticles();
        System.out.println(articles.get(0));
    }

}