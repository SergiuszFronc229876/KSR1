package pl.ksr;

import com.typesafe.config.Config;
import org.immutables.value.Value;
import pl.ksr.reader.ArticleReaderConfig;

@Value.Immutable
public interface TestAppConfig {

    static AppConfig fromRootConfig(Config config) {
        return ImmutableAppConfig.builder()
                .readerConfig(ArticleReaderConfig.fromRootConfig(config.getConfig("test-article-reader")))
                .build();
    }

    ArticleReaderConfig readerConfig();
}

