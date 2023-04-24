package pl.ksr.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class FeatureVector implements Iterable<Feature> {

    private final List<Feature> featureList;
    private final Country country;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FeatureVector vector = (FeatureVector) o;

        return new EqualsBuilder().append(featureList, vector.featureList).append(country, vector.country).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(featureList).append(country).toHashCode();
    }

    public FeatureVector(List<Feature> featureList, Country country) {
        this.featureList = featureList;
        this.country = country;
    }

    public Feature getFeature(int index) {
        return featureList.get(index);
    }

    public Country getCountry() {
        return country;
    }

    public int size() {
        return featureList.size();
    }

    public List<NumericalFeature> getNumericalFeatures() {
        return featureList.stream()
                .filter(feature -> feature.getClass() == NumericalFeature.class)
                .map(NumericalFeature.class::cast)
                .toList();
    }

    public List<Feature> getFeatureList() {
        return featureList;
    }

    public List<TextFeature> getTextFeatures() {
        return featureList.stream()
                .filter(feature -> feature.getClass() == TextFeature.class)
                .map(TextFeature.class::cast)
                .toList();
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
