package pl.ksr;

import pl.ksr.metric.Metric;
import pl.ksr.model.Article;
import pl.ksr.model.Country;
import pl.ksr.model.FeatureVector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class KNN {

    public static Country classify(int k, FeatureVector vector, List<FeatureVector> teachingVectors, Metric metric) {

        // Calculate distances between the input vector and all the vectors in the teaching list
        Map<FeatureVector, Float> distances = new LinkedHashMap<>();
        teachingVectors.forEach(f -> distances.put(f, null));

        teachingVectors.parallelStream().forEach(teachingVector -> {
            float distance = metric.calculateDistance(vector, teachingVector);
            distances.put(teachingVector, distance);
        });

        // Sort the classes by increasing distance to the input vector
        List<FeatureVector> sortedFeatures = new ArrayList<>(distances.keySet());
        sortedFeatures.sort((c1, c2) -> Float.compare(distances.get(c1), distances.get(c2)));

        // Count the k nearest neighbors for each class
        Map<Country, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < k && i < sortedFeatures.size(); i++) {
            Country country = sortedFeatures.get(i).getCountry();
            counts.put(country, counts.getOrDefault(country, 0) + 1);
        }

        // Find the class with the highest count
        Country result = null;
        int maxCount = 0;
        for (Map.Entry<Country, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > maxCount) {
                result = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return result;
    }
}
