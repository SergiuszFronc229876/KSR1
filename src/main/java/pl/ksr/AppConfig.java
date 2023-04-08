package pl.ksr;

import com.typesafe.config.Config;
import org.immutables.value.Value;
import pl.ksr.extractor.FeatureExtractorConfig;
import pl.ksr.reader.ArticleReaderConfig;

@Value.Immutable
public interface AppConfig {

    static AppConfig fromRootConfig(Config config) {
        return ImmutableAppConfig.builder()
                .readerConfig(ArticleReaderConfig.fromRootConfig(config.getConfig("article-reader")))
                .featureExtractorConfig(FeatureExtractorConfig.fromRootConfig(config.getConfig("feature-extractor")))
                .build();
    }

    ArticleReaderConfig readerConfig();

    FeatureExtractorConfig featureExtractorConfig();
}
