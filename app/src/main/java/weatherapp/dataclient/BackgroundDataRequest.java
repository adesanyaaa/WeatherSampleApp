package weatherapp.dataclient;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

/**
 * Created by osana on 8/22/14.
 */
// Asynchronous task performs a data request using Url string and gets data (Response)
// DataClient is notified when data is loaded or if data fails to load
public class BackgroundDataRequest<Response> extends AsyncTask<String, Void, Response> {

    private final UrlDataClient<Response>  dataClient;
    private final DataReceiver<Response> dataReceiver;

    private Exception exception;

    public BackgroundDataRequest(UrlDataClient<Response> dataClient,
                                 DataReceiver<Response> dataReceiver){
        this.dataReceiver = dataReceiver;
        this.dataClient = dataClient;
    }

    @Override
    protected Response doInBackground(String... params) {

        if (params != null && params.length > 0 ) {
            try {
                if (dataClient != null) {
                    return dataClient.loadData(params[0]);
                }
            } catch(MalformedURLException ex) {
                this.exception = ex;
            } catch(IOException ex) {
                this.exception = ex;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Response data) {
            if (dataReceiver != null) {
                if (exception != null) {
                    dataReceiver.onDataLoadingFailed(exception);
                }
                dataReceiver.onDataLoaded(data);
            }
    }
}