package sjtu.iiot.wi_fi_scanner_iiot;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

public class Visualization extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent getIntent = getIntent();
        TestView tv = new TestView(this, getIntent);
        setContentView(tv);
    }
}