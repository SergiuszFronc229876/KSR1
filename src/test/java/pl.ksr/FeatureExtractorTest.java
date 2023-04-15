package pl.ksr;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.ksr.extractor.FeatureExtractor;
import pl.ksr.extractor.FeatureExtractorConfig;
import pl.ksr.extractor.ImmutableFeatureExtractorConfig;
import pl.ksr.model.Article;
import pl.ksr.model.FeatureVector;
import pl.ksr.model.NumericalFeature;
import pl.ksr.model.TextFeature;
import pl.ksr.reader.ArticleReader;

import java.util.List;

import static com.typesafe.config.ConfigFactory.load;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FeatureExtractorTest {

    private List<Article> articles;
    private FeatureExtractor featureExtractor;
    private AppConfig configuration;

    @BeforeAll
    public void setup() {
        configuration = AppConfig.fromRootConfig(load());
        ArticleReader reader = new ArticleReader(configuration.readerConfig());
        this.articles = reader.getArticles();
        this.featureExtractor = new FeatureExtractor(configuration.featureExtractorConfig());
    }

    @Test
    public void featureExtractionBeforeNormalisationTest() {
        List<FeatureVector> featureVectors = featureExtractor.extractFeatures(articles);
        FeatureVector vector1 = featureVectors.get(1);


        assertEquals("usa", ((TextFeature) vector1.getFeature(0)).getValue()); // 1
        assertEquals("usa", ((TextFeature) vector1.getFeature(1)).getValue()); // 2
        assertEquals("usa", ((TextFeature) vector1.getFeature(2)).getValue()); // 3
        assertEquals("usa", ((TextFeature) vector1.getFeature(3)).getValue()); // 4

        assertEquals(0, ((NumericalFeature) vector1.getFeature(4)).getValue()); // 5_GER
        assertEquals(2, ((NumericalFeature) vector1.getFeature(5)).getValue()); // 5_USA
        assertEquals(3, ((NumericalFeature) vector1.getFeature(6)).getValue()); // 5_FRA
        assertEquals(0, ((NumericalFeature) vector1.getFeature(7)).getValue()); // 5_UK
        assertEquals(0, ((NumericalFeature) vector1.getFeature(8)).getValue()); // 5_CAN
        assertEquals(0, ((NumericalFeature) vector1.getFeature(9)).getValue()); // 5_JAP

        assertEquals("usa", ((TextFeature) vector1.getFeature(10)).getValue()); // 6
        assertEquals("usa", ((TextFeature) vector1.getFeature(11)).getValue()); // 7
        assertEquals(43, ((NumericalFeature) vector1.getFeature(12)).getValue()); // 8
        assertEquals("inch", ((TextFeature) vector1.getFeature(13)).getValue()); // 9

        assertEquals(3, ((NumericalFeature) vector1.getFeature(14)).getValue()); // 10_M
        assertEquals(6, ((NumericalFeature) vector1.getFeature(15)).getValue()); // 10_I
    }

    @Test
    public void featureExtractionWithout5and10Features() {
        FeatureExtractorConfig featureExtractorConfig = anyFeatureExtractorConfigWithGivenFeatures(List.of(1, 2, 3, 4, 6, 7, 8, 9));
        this.featureExtractor = new FeatureExtractor(featureExtractorConfig);
        List<FeatureVector> featureVectors = featureExtractor.extractFeatures(articles);
        FeatureVector vector1 = featureVectors.get(1);

        assertEquals("usa", ((TextFeature) vector1.getFeature(0)).getValue()); // 1
        assertEquals("usa", ((TextFeature) vector1.getFeature(1)).getValue()); // 2
        assertEquals("usa", ((TextFeature) vector1.getFeature(2)).getValue()); // 3
        assertEquals("usa", ((TextFeature) vector1.getFeature(3)).getValue()); // 4

        assertEquals("usa", ((TextFeature) vector1.getFeature(4)).getValue()); // 6
        assertEquals("usa", ((TextFeature) vector1.getFeature(5)).getValue()); // 7
        assertEquals(43, ((NumericalFeature) vector1.getFeature(6)).getValue()); // 8
        assertEquals("inch", ((TextFeature) vector1.getFeature(7)).getValue()); // 9
    }


    @Test
    public void featureExtractionAfterNormalisationTest() {
        List<FeatureVector> featureVectors = featureExtractor.extractFeatures(articles);
        featureExtractor.normaliseFeatures(featureVectors);

        FeatureVector vector1 = featureVectors.get(1);
        assertEquals("usa", ((TextFeature) vector1.getFeature(0)).getValue()); // 1
        assertEquals("usa", ((TextFeature) vector1.getFeature(1)).getValue()); // 2
        assertEquals("usa", ((TextFeature) vector1.getFeature(2)).getValue()); // 3
        assertEquals("usa", ((TextFeature) vector1.getFeature(3)).getValue()); // 4

//        assertEquals(0, ((NumericalFeature)vector1.getFeature(4)).getValue()); // 5_GER
//        assertEquals(2, ((NumericalFeature)vector1.getFeature(5)).getValue()); // 5_USA
//        assertEquals(3, ((NumericalFeature)vector1.getFeature(6)).getValue()); // 5_FRA
//        assertEquals(0, ((NumericalFeature)vector1.getFeature(7)).getValue()); // 5_UK
//        assertEquals(0, ((NumericalFeature)vector1.getFeature(8)).getValue()); // 5_CAN
//        assertEquals(0, ((NumericalFeature)vector1.getFeature(9)).getValue()); // 5_JAP

        assertEquals("usa", ((TextFeature) vector1.getFeature(10)).getValue()); // 6
        assertEquals("usa", ((TextFeature) vector1.getFeature(11)).getValue()); // 7
//        assertEquals(0, ((NumericalFeature)vector1.getFeature(12)).getValue()); // 8
        assertEquals("inch", ((TextFeature) vector1.getFeature(13)).getValue()); // 9

//        assertEquals(4, ((NumericalFeature)vector1.getFeature(14)).getValue()); // 10_M
//        assertEquals(3, ((NumericalFeature)vector1.getFeature(15)).getValue()); // 10_I
    }

    private FeatureExtractorConfig anyFeatureExtractorConfigWithGivenFeatures(List<Integer> list) {
        return ImmutableFeatureExtractorConfig.builder()
                .from(configuration.featureExtractorConfig()).features(list)
                .build();
    }

}