package pl.ksr.qualificationQuality;

import pl.ksr.model.Country;

public class ClassificationQuality {

    public static double calculateAccuracy(ConfusionMatrix matrix) {
        double result = 0;
        for (Country country : Country.values()) {
            result += matrix.getTruePositives(country);
        }
        return result / matrix.getAllCount();
    }

    public static double calculatePrecisionForCountry(ConfusionMatrix matrix, Country country) {
        return (double) matrix.getTruePositives(country) / (matrix.getTruePositives(country) + matrix.getFalsePositives(country));
    }

    public static double calculatePrecision(ConfusionMatrix matrix) {
        double result = 0;
        for (Country country : Country.values()) {
            result += calculatePrecisionForCountry(matrix, country) * matrix.getActualCountryCount(country);
        }
        return result / matrix.getAllCount();
    }

    public static double calculateRecallForCountry(ConfusionMatrix matrix, Country country) {
        return (double) matrix.getTruePositives(country) / (matrix.getTruePositives(country) + matrix.getFalseNegatives(country));
    }

    public static double calculateRecall(ConfusionMatrix matrix) {
        double result = 0;
        for (Country country : Country.values()) {
            result += calculateRecallForCountry(matrix, country) * matrix.getActualCountryCount(country);
        }
        return result / matrix.getAllCount();
    }

    public static double calculateF1ForCountry(ConfusionMatrix matrix, Country country) {
        return 2 * ((calculatePrecisionForCountry(matrix, country) * calculateRecallForCountry(matrix, country) /
                (calculatePrecisionForCountry(matrix, country) + calculateRecallForCountry(matrix, country))));
    }

    public static double calculateF1(ConfusionMatrix matrix) {
        double result = 0;
        for (Country country : Country.values()) {
            result += calculateF1ForCountry(matrix, country) * matrix.getActualCountryCount(country);
        }
        return result / matrix.getAllCount();
    }
}
