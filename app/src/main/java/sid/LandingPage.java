package sid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lamar.cs.whoo.FaceActivity;
import com.lamar.cs.whoo.FaceDetector;
import com.lamar.cs.whoo.LocalNameList;
import com.lamar.cs.whoo.MainActivity2;
import com.lamar.cs.whoo.R;
import com.lamar.cs.whoo.WFRDataFactory;
import com.lamar.cs.whoo.WFRPerson;
import com.lamar.cs.whoo.WFaceRecognizer;
import com.lamar.cs.whoo.WhooConfig;
import com.lamar.cs.whoo.WhooTools;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

import lib.folderpicker.FolderPicker;

/**
 * Created by sid on 7/11/17.
 */

public class LandingPage extends Activity {

    private static final int FOLDER_PICKER_CODE = 78;
    private static final int FILE_PICKER_CODE = 786;
    private static final String TAG = LandingPage.class.getSimpleName();
    EditText dirLoc;
    TextView tvLoc;
    ImageView imgDisp;
    RadioButton rbDir, rbFile, rbEigen, rbFisher, rbLbph;
    RadioGroup rbgType, rbgAlg;
    private int chooseType;
    private String folderLocation;
    Mat descriptors2,descriptors1;
    Mat img1;
    MatOfKeyPoint keypoints1,keypoints2;
    FeatureDetector detector;
    DescriptorExtractor descriptor;
    DescriptorMatcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_landing);

//        dirLoc = (EditText) findViewById(R.id.editText);
        rbDir = (RadioButton) findViewById(R.id.rbDir);
        rbFile = (RadioButton) findViewById(R.id.rbFile);
        rbEigen = (RadioButton) findViewById(R.id.rb_eigen);
        rbFisher = (RadioButton) findViewById(R.id.rb_fisher);
        rbLbph = (RadioButton) findViewById(R.id.rb_lbph);

        rbgType = (RadioGroup) findViewById(R.id.rbgType);
        rbgAlg = (RadioGroup) findViewById(R.id.rbgAlg);

        tvLoc = (TextView) findViewById(R.id.tvLoc);

        imgDisp = (ImageView) findViewById(R.id.imgDisp);
//        dirLoc.setInputType(InputType.TYPE_NULL);
//        dirLoc.setKeyListener(null);

        rbgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.e(TAG, "onCheckedChanged: "+ checkedId);
                if (checkedId == R.id.rbDir){
                    Log.e(TAG, "onCheckedChanged: "+ "Directory" );
                    chooseType = 1;
                } else if (checkedId == R.id.rbFile){
                    Log.e(TAG, "onCheckedChanged: "+ "File" );
                    chooseType = 0;
                }
            }
        });

        tvLoc.setText("No Location selected");
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
//        Intent intent = new Intent(LandingPage.this, ImageLoad.class);
//        startActivity(intent);

