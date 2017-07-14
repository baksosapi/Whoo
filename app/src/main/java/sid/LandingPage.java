package sid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lamar.cs.whoo.FaceActivity;
import com.lamar.cs.whoo.FaceDetector;
import com.lamar.cs.whoo.LocalNameList;
import com.lamar.cs.whoo.MainActivity2;
import com.lamar.cs.whoo.R;
import com.lamar.cs.whoo.WFRDataFactory;
import com.lamar.cs.whoo.WFRFaceImage;
import com.lamar.cs.whoo.WFRPerson;
import com.lamar.cs.whoo.WFaceRecognizer;
import com.lamar.cs.whoo.WhooConfig;
import com.lamar.cs.whoo.WhooTools;

import org.opencv.android.Utils;
import org.opencv.contrib.FaceRecognizer;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Vector;

import lib.folderpicker.FolderPicker;

import static android.R.attr.height;
import static android.R.attr.logo;
import static android.R.attr.width;

/**
 * Created by sid on 7/11/17.
 */

public class LandingPage extends Activity {

    private static final int FOLDER_PICKER_CODE = 78;
    private static final int FILE_PICKER_CODE = 786;
    private static final String TAG = LandingPage.class.getSimpleName();
    EditText dirLoc;
    TextView tvLoc;
    ImageView imgDisp, imgFace;
    RadioButton rbDir, rbFile, rbEigen, rbFisher, rbLbph;
    RadioGroup rbgType, rbgAlg;
    private int chooseType;
    private String folderLocation;
    Mat descriptors2,descriptors1;
    TextView tvFace;
    Mat image;
    Mat mDetectedFace ;
    MatOfKeyPoint keypoints1,keypoints2;
    FeatureDetector detector;
    DescriptorExtractor descriptor;
    DescriptorMatcher matcher;
    private Context mContext;
    private String afilename;
    DrawView mTargetView;
    private Bitmap mutableBitmap, mutableBitmapFace;
    private String filename;
//    private FaceRecognizer mReadable;
    private String mPredictResult;

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
        imgFace = (ImageView) findViewById(R.id.imgDispFace);
        tvFace = (TextView) findViewById(R.id.tvFace) ;
//        imgDisp = (ImageView) findViewById(R.id.draw_view);
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
        folderLocation = "/storage/emulated/0/Download/lena.png"; // 512px
//        folderLocation = "/storage/emulated/0/Download/image2.jpg"; // 1024px

        File imgFile = new File(folderLocation);

        if (imgFile.exists()){
            FaceDetector fd = FaceDetector.getInstance();
            WFaceRecognizer fr = WFaceRecognizer.getInstance();

            // Init detector Proses Image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inMutable = true;
            Bitmap b = BitmapFactory.decodeFile(folderLocation, options);

            Canvas c = new Canvas(b);
            fd.setResolution(b.getWidth(), b.getHeight());
//            fd.lock();

            // Get Bytes of Image
            File file = new File(folderLocation);
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            fd.onCameraFrameInjected(bytes);
            fd.onDrawView(c);

            if (fd.hasDetected()) {
                //
                // if face predicted, show the name to user
                //
                Log.e(TAG, "loadFile: "+"detected face" );
                fr.onDrawView(c);
            } else {
                //
                // otherwise, clear the previous results
                //
                fr.clear();
            }
//            Log.e(TAG, "loadFile: "+ Arrays.toString(bytes));

//            onDrawView: YUVdata Mat [ 1620*1920*CV_8UC1, isCont=true, isSubmat=false, nativeObj=0xfffffffff4b07f28, dataAddr=0xffffffffdad40010 ]
//            onDrawView: YUVdata Mat [ 0*0*CV_8UC1, isCont=false, isSubmat=false, nativeObj=0xfffffffff4b07d68, dataAddr=0x0 ] --> File
//            Mat mat = fd.getDetectedFaceForDisplaying();
//            Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(mat, bitmap);
//            imgDisp.setImageBitmap(bitmap);

//            WFaceRecognizer wfr = WFaceRecognizer.getInstance();
//            String result = wfr.getResult();
//            if (result != null) {
//                TextView textView = (TextView) findViewById(R.id.textview_person);
//                if (result.equals("Unknown")) {
//                    textView.setText("Sorry, unknown");
//                } else {
//                    textView.setText(result + " (" + (int)wfr.getConfidence() + ")");
//                }
//            }

            processPhoto();

            // Recognize
//            WFRDataFactory wdf = WFRDataFactory.getInstance();
//            Vector<WFRFaceImage> images = wdf.getFaceImages();

//            int[] label = new int[1];
//            double[] confidence = new double[1];
//
//            if (null == mReadable) {
//                Log.e(TAG, "this.mReadable is not ready.");
//                mPredictResult = null;
////                return false;
//            } else {
//                Log.d(TAG, "call opencv.predict():");
//                mReadable.predict(mDetectedFace, label, confidence);
//                Log.d(TAG, "opencv.predict() returned, label[0]=" + label[0]);
//
//            }


//            WFaceRecognizer wfr = WFaceRecognizer.getInstance();
//            wfr.onDrawView(null, mDetectedFace);
//            String result = wfr.getResult();
//            Log.e(TAG, "loadFile: "+ result );
//            if (result != null) {
//
//                TextView textView = (TextView) findViewById(R.id.textview_person);
//                if (result.equals("Unknown")) {
//
//                     Add record
//
//                     processImage();
//                    textView.setText("Sorry, unknown");
//                } else {
//                    textView.setText(result + " (" + (int)wfr.getConfidence() + ")");
//                }
//            }

//            Toast.makeText(getApplicationContext(), "Load File Success : "+filename, Toast.LENGTH_SHORT).show();

        } else {
            Log.e(TAG, "loadFile: "+"Image Not found" );
        }


    }

    private void processPhoto() {
        image = Highgui.imread(folderLocation);
        String mCascadeFileName = "lppcascade.xml";

        File cascadeDir = getApplicationContext().getDir("cascade", Context.MODE_PRIVATE);
        File mCascadeFile = new File(cascadeDir, mCascadeFileName);
        CascadeClassifier mDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());

        MatOfRect faceDetection = new MatOfRect();
        mDetector.detectMultiScale(image, faceDetection);

        Log.e(TAG, String.format("loadFile: "+"Detected %s faces", faceDetection.toArray().length ));

        // Get Name of file
        filename = new File(folderLocation).getName();
        int pos = filename.lastIndexOf(".");
        if (pos > 0) {
            filename = filename.substring(0, pos);
        }
        Log.e(TAG, "loadFile: "+ filename );

        mDetectedFace = new Mat();
        Rect[] faces = faceDetection.toArray();

        int i = 0;
        for (Rect rect : faces){
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));

            mDetectedFace = image.submat(
                    faces[i].y,
                    faces[i].y + faces[i].height,
                    faces[i].x,
                    faces[i].x + faces[i].width
            );
            i++;
            // Save to local file
