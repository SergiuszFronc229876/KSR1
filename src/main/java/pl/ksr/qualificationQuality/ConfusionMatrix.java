package pl.ksr.qualificationQuality;

import pl.ksr.model.Country;

public class ConfusionMatrix {
    private final int[][] matrix;
    private final int numClasses;

    private int allCount = 0;
    private final int[] actualCountryCount;

    public ConfusionMatrix(int numClasses) {
        this.matrix = new int[numClasses][numClasses];
        this.numClasses = numClasses;
        this.actualCountryCount = new int[numClasses];
    }

    public void add(Country actual, Country predicted) {
        matrix[actual.ordinal()][predicted.ordinal()]++;
        allCount++;
        actualCountryCount[actual.ordinal()]++;
    }

    public int getTruePositives(Country country) {
        return matrix[country.ordinal()][country.ordinal()];
    }

    public int getTrueNegatives(Country country) {
        int tn  = 0;
        for (int i = 0; i < numClasses; i++) {
            for (int j = 0; j < numClasses; j++) {
                if (Country.values()[i] != country && Country.values()[j] != country) {
                    tn += matrix[i][j];
                }
            }
        }
        return tn;
    }

    public int getFalsePositives(Country country) {
        int fn = 0;
        for (int i = 0; i < numClasses; i++) {
            if (Country.values()[i] != country) {
                fn += matrix[country.ordinal()][i];
            }
        }
        return fn;
    }

    public int getFalseNegatives(Country country) {
        int fp = 0;
        for (int i = 0; i < numClasses; i++) {
            if (i != country.ordinal()) {
                fp += matrix[i][country.ordinal()];
            }
        }
        return fp;
    }

    public int getActualCountryCount(Country country) {
        return actualCountryCount[country.ordinal()];
    }

    public int getAllCount() {
        return allCount;
    }

    public int getNumClasses() {
        return numClasses;
    }

    public int[][] getMatrix() {
        return matrix;
    }
}
