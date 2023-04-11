package pl.ksr.model;

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
        return switch (countryName) {
            case "west-germany" -> Country.West_Germany;
            case "usa" -> Country.USA;
            case "france" -> Country.France;
            case "uk" -> Country.UK;
            case "canada" -> Country.Canada;
            case "japan" -> Country.Japan;
            default -> throw new IllegalArgumentException(String.format("Couldn't resolve %s countryName", countryName));
        };
    }
}