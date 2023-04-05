package pl.ksr.model;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class FeatureVector implements Iterable<Feature> {

    private final List<Feature> featureList;
    private final String country;

    public FeatureVector(List<Feature> featureList, String country) {
        this.featureList = featureList;
        this.country = country;
    }

    public Feature getFeature(int index) {
        return featureList.get(index);
    }

    public String getCountry() {
        return country;
    }

    @Override
    public Iterator<Feature> iterator() {
        return featureList.iterator();
    }

    @Override
    public void forEach(Consumer<? super Feature> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Feature> spliterator() {
        return Iterable.super.spliterator();
    }
}