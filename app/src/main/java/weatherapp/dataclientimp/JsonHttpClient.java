package weatherapp.dataclientimp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by osana on 8/22/14.
 */

// An implementation of simple Json data client.
// Request should be in a form of url string
// Response is in a form of a json string
public final class JsonHttpClient extends HttpDataClient<String> {

    @Override
    protected String getDataFromInputStream(InputStream inputStream) throws IOException {
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuffer buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line + "\n");
            }
            return buffer.toString();
    }
}
