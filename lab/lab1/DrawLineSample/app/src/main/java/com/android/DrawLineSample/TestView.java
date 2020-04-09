package com.android.DrawLineSample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

class TestView extends View {

	public Canvas canvas;
	public Paint p;
	private Bitmap bitmap;
	float x,y;
	int color_idx;
	int[] rs, gs, bs;
	int bgColor;
	float b1_x, b1_y, b1_w, b1_h;
	boolean press_clear;

	public TestView(Context context) {
		super(context);
		press_clear = false;
		bgColor = Color.WHITE;            //设置背景颜色
		// 获取手机分辨率
		DisplayMetrics dm2 = getResources().getDisplayMetrics();
		int width = dm2.widthPixels;
		int height = dm2.heightPixels;
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);    //设置位图，线就画在位图上面，第一二个参数是位图宽和高
		setColor(); // 设置色域
		canvas=new Canvas();
		canvas.setBitmap(bitmap);       
		p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true);                //设置抗锯齿，一般设为true
		// 设置颜色
		p.setColor(Color.rgb(rs[color_idx], gs[color_idx], bs[color_idx]));
		p.setStrokeCap(Paint.Cap.ROUND);     //设置线的类型
		p.setStrokeWidth(8);                //设置线的宽度

		setButtons();
	}
    
	
	//触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		p.setColor(Color.rgb(rs[color_idx], gs[color_idx], bs[color_idx]));

		if (event.getAction() == MotionEvent.ACTION_MOVE) {    //拖动屏幕
			if (!press_clear) {
				canvas.drawLine(x, y, event.getX(), event.getY(), p);   //画线，x，y是上次的坐标，event.getX(), event.getY()是当前坐标
				color_idx = (color_idx + 1) % 65;
				invalidate();
			}
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {    //按下屏幕
			x = event.getX();				
			y = event.getY();
			if (x >= b1_x && x < b1_x + b1_w && y >= b1_y && y <= b1_y + b1_h) {
				press_clear = true;
			}
			else {
				canvas.drawPoint(x, y, p);                //画点
				color_idx = (color_idx + 1) % 65;
				invalidate();
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {    //松开屏幕
			if (press_clear) {
				clearCanvas();
				setButtons();
				press_clear = false;
			}
		}
		x = event.getX();   //记录坐标
		y = event.getY();
		return true;
	}
	
	@Override
	public void onDraw(Canvas c) {
		c.drawBitmap(bitmap, 0, 0, null);
	}

	public void setColor() {
		// 设置颜色色域
		int[]rss = {255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 240, 220, 200,
				180, 160, 140, 120, 100, 80, 60, 40, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 40, 60, 80, 100, 120, 140, 160, 180,
				200, 220, 240};
		int[]gss = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 20, 40, 60, 80, 100, 120, 140, 160, 180, 200, 220, 240,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 };
		int[]bss = { 0, 20, 40, 60, 80, 100, 120, 140, 160, 180, 200, 220, 240,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
				240, 220, 200, 180, 160, 140, 120, 100, 80, 60, 40, 20, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		rs = rss;
		gs = gss;
		bs = bss;
		color_idx = 0;
	}

	public void setButtons() {
		p.setColor(Color.rgb(rs[50], gs[50], bs[50]));
		b1_x = 20; b1_y = 20;
		b1_w = 200; b1_h = 100;
		canvas.drawRect(b1_x, b1_y, b1_x + b1_w, b1_y + b1_h, p);
		p.setColor(Color.BLACK);
		p.setTextSize(50);
		canvas.drawText("CLEAR", 50, 90, p);
	}

	public void clearCanvas() {
		bitmap.eraseColor(Color.TRANSPARENT);
		invalidate();
	}
 }
