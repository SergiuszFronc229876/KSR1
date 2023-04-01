package pl.ksr.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksr.model.Article;
import pl.ksr.model.ImmutableArticle;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArticleReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleReader.class);

    private final FileLoader fileLoader = new FileLoader();
    private final ArticleReaderConfig config;

    public ArticleReader(ArticleReaderConfig config) {
        this.config = config;

    }

    public List<Article> getArticles() {
        List<String> contentOfArticles = fileLoader.readFiles(config.articlesDir());
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
                    articles.add(buildArticle(articleInString, places.get(0)));
                }
            }
        }
        LOGGER.info("Articles read: {}", articles.size());
        return articles;
    }

    private List<String> getWordList(String contentOfArticle, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contentOfArticle);
        if (matcher.find()) {
            return Stream.of(matcher.group(1).split(" "))
                    .collect(Collectors.toCollection(ArrayList<String>::new));
        } else return List.of();
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

    private Article buildArticle(String articleInString, String place) {
        List<String> wordsFromTitle = getWordList(articleInString, "<TITLE>(.*?)</TITLE>");
        List<String> wordsFromBody = getWordList(articleInString, "<BODY>(.*?)</BODY>");
        List<String> combined = Stream.concat(wordsFromTitle.stream(), wordsFromBody.stream())
                .collect(Collectors.toList());
        combined.removeAll(config.stopWords());
        combined.removeIf(String::isEmpty);
        return ImmutableArticle.builder()
                .place(place)
                .text(combined)
                .build();
    }
}