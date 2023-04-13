package pl.ksr.metric;

import java.util.Arrays;

public enum BasicMetrics {
    EUCLIDEAN("euclidean", new EuclideanMetric()),
    CHEBYSHEV("chebyshev", new ChebyshevMetric()),
    MANHATTAN("manhattan", new ManhattanMetric());
    private final String name;
    private final Metric metric;

    BasicMetrics(String name, Metric metric) {
        this.name = name;
        this.metric = metric;
    }

    public static Metric getMetric(String metricName) {
        return Arrays.stream(BasicMetrics.values())
                .filter(m -> m.name.equals(metricName))
                .map(m -> m.metric)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Couldn't resolve %s metricName(Type: euclidean, chebyshev or manhattan)", metricName)));
    }
}
