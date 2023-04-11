package pl.ksr.metric;

import pl.ksr.model.Feature;
import pl.ksr.model.FeatureVector;
import pl.ksr.model.NumericalFeature;
import pl.ksr.model.TextFeature;

public class EuclideanMetric implements Metric {
    @Override
    public float calculateDistance(FeatureVector vector1, FeatureVector vector2) {
        float distance = 0f;

        for (int i = 0; i < vector1.size(); i++) {
            Feature feature1 = vector1.getFeature(i);
            Feature feature2 = vector2.getFeature(i);

            float v;
            if (feature1.getClass() == NumericalFeature.class) {
                v = ((NumericalFeature) feature1).getValue() - ((NumericalFeature) feature2).getValue();
            } else {
                v = 1 - trigram(((TextFeature) feature1).getValue(), ((TextFeature) feature2).getValue());
            }
            distance += v * v;
        }

        return (float) Math.sqrt(distance);
    }
}
