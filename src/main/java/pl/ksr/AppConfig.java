package pl.ksr;

import com.typesafe.config.Config;
import org.immutables.value.Value;
import pl.ksr.extractor.FeatureExtractorConfig;
import pl.ksr.metric.BasicMetrics;
import pl.ksr.metric.Metric;
import pl.ksr.reader.ArticleReaderConfig;

@Value.Immutable
public interface AppConfig {

    static AppConfig fromRootConfig(Config config) {
        return ImmutableAppConfig.builder()
                .readerConfig(ArticleReaderConfig.fromRootConfig(config.getConfig("article-reader")))
                .featureExtractorConfig(FeatureExtractorConfig.fromRootConfig(config.getConfig("feature-extractor")))
                .percentageOfTheTrainingSet((float) config.getInt("percentageOfTheTrainingSet") / 100)
                .neighbors(config.getInt("neighbors"))
                .metric(BasicMetrics.getMetric(config.getString("metric").trim()))
                .csvDir(config.getString("csvDir"))
                .build();
    }

    ArticleReaderConfig readerConfig();

    FeatureExtractorConfig featureExtractorConfig();

    float percentageOfTheTrainingSet();

    int neighbors();

    Metric metric();

    String csvDir();
}
