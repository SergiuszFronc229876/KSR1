package pl.ksr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.ksr.reader.ArticleReader;

import static com.typesafe.config.ConfigFactory.load;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArticleReaderTest {
    @Test
    public void shouldReadArticlesFromSgmFile() {
        AppConfig configuration = AppConfig.fromRootConfig(load());
        ArticleReader reader = new ArticleReader(configuration.readerConfig());
        assertFalse(reader.getArticles().isEmpty());
    }
}
