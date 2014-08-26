package weatherapp.dataclientimp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by osana on 8/24/14.
 */

/*
 * An implementation of a simple ImageLoader
 * Request should be in a form of url string
 * Response is a form of a Bitmap
 */
public final class ImageLoader extends HttpDataClient <Bitmap> {

    @Override
    protected Bitmap getDataFromInputStream(InputStream inputStream) throws IOException {
        return BitmapFactory.decodeStream(inputStream);
    }
}