public class Forecast {
    private final String date;
    private final String temp;
    private final String description;

    public Forecast(String date, String temp, String description) {
        this.date = date;
        this.temp = temp;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getTemp() {
        return temp;
    }

    public String getDescription() {
        return description;
    }
}
