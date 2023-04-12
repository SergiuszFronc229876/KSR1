package pl.ksr;

import org.junit.jupiter.api.Test;
import pl.ksr.model.Country;
import pl.ksr.qualificationQuality.ClassificationQuality;
import pl.ksr.qualificationQuality.ConfusionMatrix;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassificationQualityTest {

    @Test
    public void classificationQualityTest() {
        // initialize ConfusionMatrix with 6 classes
        ConfusionMatrix matrix = new ConfusionMatrix(6);

        // add some predictions and actual values
        matrix.add(Country.West_Germany, Country.West_Germany);
        matrix.add(Country.West_Germany, Country.West_Germany);
        matrix.add(Country.West_Germany, Country.West_Germany);
        matrix.add(Country.West_Germany, Country.West_Germany);
        matrix.add(Country.West_Germany, Country.West_Germany);
        matrix.add(Country.West_Germany, Country.West_Germany);
        matrix.add(Country.West_Germany, Country.West_Germany);
        matrix.add(Country.West_Germany, Country.Japan);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.USA);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.UK);
        matrix.add(Country.USA, Country.Canada);
        matrix.add(Country.USA, Country.Canada);
        matrix.add(Country.France, Country.West_Germany);
        matrix.add(Country.France, Country.USA);
        matrix.add(Country.France, Country.France);
        matrix.add(Country.France, Country.France);
        matrix.add(Country.France, Country.France);
        matrix.add(Country.France, Country.France);
        matrix.add(Country.France, Country.France);
        matrix.add(Country.France, Country.France);
        matrix.add(Country.France, Country.France);
        matrix.add(Country.France, Country.France);
        matrix.add(Country.France, Country.UK);
        matrix.add(Country.France, Country.Canada);
        matrix.add(Country.France, Country.Canada);
        matrix.add(Country.France, Country.Canada);
        matrix.add(Country.France, Country.Canada);
        matrix.add(Country.UK, Country.West_Germany);
        matrix.add(Country.UK, Country.France);
        matrix.add(Country.UK, Country.France);
        matrix.add(Country.UK, Country.France);
        matrix.add(Country.UK, Country.France);
        matrix.add(Country.UK, Country.France);
        matrix.add(Country.UK, Country.UK);
        matrix.add(Country.UK, Country.UK);
        matrix.add(Country.UK, Country.UK);
        matrix.add(Country.UK, Country.UK);
        matrix.add(Country.UK, Country.UK);
        matrix.add(Country.UK, Country.UK);
        matrix.add(Country.UK, Country.UK);
        matrix.add(Country.UK, Country.UK);
        matrix.add(Country.Canada, Country.USA);
        matrix.add(Country.Canada, Country.USA);
        matrix.add(Country.Canada, Country.USA);
        matrix.add(Country.Canada, Country.USA);
        matrix.add(Country.Canada, Country.USA);
        matrix.add(Country.Canada, Country.France);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Canada, Country.Canada);
        matrix.add(Country.Japan, Country.West_Germany);
        matrix.add(Country.Japan, Country.West_Germany);
        matrix.add(Country.Japan, Country.Canada);
        matrix.add(Country.Japan, Country.Japan);
        matrix.add(Country.Japan, Country.Japan);
        matrix.add(Country.Japan, Country.Japan);
        matrix.add(Country.Japan, Country.Japan);
        matrix.add(Country.Japan, Country.Japan);
        matrix.add(Country.Japan, Country.Japan);

        double acc = ClassificationQuality.calculateAccuracy(matrix);
        assertEquals(0.64, round(acc));

        double ppv_ger = ClassificationQuality.calculatePrecisionForCountry(matrix, Country.West_Germany);
        assertEquals(round(ppv_ger), 0.88);
        double ppv_usa = ClassificationQuality.calculatePrecisionForCountry(matrix, Country.USA);
        assertEquals(round(ppv_usa), 0.58);
        double ppv_fra = ClassificationQuality.calculatePrecisionForCountry(matrix, Country.France);
        assertEquals(round(ppv_fra), 0.53);
        double ppv_uk = ClassificationQuality.calculatePrecisionForCountry(matrix, Country.UK);
        assertEquals(round(ppv_uk), 0.57);
        double ppv_canada = ClassificationQuality.calculatePrecisionForCountry(matrix, Country.Canada);
        assertEquals(round(ppv_canada), 0.74);
        double ppv_japan = ClassificationQuality.calculatePrecisionForCountry(matrix, Country.Japan);
        assertEquals(round(ppv_japan), 0.67);
        double ppv = ClassificationQuality.calculatePrecision(matrix);
        assertEquals(0.64, round(ppv));

        double tpr_ger = ClassificationQuality.calculateRecallForCountry(matrix, Country.West_Germany);
        assertEquals(round(tpr_ger), 0.64);
        double tpr_usa = ClassificationQuality.calculateRecallForCountry(matrix, Country.USA);
        assertEquals(round(tpr_usa), 0.76);
        double tpr_fra = ClassificationQuality.calculateRecallForCountry(matrix, Country.France);
        assertEquals(round(tpr_fra), 0.57);
        double tpr_uk = ClassificationQuality.calculateRecallForCountry(matrix, Country.UK);
        assertEquals(round(tpr_uk), 0.38);
        double tpr_can = ClassificationQuality.calculateRecallForCountry(matrix, Country.Canada);
        assertEquals(round(tpr_can), 0.71);
        double tpr_jap = ClassificationQuality.calculateRecallForCountry(matrix, Country.Japan);
        assertEquals(round(tpr_jap), 0.86);
        double tpr = ClassificationQuality.calculateRecall(matrix);
        assertEquals(0.67, round(tpr));

        double f1_ger = ClassificationQuality.calculateF1ForCountry(matrix, Country.West_Germany);
        assertEquals(round(f1_ger), 0.74);
        double f1_usa = ClassificationQuality.calculateF1ForCountry(matrix, Country.USA);
        assertEquals(round(f1_usa), 0.66);
        double f1_fra = ClassificationQuality.calculateF1ForCountry(matrix, Country.France);
        assertEquals(round(f1_fra), 0.55);
        double f1_uk = ClassificationQuality.calculateF1ForCountry(matrix, Country.UK);
        assertEquals(round(f1_uk), 0.46);
        double f1_can = ClassificationQuality.calculateF1ForCountry(matrix, Country.Canada);
        assertEquals(round(f1_can), 0.72);
        double f1_jap = ClassificationQuality.calculateF1ForCountry(matrix, Country.Japan);
        assertEquals(round(f1_jap), 0.75);
        double f1 = ClassificationQuality.calculateF1(matrix);
        assertEquals(0.64, round(f1));
    }

    private double round(double a) {
        return (double) Math.round(a * 100) / 100;
    }
}
