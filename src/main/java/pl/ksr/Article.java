package pl.ksr;

import java.util.List;

public class Article {
    private final List<String> places;
    private final String body;
    private final String title;

    public Article(List<String> places, String body, String title) {
        this.places = places;
        this.body = body;
        this.title = title;
    }

    public List<String> getPlaces() {
        return places;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}