package sjtu.iiot.wi_fi_scanner_iiot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import java.util.Vector;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Context context = this;
    private SuperWiFi rss_scan =null;
    Vector<String> RSSList = null;
    private String testlist=null;
    public static int testID = 0;//The ID of the test result
    private boolean haveTested = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText ipText = (EditText)findViewById(R.id.ipText);//The textlist of the average of the result
        final Button changactivity = (Button)findViewById(R.id.button1);//The start button
        final Button cleanlist = (Button)findViewById(R.id.button2);//Clear the textlist
        final Button visualize = (Button)findViewById(R.id.button3);//Visualize final result
        verifyStoragePermissions(this);
        rss_scan=new SuperWiFi(this);
        testlist="";
        testID=0;
        changactivity.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                haveTested = true;
                testID = testID + 1;
                rss_scan.ScanRss();
                while(rss_scan.isscan()){//Wait for the end
                }
                RSSList=rss_scan.getRSSlist();//Get the test result
                final EditText ipText = (EditText)findViewById(R.id.ipText);
                testlist=testlist+"testID:"+testID+"\n"+RSSList.toString()+"\n";
                ipText.setText(testlist);//Display the result in the textlist
            }
        });
        cleanlist.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                testlist="";
                ipText.setText(testlist);//Clear the textlist
                testID=0;
            }
        });
        visualize.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if (haveTested && rss_scan.isValid()) {
                    Intent intent = new Intent(MainActivity.this, Visualization.class);
                    float Pos_x = rss_scan.getPos_x();
                    float Pos_y = rss_scan.getPos_y();
                    float R1 = (float)Math.sqrt((Pos_x - 1) * (Pos_x - 1) + (Pos_y - 1) * (Pos_y - 1));
                    float R2 = (float)Math.sqrt((Pos_x - 1) * (Pos_x - 1) + (Pos_y - 4) * (Pos_y - 4));
                    float R3 = (float)Math.sqrt((Pos_x - 2) * (Pos_x - 2) + (Pos_y - 2.5) * (Pos_y - 2.5));
                    intent.putExtra("Pos_x", Pos_x);
                    intent.putExtra("Pos_y", Pos_y);
                    intent.putExtra("R1", R1);
                    intent.putExtra("R2", R2);
                    intent.putExtra("R3", R3);
                    startActivity(intent);
                }
                else if (!haveTested) {
                    Toast t = Toast.makeText(context, "You have not scanned, please scan first!", Toast.LENGTH_LONG);
                    t.show();
                }
                else {
                    Toast t = Toast.makeText(context, "Invalid scan result, please scan again!", Toast.LENGTH_LONG);
                    t.show();
                }
            }
        });
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION };
    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
// Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}