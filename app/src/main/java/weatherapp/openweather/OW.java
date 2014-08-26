package weatherapp.openweather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weatherapp.model.Location;
import weatherapp.model.Weather;

/**
 * Created by osana on 8/23/14.
 */

// OpenWeather API constants
// to / from JSON parsing of weather and location per OpenWeather API
public final class OW {

    // Base urls for getting data and icons
    public final static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
    public final static String IMG_URL = "http://openweathermap.org/img/w/";

    private static String IMG_EXT = ".png";
    private static String GET_WEATHER_PARAM_METRIC = "mode=json&units=metric&q=";
    private static String GET_WEATHER_PARAM_IMPERIAL = "mode=json&units=imperial&q=";

    // data tags for OpenWeather API
    public final static String TAG_COD = "cod";
    public final static String TAG_ID = "id";
    public final static String TAG_ICON= "icon";
    public final static String TAG_NAME = "name";
    public final static String TAG_COUNTRY = "country";
    public final static String TAG_COORD = "coord";
    public final static String TAG_LONGITUDE = "lon";
    public final static String TAG_LATITUDE = "lat";

    public final static String TAG_SYS = "sys";
    public final static String TAG_MAIN= "main";
    public final static String TAG_WEATHER = "weather";
    public final static String TAG_WIND = "wind";

    public final static String TAG_DESCRIPTION = "description";
    public final static String TAG_MESSAGE = "message";

    public final static String TAG_TEMP = "temp";
    public final static String TAG_TEMP_MIN = "temp_max";
    public final static String TAG_TEMP_MAX = "temp_min";

    public final static String TAG_HUMIDITY = "humidity";
    public final static String TAG_SPEED = "speed";
    public final static String TAG_GUST = "gust";
    public final static String TAG_DIRECTION = "deg";

    public final static String CUSTOM_USE_METRIC = "useMetric";

    public final static int OK_CODE = 200;
    public final static int NOT_FOUND_CODE = 404;

    public static Location getLocation(JSONObject jsonObject) {
        try {
            JSONObject coordJsonObject = jsonObject.getJSONObject(OW.TAG_COORD);
            return new Location(jsonObject.getInt("id"),
                                jsonObject.getString(OW.TAG_NAME),
                                jsonObject.getJSONObject(OW.TAG_SYS).getString(OW.TAG_COUNTRY),
                                coordJsonObject.getDouble(OW.TAG_LONGITUDE),
                                coordJsonObject.getDouble(OW.TAG_LATITUDE));
        } catch (JSONException e) {
             Log.d("Openweather API", " Exception parsing location" + e);
        }

        return null;
    }


    public static Weather getWeather(JSONObject jsonObject, boolean useMetric) {
        try {
            if (jsonObject.has(CUSTOM_USE_METRIC)) {
                useMetric = jsonObject.getBoolean(CUSTOM_USE_METRIC);
            }
            JSONObject mainJsonObject = jsonObject.getJSONObject(OW.TAG_MAIN);
            JSONObject windJsonObject = jsonObject.getJSONObject(OW.TAG_WIND);
            jsonObject = (JSONObject) jsonObject.getJSONArray(OW.TAG_WEATHER).get(0);
            int windSpeed = windJsonObject.getInt(OW.TAG_SPEED);
            return  new Weather(jsonObject.getInt(OW.TAG_ID),
                    jsonObject.getString(OW.TAG_ICON),
                    jsonObject.getString(OW.TAG_DESCRIPTION),
                    mainJsonObject.getInt(OW.TAG_TEMP),
                    mainJsonObject.getInt(OW.TAG_TEMP_MIN),
                    mainJsonObject.getInt(OW.TAG_TEMP_MAX),
                    mainJsonObject.getInt(OW.TAG_HUMIDITY),
                    windSpeed,
                    windJsonObject.has(OW.TAG_GUST) ? windJsonObject.getInt(OW.TAG_GUST) : windSpeed,
                    windJsonObject.getInt(OW.TAG_DIRECTION),
                    useMetric);
        } catch (JSONException e) {
            Log.d("Openweather API", " Exception parsing weather" + e);
        }

        return null;
    }

    // Get json object for location / weather pair
    // Used for saving preferences so that those could be loaded on next app start
    public static JSONObject getJsonObject(Location location, Weather weather, boolean useMetric) {
        JSONObject jsonObject = null;

        try {
            if (location != null && weather != null) {
                jsonObject = new JSONObject();
                jsonObject.put(TAG_ID, location.getId());
                jsonObject.put(TAG_NAME, location.getName());

                JSONObject nextJsonObject = new JSONObject();
                nextJsonObject.put(TAG_LONGITUDE, location.getLongitude());
                nextJsonObject.put(TAG_LATITUDE, location.getLatitude());
                jsonObject.put(TAG_COORD, nextJsonObject);

                nextJsonObject = new JSONObject();
                nextJsonObject.put(TAG_COUNTRY, location.getCountry());
                nextJsonObject.put(TAG_ID, location.getId());
                jsonObject.put(TAG_SYS, nextJsonObject);


                nextJsonObject = new JSONObject();
                nextJsonObject.put(TAG_ID, weather.getId());
                nextJsonObject.put(TAG_ICON, weather.getIcon());
                nextJsonObject.put(TAG_DESCRIPTION, weather.getWeatherDescription());
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(nextJsonObject);
                jsonObject.put(TAG_WEATHER, jsonArray);

                nextJsonObject = new JSONObject();
                nextJsonObject.put(TAG_TEMP, weather.getCurrentTemperature());
                nextJsonObject.put(TAG_HUMIDITY, weather.getHumidity());
                nextJsonObject.put(TAG_TEMP_MIN, weather.getMinimumTemperature());
                nextJsonObject.put(TAG_TEMP_MAX, weather.getMaximumTemperature());
                jsonObject.put(TAG_MAIN, nextJsonObject);

                nextJsonObject = new JSONObject();
                nextJsonObject.put(TAG_SPEED, weather.getWindSpeed());
                nextJsonObject.put(TAG_GUST, weather.getWindGust());
                nextJsonObject.put(TAG_DIRECTION, weather.getWindDirection());
                jsonObject.put(TAG_WIND, nextJsonObject);

                jsonObject.put(CUSTOM_USE_METRIC, useMetric);
            }

        } catch(JSONException e) {
            Log.d("Openweather API", " Creating json from location/weather" + e);
        }

        return jsonObject;
    }

    public static boolean isOK(JSONObject jsonObject) {
        try {
            return jsonObject != null && jsonObject.getInt(TAG_COD) == OW.OK_CODE;
        } catch(JSONException e) {
            Log.d("Openweather API", " Exception parsing error code" + e);
        }
        return false;
    }

    public static String getErrorMessage(JSONObject jsonObject) {
        try {
            return jsonObject == null ? null : jsonObject.getString(TAG_MESSAGE);
        } catch(JSONException e) {
            Log.d("Openweather API", "Problems getting error message " + e);
        }
        return null;
    }

    public static String getUrlStr_WeatherAtLocation(String location, boolean useMetric) {
        return location != null && location.length() > 0 ? BASE_URL + getParams(useMetric) + location : null;
    }

    public static String getIconUrlStr(String iconId) {
        return iconId != null && iconId.length() > 0 ? IMG_URL + iconId + IMG_EXT : null;
    }

    public static  String getParams(boolean useMetric) {
        return (useMetric ? GET_WEATHER_PARAM_METRIC : GET_WEATHER_PARAM_IMPERIAL);
    }
}
