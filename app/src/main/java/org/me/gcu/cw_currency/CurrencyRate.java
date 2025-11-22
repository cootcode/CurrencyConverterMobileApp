package org.me.gcu.cw_currency;

public class CurrencyRate {
    private String currencyName;
    private String currencyCode;
    private String countryName;
    private double rate;
    private String link;
    private String pubDate;

    public CurrencyRate(String currencyName, String currencyCode, String countryName,
                        double rate, String link, String pubDate) {
        this.currencyName = currencyName;
        this.currencyCode = currencyCode;
        this.countryName = countryName;
        this.rate = rate;
        this.link = link;
        this.pubDate = pubDate;
    }

    // Getters
    public String getCurrencyName() { return currencyName; }
    public String getCurrencyCode() { return currencyCode; }
    public String getCountryName() { return countryName; }
    public double getRate() { return rate; }
    public String getLink() { return link; }
    public String getPubDate() { return pubDate; }

    // Setters
    public void setCurrencyName(String currencyName) { this.currencyName = currencyName; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public void setRate(double rate) { this.rate = rate; }
    public void setLink(String link) { this.link = link; }
    public void setPubDate(String pubDate) { this.pubDate = pubDate; }

    // Helper method to get color based on rate
    public int getColorForRate() {
        if (rate < 1.0) {
            return 0xFFE8F5E9; // Light green
        } else if (rate < 5.0) {
            return 0xFFFFF9C4; // Light yellow
        } else if (rate < 10.0) {
            return 0xFFFFE0B2; // Light orange
        } else {
            return 0xFFFFCDD2; // Light red
        }
    }

    public boolean isRateAvailable() {
        return rate > 0.0 && !Double.isNaN(rate) && !Double.isInfinite(rate);
    }

    public String getFormattedRate() {
        if (!isRateAvailable()) return "N/A";
        double r = rate;
        int decimals;
        if (r >= 100)       decimals = 2;
        else if (r >= 1)    decimals = 4;
        else if (r >= 0.01) decimals = 6;
        else                decimals = 8;
        return String.format(java.util.Locale.US, "%." + decimals + "f", r);
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %.4f", currencyName, currencyCode, rate);
    }
}