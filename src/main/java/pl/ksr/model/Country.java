package pl.ksr.model;

import java.util.Arrays;

public enum Country {
    West_Germany("west-germany"),
    USA("usa"),
    France("france"),
    UK("uk"),
    Canada("canada"),
    Japan("japan");

    private final String countryString;

    Country(String countryString) {
        this.countryString = countryString;
    }

    public String getCountryString() {
        return this.countryString;
    }

    public static Country getCountry(String countryName) {
        return Arrays.stream(Country.values())
                .filter(c -> c.countryString.equals(countryName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Couldn't resolve %s countryName", countryName)));
    }
}