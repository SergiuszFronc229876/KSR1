package pl.ksr.metric;

import pl.ksr.model.Feature;
import pl.ksr.model.FeatureVector;
import pl.ksr.model.NumericalFeature;
import pl.ksr.model.TextFeature;

public class ManhattanMetric implements Metric {
    @Override
    public float calculateDistance(FeatureVector vector1, FeatureVector vector2) {
        float distance = 0f;

        for (int i = 0; i < vector1.size(); i++) {
            Feature feature1 = vector1.getFeature(i);
            Feature feature2 = vector1.getFeature(i);

            if (feature1.getClass() == NumericalFeature.class) {
                float value = ((NumericalFeature) feature1).getValue() - ((NumericalFeature) feature2).getValue();
                distance += Math.abs(value);
            } else {
                distance += 1 - trigram(((TextFeature) feature1).getValue(), ((TextFeature) feature2).getValue());
            }
        }

        return distance;
    }
}
