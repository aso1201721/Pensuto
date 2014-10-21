package com.example.pensuto;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

/** メインクラスの定義 */
public class Pensuto extends Activity {
	pensutoView view;
	private static final int MENU_CLEAR = 0;
	private static final int MENU_SAVE = 1;
	/** アプリの初期化 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 描画クラスを設定
		view = new pensutoView(getApplication());
		setContentView(view);
		}
	/** メニューの生成イベント */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_CLEAR, 0, "もとにもどす");
		menu.add(0, MENU_SAVE, 0, "これにする");
		return true;
		}
	/** メニューがクリックされた時のイベント */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
		case MENU_CLEAR:
			view.clearDrawList(); break;
			case MENU_SAVE:
				view.saveToFile();
				break;
				}
		return true;
		}
	}
/** 描画クラスの定義 */
class pensutoView extends android.view.View {
	Bitmap bmp = null;
	Canvas bmpCanvas;
	Point oldpos = new Point(-1,-1);
	public pensutoView(Context c) {
		super(c);
		setFocusable(true);
		}
	public void clearDrawList() {
		bmpCanvas.drawColor(Color.WHITE);
		invalidate();
		}
	public void saveToFile() {
		// 保存先の決定
		String status = Environment.getExternalStorageState();
		File fout;
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			fout = Environment.getDataDirectory();
			} else {
				fout = new File("/mnt/sdcard/Pictures/");
				fout.mkdirs();
				}
		Date d = new Date();
		String fname = fout.getAbsolutePath() + "/";
		fname += String.format("%4d%02d%02d-%02d%02d%02d.png",
				(1900+d.getYear()), d.getMonth(), d.getDate(),
				d.getHours(), d.getMinutes(), d.getSeconds());
		// 画像をファイルに書き込む
		try {
			FileOutputStream out = new FileOutputStream(fname);
			bmp.compress(CompressFormat.PNG, 100, out);
			out.flush(); out.close();
			} catch(Exception e) {}
		}
	/** 画面サイズが変更された時 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w,h,oldw,oldh);
		bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bmpCanvas = new Canvas(bmp);
		bmpCanvas.drawColor(Color.WHITE);
		}
	/** 描画イベント */
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bmp, 0, 0, null);
		}
	/** タッチイベント */
	public boolean onTouchEvent(MotionEvent event) {
		// 描画位置の確認
		Point cur = new Point((int)event.getX(), (int)event.getY());
		if (oldpos.x < 0) { oldpos = cur; }
		// 描画属性を設定
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(4);
		// 線を描画
		bmpCanvas.drawLine(oldpos.x, oldpos.y, cur.x, cur.y, paint);
		oldpos = cur;
		// 指を持ち上げたら座標をリセット
		if (event.getAction() == MotionEvent.ACTION_UP) {
			oldpos = new Point(-1, -1);
			}
		invalidate();
		return true;
		}
	}
