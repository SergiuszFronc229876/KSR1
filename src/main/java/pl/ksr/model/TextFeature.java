package pl.ksr.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TextFeature implements Feature {

    private String value;

    public TextFeature(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("value", value)
                .toString();
    }

    public void setValue(String value) {
        this.value = value;
    }
}
