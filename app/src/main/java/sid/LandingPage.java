package sid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.lamar.cs.whoo.MainActivity2;
import com.lamar.cs.whoo.R;

import lib.folderpicker.FolderPicker;

/**
 * Created by sid on 7/11/17.
 */

public class LandingPage extends Activity {

    private static final int FOLDERPICKER_CODE = 78;
    private static final int FILE_PICKER_CODE = 786;
    EditText dirLoc;
    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_landing);

        dirLoc = (EditText) findViewById(R.id.editText);

        dirLoc.setInputType(InputType.TYPE_NULL);
        dirLoc.setKeyListener(null);
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

    public void openCamera(View view) {

        Intent intent = new Intent(LandingPage.this, MainActivity2.class);
        startActivity(intent);


    }

    public void loadFile(View view) {
        Intent intent = new Intent(LandingPage.this, ImageLoad.class);
        startActivity(intent);

    }

    public void setLocation(View view) {
        Intent intent = new Intent(this, FolderPicker.class);
//        startActivityForResult(intent, FILE_PICKER_CODE);
        startActivityForResult(intent, FOLDERPICKER_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FOLDERPICKER_CODE && resultCode == Activity.RESULT_OK) {

            String folderLocation = intent.getExtras().getString("data");
            Log.i( "folderLocation", folderLocation );

            dirLoc.setText(folderLocation);

        }
    }
}
