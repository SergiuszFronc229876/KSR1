package pl.ksr.reader;

import com.typesafe.config.Config;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface ArticleReaderConfig {

    static ArticleReaderConfig fromRootConfig(Config config) {
        return ImmutableArticleReaderConfig.builder()
                .articlesDir(config.getString("articles-dir"))
                .places(config.getStringList("places"))
                .stopWords(config.getStringList("stop-words"))
                .build();
    }

    String articlesDir();

    List<String> places();

    List<String> stopWords();
}
