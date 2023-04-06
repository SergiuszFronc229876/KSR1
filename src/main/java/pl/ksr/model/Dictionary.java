package pl.ksr.model;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Dictionary {

    private final Map<String, List<String>> keyWordsMap;

    public Dictionary(Map<String, List<String>> keyWordsMap) {
        this.keyWordsMap = keyWordsMap;
    }

    public List<String> getValues(String key) {
        return keyWordsMap.get(key);
    }

    public List<String> getAllValues() {
        ArrayList<String> allValues = new ArrayList<>();
        keyWordsMap.values().forEach(allValues::addAll);
        return allValues;
    }

    public Set<Map.Entry<String, List<String>>> entrySet() {
        return keyWordsMap.entrySet();
    }

    public List<Pair<String, String>> getAllKeyWordsWithKeyValue() {
        List<Pair<String, String>> pairs = new ArrayList<>();
        for (String key : keyWordsMap.keySet()) {
            List<String> values = keyWordsMap.get(key);
            for (String value : values) {
                pairs.add(new ImmutablePair<>(key, value));
            }
        }
        return pairs;
    }
}
