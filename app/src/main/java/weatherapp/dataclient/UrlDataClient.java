package weatherapp.dataclient;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by osana on 8/24/14.
 */

// DataClient performs a data Request and gets a data Response
public interface UrlDataClient<Response> {
    Response loadData(final String urlStr) throws IOException, MalformedURLException;
}
