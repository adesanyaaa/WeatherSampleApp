package weatherapp.model;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by osana on 8/22/14.
 */
public final class Weather {

    private int id;
    private String iconId;

    private int curTemp;
    private int minTemp;
    private int maxTemp;
    private String weatherDesc;

    private int humidity;
    private int windSpeed;
    private int windGust;
    private int windDirection;

    private boolean useMetric = false;

    public Weather(int id, String iconId,
                   String weatherDesc,
                   int curTemp, int minTemp, int maxTemp,
                   int humidity,
                   int windSpeed, int windGust, int windDirection, boolean useMetric) {
        this.id = id;
        this.iconId = iconId;
        this.weatherDesc = weatherDesc;
        this.curTemp = curTemp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windGust = windGust;
        this.windDirection = windDirection;

        this.useMetric = useMetric;
    }

    public int getId() {
        return id;
    }

    public String getIcon() { return iconId; }

    public int getCurrentTemperature() {
        return curTemp;
    }

    public int getMinimumTemperature() {
        return minTemp;
    }

    public int getMaximumTemperature() {
        return maxTemp;
    }

    public String getWeatherDescription() {
        return weatherDesc;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public int getWindGust() {
        return windGust;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public boolean isUseMetric() {
        return useMetric;
    }

}
