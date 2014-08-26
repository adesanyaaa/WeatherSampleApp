package weatherapp.dataclientimp;


import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import weatherapp.dataclient.UrlDataClient;

/**
 * Created by osana on 8/24/14.
 */

// An implementation of a simple data loading
// Request should be in a form of url string
// Response is of type Result
public abstract class  HttpDataClient<Result> implements UrlDataClient<Result> {

    public final Result loadData(final String urlStr) throws IOException, MalformedURLException {

        InputStream input = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) (new URL(urlStr)).openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);

            input = connection.getInputStream();
            return getDataFromInputStream(input);

        } catch(MalformedURLException ex) {
            throw ex;
        } catch(IOException ex) {
            throw ex;

        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch(IOException ex) {
                throw ex;
            }
            if (input != null) {
                connection.disconnect();
            }
        }
    }

    protected abstract Result getDataFromInputStream(final InputStream inputStream) throws IOException;
}