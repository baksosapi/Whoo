package sid;

import android.graphics.Bitmap;

/**
 * Created by sid on 7/18/17.
 */

class RowItem {

    private Bitmap bitmapImage;

    public RowItem(Bitmap bitmapImage){
        this.bitmapImage = bitmapImage;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage){
        this.bitmapImage = bitmapImage;
    }

}
