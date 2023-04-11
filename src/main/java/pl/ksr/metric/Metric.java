package pl.ksr.metric;

import pl.ksr.model.FeatureVector;

import java.util.ArrayList;
import java.util.List;

public interface Metric {

    float calculateDistance(FeatureVector vector1, FeatureVector vector2);

    default float trigram(String text1, String text2) {
        if (text1.length() >= 3 && text2.length() >= 3) {

            List<String> trigrams = new ArrayList<>();

            for (int i = 0; i < text1.length() - 2; i++) {
                trigrams.add(text1.toLowerCase().substring(i, i + 3));
            }
            int result = 0;
            for (int i = 0; i < text2.length() - 2; i++) {
                String trigram = text2.toLowerCase().substring(i, i + 3);
                if (trigrams.contains(trigram)) {
                    result += 1;
                }
            }

            return (float) (result / trigrams.size());
        } else {
            if (text1.equals(text2)) return 1f;
            else return 0f;
        }
    }
}
