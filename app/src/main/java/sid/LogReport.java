package sid;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sid on 7/18/17.
 */

class LogReport {

    Date date = new Date() ;
    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-hhmmss") ;
    String path = Environment.getExternalStorageDirectory().toString();
    File dirLog = new File(path + "/OCVReportFR");
    public File fileLog = new File(dirLog, "OCVFaceData"+ dateFormat.format(date) +".txt");
    BufferedWriter bufw;

    //    public void saveLog(String name, int[] intBitmap, byte[] bytes) {
//    }
    public void saveLog(String name, String result_name, double confidence) {

        // Char comma 44
        // Char LineFeed 10
        // Char ; 59

        try {
            bufw = new BufferedWriter(new FileWriter(fileLog, true));
            bufw.write(name);
            bufw.write(44);
            bufw.write(result_name);
            bufw.write(44);
            bufw.write(String.valueOf(confidence));

//            for (int i: facePoints) {
//                bufw.write(44);
//                bufw.write(String.valueOf(i));
//            }

            bufw.write(10);
            bufw.close();
//            bufw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void initLogReport() {

        String[] header_fr = new String[]{};
        String[] header = new String[]{
                "filename",
                "result_name",
        };

        try {
            if (!dirLog.exists()) dirLog.mkdir();
            if (!fileLog.exists()) fileLog.createNewFile();

            bufw = new BufferedWriter(new FileWriter(fileLog, true));
            bufw.write("FILE_NAME");
//            for (String s: header_fr) {
            for (String s: header) {
                bufw.write(44);
                bufw.write(s);
            }
            bufw.write(10);
            bufw.close();
//            bufw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }    }
}
