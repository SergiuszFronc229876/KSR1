package pl.ksr.model;

import java.util.Arrays;

public enum Country {
    West_Germany("west-germany", "GER"),
    USA("usa", "USA"),
    France("france", "FRA"),
    UK("uk", "UK"),
    Canada("canada", "CAN"),
    Japan("japan", "JAP");

    private final String countryString;

    private final String code;

    Country(String countryString, String code) {
        this.countryString = countryString;
        this.code = code;
    }

    public String getCountryString() {
        return this.countryString;
    }

    public String getCode() {
        return code;
    }

    public static Country getCountry(String countryName) {
        return Arrays.stream(Country.values())
                .filter(c -> c.countryString.equals(countryName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Couldn't resolve %s countryName", countryName)));
    }
}