//                SaveImage(image);
        }

        Log.e(TAG, "loadFile: "+ mDetectedFace.toString() );
//            ShowImage();

        // Display Image for Result Detected Face(s)
        Mat tmp = new Mat (image.height(), image.width(), CvType.CV_8U, new Scalar(4));
        Mat tmpfaces = new Mat (image.height(), image.width(), CvType.CV_8U, new Scalar(4));
        try {
            Imgproc.cvtColor(image, tmp, Imgproc.COLOR_RGB2BGRA);
            Mat mImageGray = mDetectedFace.submat(0, mDetectedFace.height(), 0, mDetectedFace.width());
            Imgproc.cvtColor(mImageGray, tmpfaces, Imgproc.COLOR_RGB2BGRA);
//                Imgproc.cvtColor(image, tmp, Imgproc.COLOR_RGB2GRAY);
//                Imgproc.cvtColor(image, tmp, Imgproc.COLOR_GRAY2RGBA, 4);

            mutableBitmap = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            mutableBitmapFace = Bitmap.createBitmap(tmpfaces.cols(), tmpfaces.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, mutableBitmap);
            Utils.matToBitmap(tmpfaces, mutableBitmapFace);

        } catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
        imgDisp.setImageBitmap(mutableBitmap);
        imgFace.setImageBitmap(mutableBitmapFace);
        tvFace.setText(filename);
    }

    private void processImage() {

        WFaceRecognizer wfr = WFaceRecognizer.getInstance();
        WFRDataFactory factory = WFRDataFactory.getInstance();
        LocalNameList lnlist = LocalNameList.getInstance();

//        String instr = filename;
        Log.e(TAG, "processImage: "+ filename );
        lnlist.inputLocalName(filename);
        int index = lnlist.findExistNameLocation(filename);
        if (-1 == index) {
            Log.e(TAG, "WRONG NAME, WHY?");
            WhooConfig.DBG(LandingPage.this, "WRONG NAME, WHY?");
            return;
        }

        String name = lnlist.getLocalName(index);

        WFRPerson person = factory.addPerson(name);
        if (null == person) {
            Log.e(TAG, "Add Person Failed, WHY?");
            WhooConfig.DBG(LandingPage.this, "Add Person Failed, WHY?");
            return;
        }

//        Mat mat = fd.getDetectedFace();

        Log.e(TAG, "loadFile: "+ mDetectedFace.empty() );
        assert (mDetectedFace != null);
        // resize the image to normalized size.
        mDetectedFace = WhooTools.resize(mDetectedFace);

        boolean ret = person.addFaceImage(mDetectedFace);
        if (!ret) {
            Log.e(TAG, "Add Image Failed, WHY?");
            WhooConfig.DBG(LandingPage.this, "Add Image Failed, WHY?");
            return;
        } else {
            WhooConfig.DBG(LandingPage.this, "A face image added for " + name + " !");
            Log.e(TAG, "processImage: "+ "A face image added for " + name + " !" );
        }

        // call FR.train() now, maybe it will run later on.
        wfr.train();

        // let the faceActivity exit
//            FaceActivity.this.finish();

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

    public void SaveImage (Mat image) {
                Boolean bool = null;
                String newfilename = "faceDetection.png";

                File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
                path.mkdirs();

                File file = new File(path, "image.png");
                afilename = file.toString();

                bool = Highgui.imwrite(afilename, image);

                System.out.println(String.format("Writing %s", newfilename));

                if (bool)
                    Log.e(TAG, "loadFile: "+ "SUCCESS writing image to external storage" );
                else
                    Log.e(TAG, "Fail writing image to external storage");
    }
}
