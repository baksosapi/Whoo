package sid;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.lamar.cs.whoo.MainActivity2;
import com.lamar.cs.whoo.R;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.IOException;

/**
 * Created by sid on 7/11/17.
 */

public class ImageLoad extends Activity {

    private ImageView imageView;
    private AssetManager assetManager;
    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_imageloader);

        imageView = (ImageView) findViewById(R.id.imageView);

        listAllImages();
    }

    private void listAllImages() {
//        Mat m = Highgui.imread("media/path_to_image");
//        Mat img = null;
//        try {
//            img = Utils.loadResource(this, R.drawable.image_id, CvType.CV_8UC4);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Mat m = Utils.loadResource(MainActivity2.this, R.drawable.img, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
//        Bitmap bm = Bitmap.createBitmap(m.cols(), m.rows(),Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(m, bm);
//        ImageView iv = (ImageView) findViewById(R.id.imageView1);
//        iv.setImageBitmap(bm);
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}
