package pl.ksr;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        ArticleReader reader = new ArticleReader();
        List<Article> articles = reader.getArticles();
        articles.forEach(article -> System.out.println(article.getTitle()));
        System.out.println("Articles read: " + articles.size());
    }
}