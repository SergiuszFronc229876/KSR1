package pl.ksr.extractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.ksr.model.*;
import pl.ksr.reader.ArticleReader;

import java.util.*;
import java.util.regex.Matcher;

import static pl.ksr.extractor.FeatureExtractorUtils.getWordsFromText;

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
            features.add(extractCountryWithFirstCurrencyInArticle(article));
            features.add(extractCountryWithMostOccurredName(article));
            features.add(extractCountryWithFirstCityInArticle(article));
            features.add(extractCountryWithFirstCompanyInArticle(article));
            features.add(extractNumberOfCityOccurrencesFromGivenCountry(article, "west-germany"));
            features.add(extractNumberOfCityOccurrencesFromGivenCountry(article, "usa"));
            features.add(extractNumberOfCityOccurrencesFromGivenCountry(article, "france"));
            features.add(extractNumberOfCityOccurrencesFromGivenCountry(article, "uk"));
            features.add(extractNumberOfCityOccurrencesFromGivenCountry(article, "canada"));
            features.add(extractNumberOfCityOccurrencesFromGivenCountry(article, "japan"));
            features.add(extractCountryWithTheHighestPercentageOfFamousPeopleAppearing(article));
            features.add(extractCountryWithTheHighestCurrencyOccurrences(article));
            features.add(extractTextLength(article));
            features.add(extractFirstOccurringUnit(article));
            features.add(extractNumberOfUnitsOccurrencesFromUnitSystem(article, "metric"));
            features.add(extractNumberOfUnitsOccurrencesFromUnitSystem(article, "imperial"));
            FeatureVector featureVector = new FeatureVector(features, article.getPlace());
            synchronized (featureVector) {
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
    private Feature extractCountryWithFirstCurrencyInArticle(Article article) {
        String country = null;
        int earliestIndex = Integer.MAX_VALUE;

        for (Map.Entry<String, List<String>> entry : config.currencyDictionary().entrySet()) {
            for (String keyWord : entry.getValue()) {
                Matcher matcher = FeatureExtractorUtils.provideMatcher(article.getText(), keyWord);
                while (matcher.find()) {
                    int index = matcher.start();
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
    private Feature extractCountryWithMostOccurredName(Article article) {
        String country = null;
        int foundedMaxOccurrences = 0;

        for (Map.Entry<String, List<String>> entry : config.namesDictionary().entrySet()) {
            for (String keyWord : entry.getValue()) {
                Matcher matcher = FeatureExtractorUtils.provideMatcher(article.getText(), keyWord);
                int countOccurrenses = 0;
                while (matcher.find()) {
                    countOccurrenses++;
                }
                if (countOccurrenses > foundedMaxOccurrences) {
                    country = entry.getKey();
                    foundedMaxOccurrences = countOccurrenses;
                }
            }
        }

        return new TextFeature(country);
    }

    // The country from which the city first appears in the text
    private Feature extractCountryWithFirstCityInArticle(Article article) {
        String country = "";
        int earliestIndex = Integer.MAX_VALUE;

        for (Map.Entry<String, List<String>> entry : config.cityDictionary().entrySet()) {
            for (String keyWord : entry.getValue()) {
                Matcher matcher = FeatureExtractorUtils.provideMatcher(article.getText(), keyWord);
                while (matcher.find()) {
                    int index = matcher.start();
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
    private Feature extractCountryWithFirstCompanyInArticle(Article article) {
        String country = null;
        int earliestIndex = Integer.MAX_VALUE;

        for (Map.Entry<String, List<String>> entry : config.companyDictionary().entrySet()) {
            for (String keyWord : entry.getValue()) {
                Matcher matcher = FeatureExtractorUtils.provideMatcher(article.getText(), keyWord);
                while (matcher.find()) {
                    int index = matcher.start();
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
    private Feature extractNumberOfCityOccurrencesFromGivenCountry(Article article, String country) {
        List<String> keyWords = config.cityDictionary().getValues(country);
        int occurrenceCount = 0;

        for (String keyWord : keyWords) {
            Matcher matcher = FeatureExtractorUtils.provideMatcher(article.getText(), keyWord);
            while (matcher.find()) {
                occurrenceCount++;
            }
        }

        LOGGER.debug("Number of city occurrences: {} for {}", occurrenceCount, country);
        return new NumericalFeature(occurrenceCount);
    }


    // Country for which Percentage of occurrence of names of famous people from the country is the highest
    private Feature extractCountryWithTheHighestPercentageOfFamousPeopleAppearing(Article article) {
        Map<String, Float> result = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : config.namesDictionary().entrySet()) {
            int countOccurrenses = 0;
            for (String keyWord : entry.getValue()) {
                Matcher matcher = FeatureExtractorUtils.provideMatcher(article.getText(), keyWord);
                while (matcher.find()) {
                    countOccurrenses++;
                }
            }
            result.put(entry.getKey(), (float) (countOccurrenses / Arrays.stream(getWordsFromText(article)).count()));
        }
        LOGGER.debug("Percentage of Famous People Appearing result is: {}", result);

        return result.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(stringFloatEntry -> new TextFeature(stringFloatEntry.getKey()))
                .orElseGet(() -> new TextFeature("Double Result"));

    }

    // Country for which keywords from the currency dictionary occur most frequently
    private Feature extractCountryWithTheHighestCurrencyOccurrences(Article article) {
        Map<String, Integer> result = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : config.currencyDictionary().entrySet()) {
            int countOccurrenses = 0;
            for (String keyWord : entry.getValue()) {
                Matcher matcher = FeatureExtractorUtils.provideMatcher(article.getText(), keyWord);
                while (matcher.find()) {
                    countOccurrenses++;
                }
            }
            result.put(entry.getKey(), countOccurrenses);
        }
        LOGGER.debug("Currency occurrences result is: {}", result);
        return result.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(stringFloatEntry -> new TextFeature(stringFloatEntry.getKey()))
                .orElseGet(() -> new TextFeature("Double Result"));
    }

    // Number of words in the text
    private Feature extractTextLength(Article article) {
        return new NumericalFeature(getWordsFromText(article).length);
    }

    // The first occurring unit of measurement in the text
    private Feature extractFirstOccurringUnit(Article article) {
        String unit = null;
        int earliestIndex = Integer.MAX_VALUE;

        for (Map.Entry<String, List<String>> entry : config.measurementUnitsDictionary().entrySet()) {
            for (String keyWord : entry.getValue()) {
                Matcher matcher = FeatureExtractorUtils.provideMatcher(article.getText(), keyWord);
                while (matcher.find()) {
                    int index = matcher.start();
                    if (index < earliestIndex) {
                        unit = keyWord;
                        earliestIndex = index;
                    }
                }

            }
        }

        LOGGER.debug("First occurring unit is: {}", unit);
        return new TextFeature(unit);
    }

    // Number of units of measurement occurring for a given measurement system
    private Feature extractNumberOfUnitsOccurrencesFromUnitSystem(Article article, String unitSystem) {
        List<String> keyWords = config.measurementUnitsDictionary().getValues(unitSystem);
        int countOccurrenses = 0;

        for (String keyWord : keyWords) {
            Matcher matcher = FeatureExtractorUtils.provideMatcher(article.getText(), keyWord);
            while (matcher.find()) {
                countOccurrenses++;
            }
        }
        return new NumericalFeature(countOccurrenses);
    }

}
