package pl.ksr.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class NumericalFeature implements Feature {

    private float value;

    public NumericalFeature(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("value", value)
                .toString();
    }
}
