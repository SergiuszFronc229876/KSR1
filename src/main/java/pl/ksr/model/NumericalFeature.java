package pl.ksr.model;

public class NumericalFeature implements Feature {

    private final float value;

    public NumericalFeature(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
