package pl.ksr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleReader {

    private final String dirPath;

    public ArticleReader() {
        this.dirPath = "src/main/resources/articles";
    }

    public ArticleReader(String dirPath) {
        this.dirPath = dirPath;
    }

    public List<String> readFiles() {
        List<String> articleList = new ArrayList<>();

        File directory = new File(dirPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    articleList.add(readFile(file.getAbsolutePath()));
                }
            }
        }
        return articleList;
    }

    private String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public List<Article> getArticles() {
        List<String> contentOfArticles = readFiles();
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
                    Article art = new Article(places, getBody(articleInString), getTitle(articleInString));
                    articles.add(art);
                }
            }
        }

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

    private boolean placesTagIsValid(List<String> places) {
        List<String> expectedCountries = new ArrayList<>(List.of("usa", "japan", "west-germany", "canada", "uk", "france"));
        if (places.isEmpty()) {
            return false;
        }
        if (places.size() > 1) return false;

        for (String expectedCountry : expectedCountries) {
            if (places.contains(expectedCountry)) {
                return true;
            }
        }
        return false;
    }
}