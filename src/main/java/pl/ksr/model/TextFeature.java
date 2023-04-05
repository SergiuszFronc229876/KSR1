package pl.ksr.model;

public class TextFeature implements Feature {

    private final String value;

    public TextFeature(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
