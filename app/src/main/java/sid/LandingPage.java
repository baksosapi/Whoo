package sid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lamar.cs.whoo.FaceActivity;
import com.lamar.cs.whoo.FaceDetector;
import com.lamar.cs.whoo.LocalNameList;
import com.lamar.cs.whoo.MainActivity2;
import com.lamar.cs.whoo.R;
import com.lamar.cs.whoo.Settings;
import com.lamar.cs.whoo.WFRDataFactory;
import com.lamar.cs.whoo.WFRFaceImage;
import com.lamar.cs.whoo.WFRPerson;
import com.lamar.cs.whoo.WFaceRecognizer;
import com.lamar.cs.whoo.WhooConfig;
import com.lamar.cs.whoo.WhooLog;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import lib.folderpicker.FolderPicker;

import static android.R.attr.height;
import static android.R.attr.logo;
import static android.R.attr.popupAnimationStyle;
import static android.R.attr.width;

/**
 * Created by sid on 7/11/17.
 */

public class LandingPage extends Activity {

    private static final int FOLDER_PICKER_CODE = 78;
    private static final int FILE_PICKER_CODE = 786;
    private static final String TAG = LandingPage.class.getSimpleName();
    EditText dirLoc;
    Button btCamera, btLoadFile;
    TextView tvLoc;
    ImageView imgDisp, imgFace;
    RadioButton rbDir, rbFile, rbEigen, rbFisher, rbLbph;
    RadioGroup rbgType, rbgAlg;
    private int chooseType;
    private String folderLocation;
    Mat descriptors2, descriptors1;
    TextView tvFace, tv_percentage;
    Mat image;
    Mat mDetectedFace;
    MatOfKeyPoint keypoints1, keypoints2;
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
    ProgressBar pbar;

    private BackgroundTask mTask;
    private static String m_chosenDir = "";
    LogReport logReport = new LogReport();
    private WFaceRecognizer wfr;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_landing);

        mTask = new BackgroundTask();

//        dirLoc = (EditText) findViewById(R.id.editText);
        btCamera = (Button) findViewById(R.id.btCamera);
        btLoadFile = (Button) findViewById(R.id.btLoadFile);

        rbDir = (RadioButton) findViewById(R.id.rbDir);
        rbFile = (RadioButton) findViewById(R.id.rbFile);
        rbEigen = (RadioButton) findViewById(R.id.rb_eigen);
        rbFisher = (RadioButton) findViewById(R.id.rb_fisher);
        rbLbph = (RadioButton) findViewById(R.id.rb_lbph);

        rbgType = (RadioGroup) findViewById(R.id.rbgType);
        rbgAlg = (RadioGroup) findViewById(R.id.rbgAlg);

        tvLoc = (TextView) findViewById(R.id.tvLoc);

        pbar = (ProgressBar) findViewById(R.id.progbar_h);

        imgDisp = (ImageView) findViewById(R.id.imgDisp);
        imgFace = (ImageView) findViewById(R.id.imgDispFace);
        tvFace = (TextView) findViewById(R.id.tvFace);
        tv_percentage = (TextView) findViewById(R.id.tv_percentage);
