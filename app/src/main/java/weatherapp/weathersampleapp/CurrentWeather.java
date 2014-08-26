package weatherapp.weathersampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import weatherapp.dataclientimp.ImageLoader;
import weatherapp.model.Location;
import weatherapp.model.Weather;
import weatherapp.dataclient.BackgroundDataRequest;
import weatherapp.dataclient.UrlDataClient;
import weatherapp.dataclient.DataReceiver;
import weatherapp.dataclientimp.JsonHttpClient;
import weatherapp.openweather.OW;

public final class CurrentWeather extends Activity implements  DataReceiver<String> {

    // Constants to add degree with F or C for the temperature
    private static final String METRIC_DEGREE = "\u2103";
    private static final String IMPERIAL_DEGREE = "\u2109";

    // Constant degree for direction
    private static final String DEGREE = "\u00B0";

    private static final String METRIC_SPEED = " km/h";
    private static final String IMPERIAL_SPEED = " mph";

    private boolean useMetric = false;

    // Current location and weather for that location
    private Location curLocation;
    private Weather curWeather;

    //
    private AlertDialog errorDialog;

    private static final String PREF_NAME = "WeatherApp";
    private static final String CUR_LOCATION = "CUR_LOCATION";

    private UrlDataClient<String> dataClient = new JsonHttpClient();
    private UrlDataClient<Bitmap> imageLoader = new ImageLoader();

    // Starting from API 12 it is better to use LruMap
    private HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_current_weather);

        // Get last weather update from shared preferences
        SharedPreferences sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonStr = sharedPref.getString(CUR_LOCATION, null);
        if (jsonStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                setWeatherAndLocation(OW.getLocation(jsonObject), OW.getWeather(jsonObject, false));
            } catch (JSONException e) {
                Log.d("CurrentWeather", " Exception while getting preferences" +e);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (errorDialog != null && errorDialog.isShowing()) {
            errorDialog.hide();
        }
        ((EditText) findViewById(R.id.enterLocationTextField)).setText("");

        // Save curWeather and curLocation in preferences through json string for future app run
        JSONObject jsonObject = OW.getJsonObject(curLocation, curWeather, useMetric);
        if (jsonObject != null) {
            SharedPreferences sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(CUR_LOCATION, jsonObject.toString());
            editor.commit();

        }
    }

    // Called when user clicks on Get Weather button
    public void getWeatherForLocation(View view) {
        final EditText locationTextField = (EditText) findViewById(R.id.enterLocationTextField);
        final String location = locationTextField.getText().toString();

        // load weather info based on the location entered by the user
        if (location != null && location.length() > 0) {
            BackgroundDataRequest<String> task = new BackgroundDataRequest<String>(dataClient, this);
            task.execute(OW.getUrlStr_WeatherAtLocation(location, useMetric));
        }
    }

    // Called by BackgroundDataRequest upon completion of data loading
    public void onDataLoaded(final String jsonStr) {
            try {

                if (jsonStr != null && jsonStr.length() > 0) {

                    JSONObject jsonObject = new JSONObject(jsonStr);

                    if (OW.isOK(jsonObject)) {
                        Location location = OW.getLocation(jsonObject);
                        Weather weather = OW.getWeather(jsonObject, useMetric);

                        setWeatherAndLocation(location, weather);

                        // empty location Text field
                        ((EditText) findViewById(R.id.enterLocationTextField)).setText("");

                    } else {

                        showErrorDialog( OW.getErrorMessage(jsonObject));
                    }
                }

            } catch(JSONException e) {
                Log.d("LOCATION SCREEN", "Parsing error " + e);
            }
    }

    public void onDataLoadingFailed(Exception ex) {
        showErrorDialog("Error loading data");
    }

    // Set current location and its current weather
    public void setWeatherAndLocation(final Location curLocation, final Weather curWeather) {

        if (curLocation != null && curWeather != null) {
            String oldIcon;
            synchronized (this) {
                oldIcon = this.curWeather == null ? null : this.curWeather.getIcon();
                this.curLocation = curLocation;
                this.curWeather = curWeather;

                ((TextView) findViewById(R.id.curLocation)).setText(curLocation.getName());
                ((TextView) findViewById(R.id.curTemp)).setText(curWeather.getCurrentTemperature() + getDegree());
                ((TextView) findViewById(R.id.minMaxTemp)).setText(curWeather.getMinimumTemperature() + getDegree() + "/" +
                        curWeather.getMaximumTemperature() + getDegree());
                ((TextView) findViewById(R.id.conditionDesc)).setText(curWeather.getWeatherDescription());
                ((TextView) findViewById(R.id.humidity)).setText(curWeather.getHumidity() + "%");
                ((TextView) findViewById(R.id.windSpeed)).setText(curWeather.getWindSpeed() + getSpeedLabel());

                double windGust = curWeather.getWindGust();
                if (windGust >= 0) {
                    ((TextView) findViewById(R.id.windGust)).setText(curWeather.getWindGust() + getSpeedLabel());
                }
                ((TextView) findViewById(R.id.windDirection)).setText(curWeather.getWindDirection() + DEGREE);
            }
            // update weather icon if it is suppose to change
            if (oldIcon == null || !oldIcon.equals(this.curWeather.getIcon())) {
                loadWeatherIcon(this.curWeather.getIcon());
            }
        }
    }


    private void loadWeatherIcon(final String iconId) {

        if (iconId == null || iconId.length() == 0) {
            return;
        }

        // Icon is in the cache
        final Bitmap bitmap = imageCache.get(iconId);
        if (bitmap != null) {
            if (curWeather != null && iconId.equals(curWeather.getIcon())) {
                ((ImageView) findViewById(R.id.icon)).setImageBitmap(bitmap);
            }

        } else {   // Icon has to be loaded
            // empty icon while loading (to provide feedback)
            //((ImageView) findViewById(R.id.icon)).setImageBitmap(null);
            BackgroundDataRequest<Bitmap> task =
                    new BackgroundDataRequest<Bitmap>(imageLoader, new DataReceiver<Bitmap>() {
                        @Override
                        public void onDataLoaded(Bitmap bitmap) {
                            // Make sure that the icon still has to be displayed on the screen
                            if (curWeather != null && iconId.equals(curWeather.getIcon())) {
                                ((ImageView) findViewById(R.id.icon)).setImageBitmap(bitmap);
                            }

                            // add icon to cache if it is not there yet
                            if (imageCache.get(iconId) == null) {
                                imageCache.put(iconId, bitmap);
                            }
                        }
                        public void onDataLoadingFailed(Exception ex) {
                            // icon did not load, just ignore
                        }
                    });
            task.execute(OW.getIconUrlStr(iconId));
        }
    }

    private String getDegree() {
        return (useMetric ? METRIC_DEGREE : IMPERIAL_DEGREE);
    }

    private String getSpeedLabel() {
        return (useMetric ? METRIC_SPEED : IMPERIAL_SPEED);
    }

    // Construct error dialog
    private AlertDialog getErrorDialog() {
        if (errorDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getText(R.string.tryAgain))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getText(R.string.OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            return builder.create();
        }
        return errorDialog;
    }

    private void showErrorDialog(String errorMsg) {
        if (errorDialog == null) {
            errorDialog = getErrorDialog();
        }

        if (errorDialog != null) {
            errorDialog.setMessage(errorMsg == null ? "" : errorMsg);
            if (!errorDialog.isShowing()) {
                errorDialog.show();
            }
        }
    }
}
