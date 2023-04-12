package pl.ksr;

import org.junit.jupiter.api.Test;
import pl.ksr.model.Country;
import pl.ksr.qualificationQuality.ConfusionMatrix;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfusionMatrixTest {

    @Test
    public void confusionMatrixTest() {
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

        // test true positives, true negatives, false positives, and false negatives
        assertEquals(7, matrix.getTruePositives(Country.West_Germany));
        assertEquals(19, matrix.getTruePositives(Country.USA));
        assertEquals(8, matrix.getTruePositives(Country.France));
        assertEquals(8, matrix.getTruePositives(Country.UK));
        assertEquals(17, matrix.getTruePositives(Country.Canada));
        assertEquals(6, matrix.getTruePositives(Country.Japan));

        assertEquals(90, matrix.getTrueNegatives(Country.West_Germany));
        assertEquals(63, matrix.getTrueNegatives(Country.USA));
        assertEquals(81, matrix.getTrueNegatives(Country.France));
        assertEquals(75, matrix.getTrueNegatives(Country.UK));
        assertEquals(72, matrix.getTrueNegatives(Country.Canada));
        assertEquals(92, matrix.getTrueNegatives(Country.Japan));

        assertEquals(1, matrix.getFalsePositives(Country.West_Germany));
        assertEquals(14, matrix.getFalsePositives(Country.USA));
        assertEquals(7, matrix.getFalsePositives(Country.France));
        assertEquals(6, matrix.getFalsePositives(Country.UK));
        assertEquals(6, matrix.getFalsePositives(Country.Canada));
        assertEquals(3, matrix.getFalsePositives(Country.Japan));

        assertEquals(4, matrix.getFalseNegatives(Country.West_Germany));
        assertEquals(6, matrix.getFalseNegatives(Country.USA));
        assertEquals(6, matrix.getFalseNegatives(Country.France));
        assertEquals(13, matrix.getFalseNegatives(Country.UK));
        assertEquals(7, matrix.getFalseNegatives(Country.Canada));
        assertEquals(1, matrix.getFalseNegatives(Country.Japan));
    }
}
