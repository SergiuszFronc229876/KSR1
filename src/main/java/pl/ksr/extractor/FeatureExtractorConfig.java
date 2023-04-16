package pl.ksr.extractor;

import com.typesafe.config.Config;
import org.immutables.value.Value;
import pl.ksr.model.Dictionary;
import pl.ksr.reader.JsonReader;

import java.util.List;

@Value.Immutable
public interface FeatureExtractorConfig {
    static FeatureExtractorConfig fromRootConfig(Config config) {
        return ImmutableFeatureExtractorConfig.builder()
                .currencyDictionary(new Dictionary(JsonReader.readJsonIntoMap(config.getString("dictionaries.currency-dir"))))
                .cityDictionary(new Dictionary(JsonReader.readJsonIntoMap(config.getString("dictionaries.city-dir"))))
                .companyDictionary(new Dictionary(JsonReader.readJsonIntoMap(config.getString("dictionaries.company-dir"))))
                .namesDictionary(new Dictionary(JsonReader.readJsonIntoMap(config.getString("dictionaries.names-dir"))))
                .measurementUnitsDictionary(new Dictionary(JsonReader.readJsonIntoMap(config.getString("dictionaries.units-dir"))))
                .features(JsonReader.readStringToArray(config.getString("features")))
                .build();
    }

    Dictionary currencyDictionary();

    Dictionary cityDictionary();

    Dictionary companyDictionary();

    Dictionary namesDictionary();

    Dictionary measurementUnitsDictionary();

    List<Integer> features();
}
