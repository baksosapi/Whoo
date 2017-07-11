package sid;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.lamar.cs.whoo.R;

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
