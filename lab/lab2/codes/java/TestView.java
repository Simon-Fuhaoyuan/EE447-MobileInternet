package sjtu.iiot.wi_fi_scanner_iiot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

class TestView extends View {

    public Canvas canvas;
    public Paint p;
    private Bitmap bitmap;
    private Intent getIntent;

    int width, height;
    float wifi1_x, wifi1_y, wifi2_x, wifi2_y, wifi3_x, wifi3_y;
    float padding_x, padding_y;
    float R1, R2, R3;
    float Pos_x, Pos_y;
    int bgColor;

    final float EDGE_HEIGHT = 1500;
    final float EDGE_WIDTH = 900;
    final float PIXELS_PER_METER = 300;

    public TestView(Context context, Intent getIntent) {
        super(context);

        // 获取手机分辨率
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        width = dm2.widthPixels;
        height = dm2.heightPixels;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        canvas=new Canvas();
        canvas.setBitmap(bitmap);
        p = new Paint(Paint.DITHER_FLAG);
        p.setAntiAlias(true);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(8);

        this.getIntent = getIntent;
        initParameter();
        Draw();
        makeLabels();
    }
    // The room is 3m * 5m
    // The screen for room is 900 * 1500, 300 pixels for 1m
    private void initParameter() {
        bgColor = Color.WHITE;
        padding_x = (width - EDGE_WIDTH) / 2;
        padding_y = 50;
        // wifi1 (1,1)
        wifi1_x = padding_x + 1 * PIXELS_PER_METER;
        wifi1_y = padding_y + 1 * PIXELS_PER_METER;
        // wifi2 (1,4)
        wifi2_x = padding_x + 1 * PIXELS_PER_METER;
        wifi2_y = padding_y + 4 * PIXELS_PER_METER;
        // wifi3 (2,2.5)
        wifi3_x = padding_x + 2 * PIXELS_PER_METER;
        wifi3_y = padding_y + (float)2.5 * PIXELS_PER_METER;
        R1 = getIntent.getFloatExtra("R1", 0) * PIXELS_PER_METER;
        R2 = getIntent.getFloatExtra("R2", 0) * PIXELS_PER_METER;
        R3 = getIntent.getFloatExtra("R3", 0) * PIXELS_PER_METER;
        Pos_x = padding_x + getIntent.getFloatExtra("Pos_x", 0) * PIXELS_PER_METER;
        Pos_y = padding_y + getIntent.getFloatExtra("Pos_y", 0) * PIXELS_PER_METER;
    }

    private void Draw() {
        // background
        p.setColor(bgColor);
        canvas.drawRect(0, 0, width, height, p);

        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.YELLOW);
        p.setStrokeWidth(16);
        canvas.drawPoint(wifi1_x, wifi1_y, p);
        p.setStrokeWidth(8);
        canvas.drawCircle(wifi1_x, wifi1_y, R1, p);

        p.setColor(Color.GREEN);
        p.setStrokeWidth(16);
        canvas.drawPoint(wifi2_x, wifi2_y, p);
        p.setStrokeWidth(8);
        canvas.drawCircle(wifi2_x, wifi2_y, R2, p);

        p.setColor(Color.BLUE);
        p.setStrokeWidth(16);
        canvas.drawPoint(wifi3_x, wifi3_y, p);
        p.setStrokeWidth(8);
        canvas.drawCircle(wifi3_x, wifi3_y, R3, p);

        p.setStrokeWidth(24);
        p.setColor(Color.RED);
        canvas.drawPoint(Pos_x, Pos_y, p);

        p.setStrokeWidth(8);
        p.setColor(Color.BLACK);
        canvas.drawRect(padding_x, padding_y, padding_x + EDGE_WIDTH, padding_y + EDGE_HEIGHT, p);

        p.setStyle(Paint.Style.FILL);
        p.setColor(bgColor);
        canvas.drawRect(0, 0, width, padding_y, p);
        canvas.drawRect(0, 0, padding_x, height, p);
        canvas.drawRect(0, padding_y + EDGE_HEIGHT, width, height, p);
        canvas.drawRect(padding_x + EDGE_WIDTH, 0, width, height, p);
    }

    private void makeLabels() {
        p.setColor(Color.BLACK);
        p.setTextSize(40);
        canvas.drawText("Wifi_1", wifi1_x + 20, wifi1_y - 20, p);
        canvas.drawText("Wifi_2", wifi2_x + 20, wifi2_y - 20, p);
        canvas.drawText("Wifi_3", wifi3_x + 20, wifi3_y - 20, p);
        canvas.drawText("Your Pos", Pos_x - 200, Pos_y - 20, p);
        String pos = "(" + String.valueOf(getIntent.getFloatExtra("Pos_x", 0)) +
                "," + String.valueOf(getIntent.getFloatExtra("Pos_y", 0)) + ")";
        canvas.drawText(pos, Pos_x - 200, Pos_y + 20, p);
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawBitmap(bitmap, 0, 0, null);
    }
}
