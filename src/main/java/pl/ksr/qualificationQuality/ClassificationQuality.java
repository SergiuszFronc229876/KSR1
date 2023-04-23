package pl.ksr.qualificationQuality;

import pl.ksr.model.Country;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ClassificationQuality {

    public static double calculateAccuracy(ConfusionMatrix matrix) {
        double result = 0;
        for (Country country : Country.values()) {
            result += matrix.getTruePositives(country);
        }
        return roundResult(result / matrix.getAllCount());
    }

    public static double calculatePrecisionForCountry(ConfusionMatrix matrix, Country country) {
        return roundResult((double) matrix.getTruePositives(country) / (matrix.getTruePositives(country) + matrix.getFalsePositives(country)));
    }

    public static double calculatePrecision(ConfusionMatrix matrix) {
        double result = 0;
        for (Country country : Country.values()) {
            result += calculatePrecisionForCountry(matrix, country) * matrix.getActualCountryCount(country);
        }
        return roundResult(result / matrix.getAllCount());
    }

    public static double calculateRecallForCountry(ConfusionMatrix matrix, Country country) {
        return roundResult((double) matrix.getTruePositives(country) / (matrix.getTruePositives(country) + matrix.getFalseNegatives(country)));
    }

    public static double calculateRecall(ConfusionMatrix matrix) {
        double result = 0;
        for (Country country : Country.values()) {
            result += calculateRecallForCountry(matrix, country) * matrix.getActualCountryCount(country);
        }
        return roundResult(result / matrix.getAllCount());
    }

    public static double calculateF1ForCountry(ConfusionMatrix matrix, Country country) {
        return roundResult(2 * ((calculatePrecisionForCountry(matrix, country) * calculateRecallForCountry(matrix, country) /
                (calculatePrecisionForCountry(matrix, country) + calculateRecallForCountry(matrix, country)))));
    }

    public static double calculateF1(ConfusionMatrix matrix) {
        double result = 0;
        for (Country country : Country.values()) {
            result += calculateF1ForCountry(matrix, country) * matrix.getActualCountryCount(country);
        }
        return roundResult(result / matrix.getAllCount());
    }

    private static double roundResult(Double d) {
        try {
            return new BigDecimal(d).setScale(2, RoundingMode.HALF_UP).doubleValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
