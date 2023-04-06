package pl.ksr.model;

public class TextFeature implements Feature {

    private String value;

    public TextFeature(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
