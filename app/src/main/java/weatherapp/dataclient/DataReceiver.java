package weatherapp.dataclient;

/**
 * Created by osana on 8/22/14.
 */

// Data response will be delivered to the DataReceiver
public interface DataReceiver<Response> {
    void onDataLoaded(Response data);
    void onDataLoadingFailed(Exception exception);
}
