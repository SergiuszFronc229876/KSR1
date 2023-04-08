package pl.ksr.extractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksr.model.*;
import pl.ksr.reader.ArticleReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatureExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleReader.class);

    private final FeatureExtractorConfig config;

    public FeatureExtractor(FeatureExtractorConfig config) {
        this.config = config;
    }

    public List<FeatureVector> extractFeatures(List<Article> articleList) {
        ArrayList<FeatureVector> featureVectors = new ArrayList<>();
        LOGGER.info("Starting Features Extraction of {} articles", articleList.size());

        articleList.parallelStream().forEach(article -> {
            ArrayList<Feature> features = new ArrayList<>();
            features.add(extractFeature1(article));
            features.add(extractFeature2(article));
            features.add(extractFeature3(article));
            features.add(extractFeature4(article));
            features.add(extractFeature5(article, "west-germany"));
            features.add(extractFeature5(article, "usa"));
            features.add(extractFeature5(article, "france"));
            features.add(extractFeature5(article, "uk"));
            features.add(extractFeature5(article, "canada"));
            features.add(extractFeature5(article, "japan"));
            features.add(extractFeature6(article));
            features.add(extractFeature7(article));
            features.add(extractFeature8(article));
            features.add(extractFeature9(article));
            features.add(extractFeature10(article, "metric"));
            features.add(extractFeature10(article, "imperial"));
            FeatureVector featureVector = new FeatureVector(features, article.getPlace());
            synchronized (featureVectors) {
                LOGGER.info(article.getPlace());
                featureVectors.add(featureVector);
            }
        });

        LOGGER.info("Features Extraction Finished");
        return featureVectors;
    }

    public void normaliseFeatures(List<FeatureVector> featureVectorList) {
        List<Float> minValues = new ArrayList<>();
        List<Float> maxValues = new ArrayList<>();

        LOGGER.info("Starting Features Normalization of {} feature vectors", featureVectorList.size());

        // Get min and max value of each feature
        for (FeatureVector featureList : featureVectorList) {
            int index = 0;
            for (NumericalFeature feature : featureList.getNumericalFeatures()) {
                if (maxValues.size() <= index) {
                    maxValues.add(feature.getValue());
                } else {
                    float currentMaxValue = maxValues.get(index);
                    if (feature.getValue() > currentMaxValue) {
                        maxValues.set(index, feature.getValue());
                    }
                }
                if (minValues.size() <= index) {
                    minValues.add(feature.getValue());
                } else {
                    float currentMinValue = minValues.get(index);
                    if (feature.getValue() < currentMinValue) {
                        minValues.set(index, feature.getValue());
                    }
                }
                index++;
            }
        }

        // Normalise values
        for (FeatureVector featureList : featureVectorList) {
            int index = 0;
            for (NumericalFeature feature : featureList.getNumericalFeatures()) {
                if (minValues.get(index).equals(maxValues.get(index))) {
                    feature.setValue(0);
                } else {
                    feature.setValue(
                            (feature.getValue() - minValues.get(index)) / (maxValues.get(index) - minValues.get(index)));
                }
                index++;
            }
        }
        LOGGER.info("Features Normalization Finished");

    }

    // The country from which the currency appears first in the text
    private Feature extractFeature1(Article article) {
        String country = null;
        int earliestIndex = Integer.MAX_VALUE;

        for (String word : article.getText()) {
            for (Map.Entry<String, List<String>> entry : config.currencyDictionary().entrySet()) {
                List<String> stringList = entry.getValue();
                if (stringList.contains(word)) {
                    int index = article.getText().indexOf(word);
                    if (index < earliestIndex) {
                        country = entry.getKey();
                        earliestIndex = index;
                    }
                }
            }
        }

        return new TextFeature(country);
    }

    // Country from which one of the names from the list of famous people from that country occurs most often
    private Feature extractFeature2(Article article) {
        return new TextFeature(""); // TODO
    }

    // The country from which the city first appears in the text
    private Feature extractFeature3(Article article) {
        String country = "";
        int earliestIndex = Integer.MAX_VALUE;

        for (String word : article.getText()) {
            for (Map.Entry<String, List<String>> entry : config.cityDictionary().entrySet()) {
                List<String> stringList = entry.getValue();
                if (stringList.contains(word)) {
                    int index = article.getText().indexOf(word);
                    if (index < earliestIndex) {
                        country = entry.getKey();
                        earliestIndex = index;
                    }
                }
            }
        }

        return new TextFeature(country);
    }

    // The country from which the company first appears in the text
    private Feature extractFeature4(Article article) {
        String country = null;
        int earliestIndex = Integer.MAX_VALUE;

        for (String word : article.getText()) {
            for (Map.Entry<String, List<String>> entry : config.companyDictionary().entrySet()) {
                List<String> stringList = entry.getValue();
                if (stringList.contains(word)) {
                    int index = article.getText().indexOf(word);
                    if (index < earliestIndex) {
                        country = entry.getKey();
                        earliestIndex = index;
                    }
                }
            }
        }

        return new TextFeature(country);
    }

    // Number of occurrences of keywords that are city names from a dictionary of a given country
    private Feature extractFeature5(Article article, String country) {
        List<String> keyWords = config.cityDictionary().getValues(country);

        int occurrenceCount = 0;
        for (String word : article.getText()) {
            if (keyWords.contains(word)) {
                occurrenceCount++;
            }
        }

        return new NumericalFeature(occurrenceCount);
    }

    // Country for which Percentage of occurrence of names of famous people from the country is the highest
    private Feature extractFeature6(Article article) {
        return new TextFeature(""); // TODO
    }

    // Country for which keywords from the currency dictionary occur most frequently
    private Feature extractFeature7(Article article) {
        return new TextFeature(""); // TODO
    }

    // Number of words in the text
    private Feature extractFeature8(Article article) {
        return new NumericalFeature(article.getText().size());
    }

    // The first occurring unit of measurement in the text
    private Feature extractFeature9(Article article) {
        return new TextFeature(""); // TODO
    }

    // Number of units of measurement occurring for a given measurement system
    private Feature extractFeature10(Article article, String unitSystem) {
        List<String> keyWords = config.measurementUnitsDictionary().getValues(unitSystem);

        int unitCount = 0;
        for (String word : article.getText()) {
            if (keyWords.contains(word)) {
                unitCount++;
            }
        }

        return new NumericalFeature(unitCount);
    }
}
