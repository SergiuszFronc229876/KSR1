package pl.ksr.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksr.model.Article;
import pl.ksr.model.ImmutableArticle;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleReader.class);

    private final FileLoader fileLoader;
    private final ArticleReaderConfig config;

    public ArticleReader(ArticleReaderConfig config) {
        this.config = config;
        this.fileLoader = new FileLoader(config.articlesDir(), config.stopWords());

    }

    public List<Article> getArticles() {
        List<String> contentOfArticles = fileLoader.readFiles();
        return getArticles(contentOfArticles);
    }

    private List<Article> getArticles(List<String> contentOfArticles) {
        List<Article> articles = new ArrayList<>();
        String regex = "<REUTERS(.*?)</REUTERS>";
        Pattern pattern = Pattern.compile(regex);

        for (String contentOfArticle : contentOfArticles) {
            Matcher matcher = pattern.matcher(contentOfArticle);
            while (matcher.find()) {
                String articleInString = matcher.group(1);
                List<String> places = getPlaces(articleInString);

                if (placesTagIsValid(places)) {
                    articles.add(buildArticle(articleInString, places));
                }
            }
        }
        LOGGER.info("Articles read: {}", articles.size());
        return articles;
    }

    private String getBody(String contentOfArticle) {
        String regex = "<BODY>(.*?)</BODY>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contentOfArticle);
        if (matcher.find()) {
            return matcher.group(1);
        } else return "";
    }

    private String getTitle(String contentOfArticle) {
        String regex = "<TITLE>(.*?)</TITLE>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contentOfArticle);
        if (matcher.find()) {
            return matcher.group(1);
        } else return "";
    }

    private List<String> getPlaces(String contentOfArticle) {
        List<String> places = new ArrayList<>(List.of());
        String regex = "<PLACES>(.*?)</PLACES>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contentOfArticle);
        if (matcher.find()) {
            String placesTagContent = matcher.group(1);
            regex = "<D>(.*?)</D>";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(placesTagContent);
            while (matcher.find()) {
                places.add(matcher.group(1));
            }
        }
        return places;
    }

    private boolean placesTagIsValid(List<String> articlePlaces) {
        if (articlePlaces.size() == 1) {
            return config.places().contains(articlePlaces.get(0));
        }
        return false;
    }

    private Article buildArticle(String articleInString, List<String> places) {
        return ImmutableArticle.builder()
                .title(getTitle(articleInString))
                .addAllPlaces(places)
                .body(getBody(articleInString))
                .build();
    }

}