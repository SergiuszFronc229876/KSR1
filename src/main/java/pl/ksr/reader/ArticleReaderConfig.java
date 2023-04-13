package pl.ksr.reader;

import com.typesafe.config.Config;
import org.immutables.value.Value;
import pl.ksr.model.Country;

import java.util.List;

@Value.Immutable
public interface ArticleReaderConfig {

    static ArticleReaderConfig fromRootConfig(Config config) {
        return ImmutableArticleReaderConfig.builder()
                .articlesDir(config.getString("articles-dir"))
                .places(config.getStringList("places").stream().map(Country::getCountry).toList())
                .stopWords(config.getStringList("stop-words"))
                .build();
    }

    String articlesDir();

    List<Country> places();

    List<String> stopWords();
}
