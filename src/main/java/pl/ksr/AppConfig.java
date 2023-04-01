package pl.ksr;

import com.typesafe.config.Config;
import org.immutables.value.Value;
import pl.ksr.reader.ArticleReaderConfig;

@Value.Immutable
public interface AppConfig {

    static AppConfig fromRootConfig(Config config) {
        return ImmutableAppConfig.builder()
                .readerConfig(ArticleReaderConfig.fromRootConfig(config.getConfig("article-reader")))
                .build();
    }

    ArticleReaderConfig readerConfig();
}
