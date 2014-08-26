package weatherapp.model;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by osana on 8/22/14.
 */
public final class Location  {

    private int id;
    private String name;
    private String country;
    private double longitude;
    private double latitude;

    public Location(int id, String name, String country, double longitude, double latitude) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

}
