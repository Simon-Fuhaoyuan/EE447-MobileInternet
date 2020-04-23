package sjtu.iiot.wi_fi_scanner_iiot;

/*****************************************************************************************************************
 * Created by HelloShine on 2019-3-24.
 * ***************************************************************************************************************/
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log; //Log can be utilized for debug.

public class SuperWiFi extends MainActivity{

    /*****************************************************************************************************************
     * When you run the APP in your mobile phone, you can utilize the following code for debug:
     * Log.d("TEST_INFO","Your Own String Type Content Here");
     * You can also generate the String via ("String" + int/double value). for example, "CurTime " + 20 = "CurTime 20"
     * ***************************************************************************************************************/
    private String FileLabelName = "FuHaoyuan";// Define the file Name
    /*****************************************************************************************************************
     * You can define the Wi-Fi SSID to be measured in FileNameGroup, more than 2 SSIDs are OK.
     * It is noting that multiple Wi-Fi APs might share the same SSID such as SJTU.
     * ***************************************************************************************************************/
    private String FileNameGroup[] = {"TP-LINK_E5E2","HUAWEI_MATE20","HUAWEI_P9"}; //"SJTU","IIoT-434",

    private int TestTime = 10;//Number of measurement
    private int ScanningTime = 1000;//Wait for (?) ms for next scan

    private int NumberOfWiFi = FileNameGroup.length;

    // RSS_Value_Record and RSS_Measurement_Number_Record are used to record RSSI values
    private int[] RSS_Value_Record = new int[NumberOfWiFi];
    private int[] RSS_Measurement_Number_Record = new int[NumberOfWiFi];
    private float Pos_x = 0, Pos_y = 0;


    private WifiManager mWiFiManager = null;
    private Vector<String> scanned = null;
    boolean isScanning = false;
    boolean valid = true;

    public SuperWiFi(Context context)
    {
        this.mWiFiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        this.scanned = new Vector<String>();
    }

    private void startScan()//The start of scanning
    {
        this.isScanning = true;
        Thread scanThread = new Thread(new Runnable()
        {
            public void run() {
                scanned.clear();//Clear last result
                for(int index = 1;index <= NumberOfWiFi; index++){
                    RSS_Value_Record[index - 1] = 0;
                    RSS_Measurement_Number_Record[index - 1] = 1;
                }
                int CurTestTime = 1; //Record the test time and write into the SD card
                SimpleDateFormat formatter = new SimpleDateFormat
                        ("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis()); //Get the current time
                String CurTimeString = formatter.format(curDate);
                for(int index = 1;index <= NumberOfWiFi; index++){
                    write2file(FileLabelName + "-" + FileNameGroup[index - 1] + ".txt","Test_ID: " + testID + " TestTime: " + CurTimeString + " BEGIN\r\n");
                }
                //Scan for a certain times
                while(CurTestTime++ <= TestTime) performScan();

                for(int index = 1;index <= NumberOfWiFi; index++){//Record the average of the result
                    scanned.add(FileLabelName + "-" + FileNameGroup[index - 1] + " = " + RSS_Value_Record[index - 1]/ RSS_Measurement_Number_Record[index - 1] + "\r\n");
                }

                // *********************** code for localization *********************************
                for(int index = 1;index <= NumberOfWiFi; index++){//Record the average of the result
                    RSS_Value_Record[index - 1] /= RSS_Measurement_Number_Record[index - 1];
                    if (RSS_Value_Record[index - 1] == 0)
                        valid = false;
                }
                if (valid) {
                    double A[] = {45, 55, 55};
                    double n = 3.25;
                    double d_wifi_1 = Math.pow(10, ((-1 * RSS_Value_Record[0]) - A[0]) / (10 * n));
                    double d_wifi_2 = Math.pow(10, ((-1 * RSS_Value_Record[1]) - A[1]) / (10 * n));
                    double d_wifi_3 = Math.pow(10, ((-1 * RSS_Value_Record[2]) - A[2]) / (10 * n));
                    double y = (d_wifi_1 * d_wifi_1 - d_wifi_2 * d_wifi_2 + 15.0) / 6.0;
                    double x = 0;
                    if (y < 0 || y > 5)
                        valid = false;
                    if (valid) {
                        double x_before_sqrt = d_wifi_1 * d_wifi_1 - (y - 1) * (y - 1);
                        if (x_before_sqrt < 0)
                            valid = false;
                        if (valid) {
                            double x_sqrt = Math.sqrt(x_before_sqrt);
                            double x1 = x_sqrt + 1;
                            double x2 = -1 * x_sqrt + 1;
                            if (x2 < 0 && x1 > 3)
                                valid = false;
                            else if (x2 < 0)
                                x = x1;
                            else if (x1 > 3)
                                x = x2;
                            else {
                                double d_wifi3_1 = (x1 - 2) * (x1 - 2) + (y - 2.5) * (y - 2.5);
                                double d_wifi3_2 = (x2 - 2) * (x2 - 2) + (y - 2.5) * (y - 2.5);
                                if (Math.abs(d_wifi_3 * d_wifi_3 - d_wifi3_1) < Math.abs(d_wifi_3 * d_wifi_3 - d_wifi3_2))
                                    x = x1;
                                else
                                    x = x2;
                            }

                            if (valid) {
                                Pos_x = (float)x;
                                Pos_y = (float)y;
                            }
                        }
                    }
                }
                
                // **************************************************************************************************************
                for(int index = 1;index <= NumberOfWiFi; index++){//Mark the end of the test in the file
                    write2file(FileLabelName + "-" + FileNameGroup[index - 1] + ".txt","testID:"+testID+"END\r\n");
                }
                isScanning=false;
            }
        });
        scanThread.start();
    }

    private void performScan()//The realization of the test
    {
        if(mWiFiManager == null)
            return;
        try
        {
            if(!mWiFiManager.isWifiEnabled())
            {
                mWiFiManager.setWifiEnabled(true);
            }
            mWiFiManager.startScan();//Start to scan
            try {
                Thread.sleep(ScanningTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.scanned.clear();
            List<ScanResult> sr = mWiFiManager.getScanResults();
            Iterator<ScanResult> it = sr.iterator();
            while(it.hasNext())
            {
                ScanResult ap = it.next();
                for(int index = 1;index <= FileNameGroup.length; index++){
                    if (ap.SSID.equals(FileNameGroup[index - 1])){//Write the result to the file
                        RSS_Value_Record[index-1] = RSS_Value_Record[index-1] + ap.level;
                        RSS_Measurement_Number_Record[index - 1]++;
                        write2file(FileLabelName + "-" + FileNameGroup[index - 1] + ".txt",ap.level+"\r\n");
                    }
                }
            }
        }
        catch (Exception e)
        {
            this.isScanning = false;
            this.scanned.clear();
        }
    }




    public void ScanRss(){
        startScan();
    }
    public boolean isscan(){
        return isScanning;
    }
    public boolean isValid() { return valid; }
    public Vector<String> getRSSlist(){
        return scanned;
    }
    public float getPos_x() { return Pos_x; }
    public float getPos_y() { return Pos_y; }

    private void write2file(String filename, String a){//Write to the SD card
        try {
            File file = new File("/sdcard/"+filename);
            if (!file.exists()){
                file.createNewFile();} // Open a random filestream by Read&Write
            RandomAccessFile randomFile = new
                    RandomAccessFile("/sdcard/"+filename, "rw"); // The length of the file(byte)
            long fileLength = randomFile.length(); // Put the writebyte to the end of the file
            randomFile.seek(fileLength);
            randomFile.writeBytes(a);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}