//        folderLocation = "/storage/emulated/0/Download/Donal1.jpg";
        folderLocation = "/storage/emulated/0/Download/lena.png";

        File imgFile = new File(folderLocation);

        if (imgFile.exists()){

            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            ImageView imageView = (ImageView) findViewById(R.id.imgDisp);
//            Log.e(TAG, "loadFile: "+ bitmap );
//            imgDisp.setImageBitmap(bitmap);

            // Init detector
//            CascadeClassifier faceDetector = new CascadeClassifier(getClass().getResource("/res/raw/lbpcascade_frontalface.xml").getPath());
            CascadeClassifier faceDetector = new CascadeClassifier(getClass().getResource("/res/raw/haarcascade_frontalface_alt.xml").getPath());

            Log.e(TAG, "loadFile: "+"facedetector "+ faceDetector );

            // Proses Image
//            img1 = new Mat();
            Mat image = Highgui.imread(folderLocation);

            MatOfRect faceDetection = new MatOfRect();
            faceDetector.detectMultiScale(image, faceDetection);

            Log.e(TAG, String.format("loadFile: "+"Detected %s faces", faceDetection.toArray().length ));

            String filename= folderLocation.substring(folderLocation.lastIndexOf("/")+1);

            for (Rect rect : faceDetection.toArray()){
                Log.e(TAG, "loadFile: "+1 );
                Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                Boolean bool = null;
                bool = Highgui.imwrite(filename, image);

                if (bool)
                    Log.e(TAG, "loadFile: "+ "SUCCESS writing image to external storage" );
                else
                    Log.e(TAG, "Fail writing image to external storage");

                String newfilename = "faceDetection.png";
                System.out.println(String.format("Writing %s", newfilename));
//                imwrite(newfilename, image);
//                Highgui.imwrite(newfilename, )

            }
            imgDisp.setImageBitmap(bitmap);

//            Utils.bitmapToMat(bitmap, img1);
//            Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGB2GRAY);
//            img1.convertTo(img1, 0);
//            descriptors1 = new Mat();
//            keypoints1 = new MatOfKeyPoint();
//            detector.detect(img1, keypoints1);
//            descriptor.compute(img1, keypoints1, descriptors1);
//            FaceDetector fd = FaceDetector.getInstance();
//            Mat mat = fd.getDetectedFace();

//            WFaceRecognizer wfr = WFaceRecognizer.getInstance();
//            String result = wfr.getResult();


//            if (result != null && !result.equalsIgnoreCase("Unknown")) {
//                input.setCompletionHint(result);
//                input.setText(result);
//                Log.e(TAG, "loadFile: "+ filename );
//            }


//            FaceDetector fd = FaceDetector.getInstance();
//            WFaceRecognizer wfr = WFaceRecognizer.getInstance();
//            WFRDataFactory factory = WFRDataFactory.getInstance();

            LocalNameList lnlist = LocalNameList.getInstance();

            String instr = filename;
            lnlist.inputLocalName(instr);
            int index = lnlist.findExistNameLocation(instr);
            if (-1 == index) {
                Log.e(TAG, "WRONG NAME, WHY?");
                WhooConfig.DBG(LandingPage.this, "WRONG NAME, WHY?");
                return;
            }

            String name = lnlist.getLocalName(index);

//            WFRPerson person = factory.addPerson(name);
//            if (null == person) {
//                Log.e(TAG, "Add Person Failed, WHY?");
//                WhooConfig.DBG(LandingPage.this, "Add Person Failed, WHY?");
//                return;
//            }

//            Mat mmat = fd.getDetectedFace();
//            Log.e(TAG, "loadFile: mmat "+mmat );
//            assert (mmat != null);
            // resize the image to normalized size.
//            mmat = WhooTools.resize(mmat);

//            boolean ret = person.addFaceImage(mmat);
//            boolean ret = person.addFaceImage(mat);
//            if (!ret) {
//                Log.e(TAG, "Add Image Failed, WHY?");
//                WhooConfig.DBG(LandingPage.this, "Add Image Failed, WHY?");
//                return;
//            } else {
//                WhooConfig.DBG(LandingPage.this, "A face image added for " + name + " !");
//            }

            // call FR.train() now, maybe it will run later on.
//            wfr.train();

            // let the faceActivity exit
//            LandingPage.this.finish();


        } else {
            Log.e(TAG, "loadFile: "+"Nof found" );
        }


    }

    public void setLocation(View view) {
        int picker_code = 100;

        Log.e(TAG, "setLocation: "+ chooseType );
        Intent intent = new Intent(this, FolderPicker.class);
//        startActivityForResult(intent, FOLDER_PICKER_CODE);

        if (chooseType == 1){
            startActivityForResult(intent, FOLDER_PICKER_CODE);
        } else if (chooseType == 0){
            intent.putExtra("title", "Select file to upload");
            intent.putExtra("location", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
            intent.putExtra("pickFiles", true);
            startActivityForResult(intent, FILE_PICKER_CODE);
//
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FOLDER_PICKER_CODE && resultCode == Activity.RESULT_OK) {

            Log.e(TAG, "onActivityResult: "+ intent );
            folderLocation = intent.getExtras().getString("data");
            Log.i( "folderLocation", folderLocation );

            tvLoc.setText(folderLocation);

        } else if (requestCode == FILE_PICKER_CODE && resultCode == Activity.RESULT_OK){
            Log.e(TAG, "onActivityResult: "+ intent );
            folderLocation = intent.getExtras().getString("data");
            Log.i( "folderLocation", folderLocation );

            tvLoc.setText(folderLocation);

        }
    }

    public void SaveImage (Mat mat) {
        Mat mIntermediateMat = new Mat();

//        Imgproc.cvtColor(mRgba, mIntermediateMat, Imgproc.COLOR_RGBA2BGR, 3);

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String filename = "barry.png";
        File file = new File(path, filename);

        Boolean bool = null;
        filename = file.toString();
        bool = Highgui.imwrite(filename, mIntermediateMat);

        if (bool == true)
            Log.d(TAG, "SUCCESS writing image to external storage");
        else
            Log.d(TAG, "Fail writing image to external storage");
    }
}
