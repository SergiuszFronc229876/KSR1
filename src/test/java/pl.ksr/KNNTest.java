package pl.ksr;

import org.junit.jupiter.api.Test;
import pl.ksr.metric.EuclideanMetric;
import pl.ksr.metric.Metric;
import pl.ksr.model.Country;
import pl.ksr.model.FeatureVector;
import pl.ksr.model.NumericalFeature;
import pl.ksr.model.TextFeature;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KNNTest {
    @Test
    void testClassifyForKEquals1() {
        // Create teaching vectors
        List<FeatureVector> teachingVectors = List.of(
                new FeatureVector(
                        List.of(
                                new NumericalFeature(0.23f),
                                new TextFeature("Tokyo"),
                                new NumericalFeature(8.9f)),
                        Country.Japan),
                new FeatureVector(
                        List.of(
                                new NumericalFeature(2.1f),
                                new TextFeature("New York"),
                                new NumericalFeature(20.4f)),
                        Country.USA),
                new FeatureVector(
                        List.of(
                                new NumericalFeature(0.85f),
                                new TextFeature("Paris"),
                                new NumericalFeature(6.3f)),
                        Country.France));

        // Create the test vector
        FeatureVector test = new FeatureVector(
                List.of(
                        new NumericalFeature(1.2f),
                        new TextFeature("Paris"),
                        new NumericalFeature(7.8f)), null);

        Metric metric = new EuclideanMetric();

        // Classify the test vector
        Country classification = KNN.classify(1, test, teachingVectors, metric);

        // Check if the classification is correct
        assertEquals(Country.France, classification);
    }

    @Test
    void testClassifyForKEquals3() {
        // create the teaching set of vectors
        List<FeatureVector> teachingSet = List.of(
                new FeatureVector(List.of(
                        new NumericalFeature(0.25f),
                        new NumericalFeature(0.75f),
                        new TextFeature("sushi"),
                        new NumericalFeature(0.1f)
                ), Country.Japan),
                new FeatureVector(List.of(
                        new NumericalFeature(0.5f),
                        new NumericalFeature(0.4f),
                        new TextFeature("sausage"),
                        new NumericalFeature(0.2f)
                ), Country.West_Germany),
                new FeatureVector(List.of(
                        new NumericalFeature(0.8f),
                        new NumericalFeature(0.1f),
                        new TextFeature("hamburger"),
                        new NumericalFeature(0.3f)
                ), Country.USA),
                new FeatureVector(List.of(
                        new NumericalFeature(0.6f),
                        new NumericalFeature(0.6f),
                        new TextFeature("tea"),
                        new NumericalFeature(0.4f)
                ), Country.UK),
                new FeatureVector(List.of(
                        new NumericalFeature(0.9f),
                        new NumericalFeature(0.2f),
                        new TextFeature("maple syrup"),
                        new NumericalFeature(0.5f)
                ), Country.Canada),
                new FeatureVector(List.of(
                        new NumericalFeature(0.7f),
                        new NumericalFeature(0.3f),
                        new TextFeature("croissant"),
                        new NumericalFeature(0.6f)
                ), Country.France),
                new FeatureVector(List.of(
                        new NumericalFeature(0.3f),
                        new NumericalFeature(0.9f),
                        new TextFeature("hot dog"),
                        new NumericalFeature(0.7f)
                ), Country.USA),
                new FeatureVector(List.of(
                        new NumericalFeature(0.4f),
                        new NumericalFeature(0.7f),
                        new TextFeature("fish and chips"),
                        new NumericalFeature(0.8f)
                ), Country.UK),
                new FeatureVector(List.of(
                        new NumericalFeature(0.2f),
                        new NumericalFeature(0.8f),
                        new TextFeature("ramen"),
                        new NumericalFeature(0.9f)
                ), Country.Japan)
        );

        // create the test vector
        FeatureVector testVector = new FeatureVector(List.of(
                new NumericalFeature(0.4f),
                new NumericalFeature(0.5f),
                new TextFeature("sushi"),
                new NumericalFeature(0.3f)
        ), null);

        // create the metric object with ngram size of 3
        Metric metric = new EuclideanMetric();

        // classify the test vector using k=3
        Country result = KNN.classify(3, testVector, teachingSet, metric);

        // check that the result matches the expected class
        assertEquals(Country.Japan, result);
    }
}
