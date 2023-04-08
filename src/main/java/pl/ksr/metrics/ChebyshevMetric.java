package pl.ksr.metrics;

import pl.ksr.model.Feature;
import pl.ksr.model.FeatureVector;
import pl.ksr.model.NumericalFeature;
import pl.ksr.model.TextFeature;

public class ChebyshevMetric implements Metric {
    @Override
    public float calculateDistance(FeatureVector vector1, FeatureVector vector2) {
        float maxValue = Float.MIN_VALUE;

        for (int i = 0; i < vector1.size(); i++) {
            Feature feature1 = vector1.getFeature(i);
            Feature feature2 = vector1.getFeature(i);

            float v;
            if (feature1.getClass() == NumericalFeature.class) {
                v = ((NumericalFeature) feature1).getValue() - ((NumericalFeature) feature2).getValue();
                v = Math.abs(v);
            } else {
                v = 1 - trigram(((TextFeature) feature1).getValue(), ((TextFeature) feature2).getValue());
            }
            if (v > maxValue) {
                maxValue = v;
            }
        }

        return maxValue;
    }
}
