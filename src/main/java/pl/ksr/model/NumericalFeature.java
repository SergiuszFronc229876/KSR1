package pl.ksr.model;

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
}
