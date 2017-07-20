package sid.gallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lamar.cs.whoo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import sid.LogReport;

public class Main3Activity extends Activity {
    public static final String ALBUM_NAME = "serialize_deserialize";
    private final String TAG = Main3Activity.class.getSimpleName();
    private static String m_chosenDir = "";
    RadioButton rb_addrecord;
    RadioButton rb_identify;
    Button btn_loadDir, btn_start, btn_pause, btn_stop, btn_loadTest, btn_bar;
    TextView tv_percentage;
    TextView tv_guide;
    TextView tv_outof;
    TextView tv_folder_uri;
    ProgressBar progressBar;
    ListView listView;

    private int batchMode = 2;
    private int ADD_RECORDS = 0;
    private int IDENTIFY = 1;
    private boolean batchModeSelected = false;
    private HashMap<String, String> hash;
    private ProgressBar pb_mypb;
    private int[] intBitmap;
//    private FacialProcessing mFaceObj;
//    private FaceData[] mFaceData;
//    private SidFaceActivity mSidFace;
    private CustomListViewAdapter listViewAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initView();
        initListeners();
    }

    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
    }

    protected void onStop(){
        super.onStop();
        m_chosenDir = "";
    }

    protected void onDestroy(){
        super.onDestroy();
        m_chosenDir = "";
    }

    private void initView() {
        tv_folder_uri = (TextView) findViewById(R.id.folder_uri);
        btn_loadDir = (Button) findViewById(R.id.action_load_dir);
        btn_start = (Button) findViewById(R.id.start_load);
        btn_pause = (Button) findViewById(R.id.pause_load);
        btn_stop = (Button) findViewById(R.id.stop_load);
        progressBar = (ProgressBar) findViewById(R.id.progbar_h);
        tv_percentage = (TextView) findViewById(R.id.tv_percentage);
        tv_outof = (TextView) findViewById(R.id.tv_outof);
        tv_guide = (TextView) findViewById(R.id.tv_guidance);
        rb_addrecord = (RadioButton) findViewById(R.id.rb_addrecord);
        rb_identify = (RadioButton) findViewById(R.id.rb_identify);
        listView = (ListView) findViewById(R.id.lv_thumbnail);

//        tv_guide.setText("Choose Mode, Choose Directory");
//        btn_loadTest = (Button) findViewById(R.id.bt_loadTest);
//        btn_bar = (Button) findViewById(R.id.bt_bar);
//        pb_mypb = (ProgressBar) findViewById(R.id.progressBar2);

    }

    private void initListeners() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(Main3Activity.this);

        btn_loadDir.setOnClickListener(new View.OnClickListener() {

            private boolean m_newFolderEnabled = true;

            @Override
            public void onClick(View v) {
//                Toast.makeText(LoadGallery.this, "loadGallery", Toast.LENGTH_SHORT).show();
//                Create DirectoryChooserDialog and register a callback
                DirectoryChooserDialog directoryChooserDialog =
                        new DirectoryChooserDialog(Main3Activity.this,
                                new DirectoryChooserDialog.ChosenDirectoryListener()
                                {
                                    @Override
                                    public void onChosenDir(String chosenDir)
                                    {
                                        m_chosenDir = chosenDir;

                                        Toast.makeText(
                                                Main3Activity.this, "Chosen directory: " +
                                                        chosenDir, Toast.LENGTH_LONG).show();
                                        tv_folder_uri.setText(m_chosenDir);

                                    }
                                });
                // Toggle new folder button enabling
                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                // Load directory chooser dialog for initial 'm_chosenDir' directory.
                // The registered callback will be called upon final directory selection.
                directoryChooserDialog.chooseDirectory(m_chosenDir);
                m_newFolderEnabled = ! m_newFolderEnabled;
            }
        });

        rb_addrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        rb_identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        m_chosenDir = String.valueOf(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/SID"));

        btn_start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check Mode to Operate Add new or Indentify
                if(batchMode == 1 || batchMode == 0 ){

                    ImageProcessingTask task = new ImageProcessingTask(Main3Activity.this);

                    Log.e(TAG, "onClick: "+new File(m_chosenDir).exists() );
                    if(new File(m_chosenDir).exists() && !Main3Activity.m_chosenDir.isEmpty()) {

                        // Execution main process
                        task.execute();
//                    mTask = (BackgroundTask) new BackgroundTask().execute();
//                    btn_start.setClickable(false);
                        Toast.makeText(Main3Activity.this, "Mode : "+ batchMode, Toast.LENGTH_SHORT).show();
                    } else {
                        builder.setTitle("Directory didn't selected?");
                        builder.setMessage("Operation aborted, please Select Directory or 'OK' to default directory.");
                        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) task.execute());
                        builder.setNegativeButton("CANCEL", null);
                        builder.show();

                    }
                } else {
                    builder.setTitle("Mode didn't selected?")
                            .setMessage("Operation aborted, please Select mode")
                            .setPositiveButton("OK", null)
//                    builder.setNegativeButton("CANCEL", null);
                            .setCancelable(true)
                            .show();

                }
            }
        });

        btn_stop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mTask.cancel(true);
                btn_start.setClickable(true);
            }
        });


    }

    private void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton)view).isChecked();

        switch (view.getId()){
            case R.id.rb_addrecord:
                if (checked)
                    Toast.makeText(this, "Add New Mode is Selected", Toast.LENGTH_SHORT).show();
                batchMode = ADD_RECORDS;
                break;
            case R.id.rb_identify:
                if (checked)
                    Toast.makeText(this, "Identify Mode is Selected", Toast.LENGTH_SHORT).show();
                batchMode = IDENTIFY;
                break;

        }
    }

    private class ImageProcessingTask extends AsyncTask<String, Integer, List<RowItem>> {

        int myProgress;
        private Activity context;
        List<RowItem> rowItems;
        int noOfPaths;
        private File[] list;
        LogReport logReport = new LogReport();

        ImageProcessingTask(Activity context){
            this.context = context;
        }

        @Override
        protected List<RowItem> doInBackground(String... urls){
            noOfPaths = urls.length;
            rowItems = new ArrayList<RowItem>();
            Bitmap map;

            Log.e(TAG, "doInBackground: "+noOfPaths );

            String strDir = Main3Activity.m_chosenDir;

            if(strDir.isEmpty()){
                Log.e(TAG, "doInBackground: "+"Directory Empty" );

            } else {

                File dir = new File(strDir);

                list = dir.listFiles();

                Log.e(TAG, "doInBackground: numfiles "+list.length );
                Log.e(TAG, "doInBackground: files "+ Arrays.toString(list));

                // Initialize Log Report File
//                logReport.initLogReport();

                for (File f: list){

                    processPhoto(f);

                    myProgress++;

                    Log.e(TAG, "doInBackground: myProgress "+ myProgress );
                    publishProgress(myProgress*100/list.length);
//                    rowItems.add(new RowItem(map));

                }

//                Log.e(TAG, "doInBackground: "+"saveLog to : "+ logReport.fileLog.toString()  );

//                for (String url : urls) {
//                    map = processPhoto(url);
//                    Log.e(TAG, "doInBackground: ");
//                rowItems.add(new RowItem(map));
//                }
            }
            return rowItems;
        }

        Bitmap bitmap;
        ByteArrayOutputStream stream ;

        private Bitmap processPhoto(File s){
//            s = new File("/storage/emulated/0/DCIM/Missmatch/96006129.jpg");
//            int count = 0;
//            URL url;
            long tFaceData = 0;

            long startProcessPhoto = System.nanoTime();

            bitmap = BitmapFactory.decodeFile(s.getAbsolutePath());

            long tBitmapFactory = System.nanoTime() - startProcessPhoto;

//            Log.e(TAG, "processPhoto: "+ bitmap.getByteCount() );
            stream = new ByteArrayOutputStream();

            if(bitmap != null){
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
            }

            long tCompressBitmap = System.nanoTime() - startProcessPhoto;


//            Log.e(TAG, "processPhoto: "+ bitmap.getByteCount() );
            byte[] bytes = stream.toByteArray();

            Bitmap storedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

//            Log.e(TAG, "processPhoto: storedBitmap"+ storedBitmap.getByteCount() );

//            mFaceObj = SidFaceActivity.faceObj;

//            boolean initFaceObj = mFaceObj.setBitmap(storedBitmap);
            boolean initFaceObj = true;

            long tSetBitmap = System.nanoTime() - startProcessPhoto;

            // Check if setBitmap Success
            if (initFaceObj) {
                // TODO: Handle mFaceData null cause wrong filetypes

//                mFaceData = mFaceObj.getFaceData();
                String mFaceData = "";

                tFaceData = System.nanoTime() - startProcessPhoto;

//                Log.e(TAG, "processPhoto: "+mFaceObj );

                if(mFaceData != null){

//                    Log.e(TAG, "processPhoto: "+s+" Rect.Length "+mFaceData.length );
//                    Log.e(TAG, "processPhoto: "+s.getName()+" personId: "+mFaceData[0].getPersonId() +
//                            " confidence: "+mFaceData[0].getRecognitionConfidence() +
//                    " orientation "+ storedBitmap.getWidth());

//                  Only single face
//                    if(mFaceData[0].getPersonId() < 0){
//                        if(hash.containsKey(s.getName().replace(".jpg",""))){
//                            Log.e(TAG, "processPhoto: "+"name already in used" );
//                        } else {
//                            int result = mFaceObj.addPerson(0);
//
//                            Log.e(TAG, "processPhoto: addPerson: "+ result);
//                            hash.put(s.getName().replace(".jpg",""), Integer.toString(result));
//                            mSidFace.saveHash(hash, getApplicationContext());
//                            saveAlbum();
//                            Log.e(TAG, "processPhoto: "+"Add Success!" );
//                        }
//                    }

                    long tDbEnd = System.nanoTime() - startProcessPhoto;

                    intBitmap = new int[]{
//                            storedBitmap.getByteCount(),

                    };

                } else {

                    Log.e(TAG, "processPhoto: mFaceData "+ s.getName() +" No Face Detected" );

                    intBitmap = new int[]{
                            storedBitmap.getByteCount(),
                            storedBitmap.getWidth(),
                            storedBitmap.getHeight()};
                }
            } else {
                Log.e(TAG, "processPhoto: "+s.getAbsolutePath()+ " setBitmap Failed!" );
                intBitmap = new int[]{
                        storedBitmap.getByteCount(),
                        storedBitmap.getWidth(),
                        storedBitmap.getHeight()};

            }

//            String mFace = Arrays.toString(mFaceObj.serializeRecogntionAlbum());
//            processPhoto: [20, 27, 0, 0, 76, 65, -68, -20, 77,
//            Log.e(TAG, "processPhoto: "+mFace );

//            String[] res = mFace.substring(1, mFace.length() - 1).split(",");

//            Log.e(TAG, "processPhoto: Length "+ res.length  );
//            String[] faceVectorHeader = Arrays.copyOfRange(res, 0, 32);
//            String[] faceVectorContent = Arrays.copyOfRange(res, res.length - 300, res.length);
//            String[] faceVectorContent = new String[]{ "a", "b" };
//
//            logReport.saveLog(s.getName(), intBitmap , Arrays.toString(faceVectorHeader), Arrays.toString(faceVectorContent));

            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
            tv_percentage.setText(String.format("%s %%", values[0].toString()));
            tv_outof.setText(MessageFormat.format(
                    "{0}/{1}", String.valueOf(values[0] * list.length / 100), String.valueOf(list.length)));
            Log.e(TAG, "onProgressUpdate: "+ values[0] );
        }

        @Override
        protected void onPostExecute(List<RowItem> rowItems){

            listViewAdapter = new CustomListViewAdapter(context, rowItems);
            listView.setAdapter(listViewAdapter);
            Toast.makeText(context, "Load Gallery has finished", Toast.LENGTH_SHORT).show();
        }

    }

}