//        imgDisp = (ImageView) findViewById(R.id.draw_view);
//        dirLoc.setInputType(InputType.TYPE_NULL);
//        dirLoc.setKeyListener(null);

        tvLoc.setText("No Location selected");

        initListener();

    }

    private void initListener() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LandingPage.this);

        rbgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Log.e(TAG, "onCheckedChanged: "+ checkedId);
                if (checkedId == R.id.rbDir) {
                    Log.e(TAG, "onCheckedChanged: " + "Directory");
                    chooseType = 1;
                } else if (checkedId == R.id.rbFile) {
                    Log.e(TAG, "onCheckedChanged: " + "File");
                    chooseType = 0;
                }
            }
        });

        btLoadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (folderLocation == null) {

                    builder.setTitle("Directory didn't selected?");
                    builder.setMessage("Operation aborted, please Select Directory or 'OK' to default directory.");
                            m_chosenDir = String.valueOf(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/SID2"));
                    builder.setPositiveButton("OK", eksekusi());
                    builder.setNegativeButton("CANCEL", null);
                    builder.show();
                    Log.e(TAG, "onClick: " + new File(m_chosenDir).exists());
                } else {
                    m_chosenDir = folderLocation;
                }

                if (new File(m_chosenDir).exists() && !LandingPage.m_chosenDir.isEmpty()) {

                    // Execution main process
//                    task.execute();
                    if (mTask == null) {
                        mTask = new BackgroundTask();
                    }
                    new BackgroundTask().execute();
//                    mTask = (BackgroundTask) new BackgroundTask().execute();
//                    btn_start.setClickable(false);
                    Toast.makeText(LandingPage.this, "Mode : Batch Mode ", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private DialogInterface.OnClickListener eksekusi() {
        Log.e(TAG, "eksekusi: " );
        mTask.execute();
        return null;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause() called.");

        super.onPause();

        if (isFinishing()) {
            //
            // Exit by user pressing KEY_BACK.
            //
//            Settings.getInstance().sync();
//            LocalNameList.getInstance().store();
            //FaceDetector.getInstance().destroy();
            WhooLog.close();
        } else {
            // Exit for other reasons.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void openCamera(View view) {

        Intent intent = new Intent(LandingPage.this, MainActivity2.class);
        startActivity(intent);

    }

    public void loadFile(File imgFile) {
//        Intent intent = new Intent(LandingPage.this, ImageLoad.class);
//        startActivity(intent);

//        folderLocation = "/storage/emulated/0/Download/Donal1.jpg";
//        folderLocation = "/storage/emulated/0/Download/lena.png"; // 512px
//        folderLocation = "/storage/emulated/0/Download/image2.jpg"; // 1024px
//        folderLocation = "/storage/emulated/0/Download/scarlett_johansson.png"; // 1920px
//        folderLocation = "/storage/emulated/0/Download/10006868.jpg"; // 1920px
//        folderLocation = "/storage/emulated/0/Download/nail.png"; // 1920px
//        folderLocation = "/storage/emulated/0/Download/balsamic.png"; // 1920px

//        mTask.execute();

        String fileLoc = imgFile.toString();

        filename = imgFile.getName();
        int pos = filename.lastIndexOf(".");
        if (pos > 0) {
            filename = filename.substring(0, pos);
        }
        Log.e(TAG, "loadFile: FileName " + filename);

//        File imgFile = new File(folderLocation);

        if (imgFile.exists()) {
            FaceDetector fd = FaceDetector.getInstance();
            wfr = WFaceRecognizer.getInstance();

            // Init detector Proses Image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inMutable = true;
            Bitmap b = BitmapFactory.decodeFile(fileLoc, options);

            Canvas c = new Canvas(b);
            fd.setResolution(b.getWidth(), b.getHeight());
//            fd.lock();
            Mat photo = Highgui.imread(fileLoc);
            // Get Bytes of Image
//            File file = new File(folderLocation);
//            int size = (int) file.length();
//            byte[] bytes = new byte[size];
//            try {
//                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//                buf.read(bytes, 0, bytes.length);
//                buf.close();
//            } catch (FileNotFoundException e) {
//                 TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                 TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            WhooLog.d("File Name = "+ filename);
//
            fd.onCameraFrameInjected(photo);
            fd.onDrawView(c);

            if (fd.hasDetected()) {
                //
                // if face predicted, show the name to user
                //
                wfr.onDrawView(c);

                String nameResult = wfr.getResult();
                Double matchingScore = wfr.getConfidence();

                Log.e(TAG, "loadFile: nameResult " + nameResult + " confidence: " + matchingScore);

                if (nameResult == null) {
                    nameResult = "anonym";

//                    processImage(filename, fd.getDetectedFace());

                }
//                processImage(filename, fd.getDetectedFace());

//                if (matchingScore == null) matchingScore = 0d;

//                logReport.saveLog(filename, nameResult, matchingScore);

                //Display
//                showPhoto(photo, c, fd.getDetectedFaceForDisplaying(), fr.getResult() +" "+ Math.round(fr.getConfidence()));
//                processPhoto(photo);

            } else {
                //
                // otherwise, clear the previous results
                //
                wfr.clear();
            }
//            Toast.makeText(getApplicationContext(), "Load File Success : "+ fr.getResult(), Toast.LENGTH_SHORT).show();

        } else {
            Log.e(TAG, "loadFile: " + "Image Not found");
        }
    }

    private void showPhoto(Mat photo, Canvas c, Mat detectedFaceForDisplaying, String result) {

        // Display Image for Result Detected Face(s)

        Mat tmp = new Mat(photo.height(), photo.width(), CvType.CV_8U, new Scalar(4));
        Mat tmpfaces = new Mat(detectedFaceForDisplaying.height(), detectedFaceForDisplaying.width(), CvType.CV_8U, new Scalar(4));
        try {
            Imgproc.cvtColor(photo, tmp, Imgproc.COLOR_RGB2BGRA);
            Mat faces = detectedFaceForDisplaying.submat(0, detectedFaceForDisplaying.height(), 0, detectedFaceForDisplaying.width());

            // Flip/Mirror Image for Display
            Core.flip(faces, faces, 1);

            Imgproc.cvtColor(faces, tmpfaces, Imgproc.COLOR_RGB2BGRA);
//            Imgproc.cvtColor(image, tmp, Imgproc.COLOR_RGB2GRAY);
//            Imgproc.cvtColor(image, tmp, Imgproc.COLOR_GRAY2RGBA, 4);

            mutableBitmap = Bitmap.createBitmap(tmp.cols(), tmp.rows(), Bitmap.Config.ARGB_8888);
            mutableBitmapFace = Bitmap.createBitmap(tmpfaces.cols(), tmpfaces.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(tmp, mutableBitmap);
            Utils.matToBitmap(tmpfaces, mutableBitmapFace);

        } catch (CvException e) {
            Log.d("Exception", e.getMessage());
        }


        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        c.drawBitmap(bitmap, 0, 0, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        c.drawCircle(50, 50, 10, paint);
        imgDisp.setImageBitmap(bitmap);
//        imgDisp.setImageDrawable(new BitmapDrawable(getResources(), mutableBitmap ));
        imgFace.setImageBitmap(mutableBitmapFace);
        tvFace.setText(result);
    }

    private void processPhoto(Mat image) {
        // Detector
        //
        String mCascadeFileName = "lppcascade.xml";
        File cascadeDir = getApplicationContext().getDir("cascade", Context.MODE_PRIVATE);
        File mCascadeFile = new File(cascadeDir, mCascadeFileName);
        CascadeClassifier mDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());

        MatOfRect faceDetection = new MatOfRect();
        mDetector.detectMultiScale(image, faceDetection);

        Log.e(TAG, String.format("loadFile: " + "Detected %s faces", faceDetection.toArray().length));

        // Get Name of file
        filename = new File(folderLocation).getName();
        int pos = filename.lastIndexOf(".");
        if (pos > 0) {
            filename = filename.substring(0, pos);
        }

        mDetectedFace = new Mat();
        Rect[] faces = faceDetection.toArray();

        int i = 0;
        for (Rect rect : faces) {
            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(10, 255, 0));

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

        Log.e(TAG, "loadFile: " + mDetectedFace.toString());
//            ShowImage();

        // Display Image for Result Detected Face(s)
        Mat tmp = new Mat(image.height(), image.width(), CvType.CV_8U, new Scalar(4));
        Mat tmpfaces = new Mat(image.height(), image.width(), CvType.CV_8U, new Scalar(4));
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

        } catch (CvException e) {
            Log.d("Exception", e.getMessage());
        }
        imgDisp.setImageBitmap(mutableBitmap);
        imgFace.setImageBitmap(mutableBitmapFace);
        tvFace.setText(filename);
    }

    private void processImage(String filename, Mat mDetectedFace) {

        Log.e(TAG, "processImage: " + "called");
        wfr = WFaceRecognizer.getInstance();
        WFRDataFactory factory = WFRDataFactory.getInstance();
        LocalNameList lnlist = LocalNameList.getInstance();

//        String instr = filename;
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
            Log.e(TAG, "processImage: " + "A face image added for " + name + " !");
        }

        // call wfr.train() now, maybe it will run later on.
        Log.e(TAG, "processImage: train next");
        wfr.train();

        Log.e(TAG, "processImage: ");

//        if(thread.getState()!=Thread.State.TERMINATED){ }

        // let the faceActivity exit
//            LandingPage.this.finish();

    }

    public void setLocation(View view) {
        int picker_code = 100;

        Log.e(TAG, "setLocation: " + chooseType);
        Intent intent = new Intent(this, FolderPicker.class);
//        startActivityForResult(intent, FOLDER_PICKER_CODE);

        if (chooseType == 1) {
            startActivityForResult(intent, FOLDER_PICKER_CODE);
        } else if (chooseType == 0) {
            intent.putExtra("title", "Select file to upload");
            intent.putExtra("location", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
            intent.putExtra("pickFiles", true);
            startActivityForResult(intent, FILE_PICKER_CODE);
//
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        m_chosenDir = folderLocation;


        if (requestCode == FOLDER_PICKER_CODE && resultCode == Activity.RESULT_OK) {

            Log.e(TAG, "onActivityResult: " + intent);
            folderLocation = intent.getExtras().getString("data");
            Log.i("folderLocation", folderLocation);
            tvLoc.setText(folderLocation);

        } else if (requestCode == FILE_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult: " + intent);
            folderLocation = intent.getExtras().getString("data");
            Log.i("folderLocation", folderLocation);
            tvLoc.setText(folderLocation);

        }
    }

    public void SaveImage(Mat image) {
        Boolean bool = null;
        String newfilename = "faceDetection.png";

        File path = new File(Environment.getExternalStorageDirectory() + "/Images/");
        path.mkdirs();

        File file = new File(path, "image.png");
        afilename = file.toString();

        bool = Highgui.imwrite(afilename, image);

        System.out.println(String.format("Writing %s", newfilename));

        if (bool)
            Log.e(TAG, "loadFile: " + "SUCCESS writing image to external storage");
        else
            Log.e(TAG, "Fail writing image to external storage");
    }

    private class BackgroundTask extends AsyncTask<String, Integer, List<RowItem>> {

        @Override
        protected void onPreExecute() {
//            Toast.makeText(LandingPage.this, "onPreExecute", Toast.LENGTH_LONG).show();
            myProgress = 0;
        }

        @Override
        protected void onPostExecute(List<RowItem> aVoid) {
//            Toast.makeText(LandingPage.this, "onPostExecute", Toast.LENGTH_LONG).show();
            btLoadFile.setClickable(true);
            mTask = null;
        }

//        @Override
//        protected Void doInBackground(Void... params) {
//            while (myProgress<100){
//                myProgress++;
//                publishProgress(myProgress);
//                SystemClock.sleep(100);
//            }
//            return null;
//        }

        int myProgress;
        private Activity context;
        List<RowItem> rowItems;
        int noOfPaths;
        private File[] list;

        @Override
        protected List<RowItem> doInBackground(String... urls) {
            noOfPaths = urls.length;
            rowItems = new ArrayList<RowItem>();

            Log.e(TAG, "doInBackground: " + noOfPaths);

            String strDir = LandingPage.m_chosenDir;

            if (new File(strDir).isDirectory()) {

                if (strDir.isEmpty()) {
                    Log.e(TAG, "doInBackground: " + "Directory Empty");

                } else {

                    File dir = new File(strDir);

                    list = dir.listFiles();

                    Log.e(TAG, "doInBackground: numfiles " + list.length);
                    Log.e(TAG, "doInBackground: files " + Arrays.toString(list));

                    // Initialize Log Report File
//                logReport.initLogReport();

                    for (File f : list) {

                        loadFile(f);

                        myProgress++;

                        Log.e(TAG, "doInBackground: train myProgress " + myProgress);
                        publishProgress(myProgress * 100 / list.length);
//                    rowItems.add(new RowItem(map));

                    }

                    WFRDataFactory.getInstance().flush();
                    Settings.getInstance().sync();
                    LocalNameList.getInstance().store();

                    Log.e(TAG, "doInBackground: " + "saveLog to : " + logReport.fileLog.toString());

                }
            } else {
                loadFile(new File(strDir));
            }

            return rowItems;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pbar.setProgress(values[0]);
            tv_percentage.setText(String.format("%s %%", values[0].toString()));
            Log.e(TAG, "onProgressUpdate: values " + values[0]);
        }
    }
}
