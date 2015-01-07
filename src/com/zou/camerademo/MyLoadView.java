package com.zou.camerademo;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class MyLoadView extends ImageView {

	private Bitmap mDESTR; //锁屏描述文字
	private int mColors[] = { 0xFF0dd2b4, 0xFF138fb4 }; // 背景渐变色
	private float mPositions[] = { 0, 1 }; // new float[2];
	private float mDistance = 10000;
	private Bitmap mFirstIcon;
	Paint mPaint;
	private Rect mClientRect = new Rect();
	private RectF mOval3 = new RectF(); // 设置个新的长方形
	private long mRuntime = 0; // 动画执行时间
	private long mStartime = 0; // 动画开始时间
	private final static long sANITIME = 1000; // 文字遮罩动画执行周期
	private long mDelay = 0; // 动画延迟时间
	public static float sDensity = 1.0f;
	private Context mContext = null;
	private boolean mIsFirst = false; //是否首发

	public MyLoadView(Context context) {
		super(context);
		mContext = context;
		mDESTR = ((BitmapDrawable) getResources()
				.getDrawable(
						R.drawable.gomarket_gostore_theme_detail_bottom_button_layout_background_download_finished_long))
				.getBitmap();
		mFirstIcon = ((BitmapDrawable) getResources()
				.getDrawable(
						R.drawable.gomarket_gostore_theme_detail_bottom_button_layout_background_downloading_long))
				.getBitmap();
	}

	/** 
	 * <默认构造函数>  为何不能少呢？
	 */
	public MyLoadView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public MyLoadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPaint = new Paint();
	}

	int mX = 0;
	boolean mIsMove = true;
	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.reset();
		if (mDESTR == null) {
			mDESTR = ((BitmapDrawable) getResources()
					.getDrawable(
							R.drawable.gomarket_gostore_theme_detail_bottom_button_layout_background_download_finished_long))
					.getBitmap();
		}

		int w = getWidth();
		int h = getHeight();

		mClientRect.set(0, 0, w, h);

		LinearGradient shader = new LinearGradient(0, 0, 0, h, mColors, mPositions, TileMode.MIRROR);
		mPaint.setShader(shader);
		mPaint.setAntiAlias(true); // 设置画笔的锯齿效果
		canvas.drawRect(mClientRect, mPaint); // 画背景

		if (mFirstIcon == null) {
			mFirstIcon = ((BitmapDrawable) getResources()
					.getDrawable(
							R.drawable.gomarket_gostore_theme_detail_bottom_button_layout_background_downloading_long))
					.getBitmap();
		}

		if (mIsFirst) {
			if (mFirstIcon != null && !mFirstIcon.isRecycled()) {
				canvas.drawBitmap(mFirstIcon, w / 2 - mFirstIcon.getWidth() / 2, h - h * 96 / 1280
						- mFirstIcon.getHeight(), null); // 画首发图标
			}
		}
		canvas.save();
		canvas.translate(mX, 0);
		
		canvas.drawBitmap(mDESTR, 0 , h - h * 96 / 1280
				- mFirstIcon.getHeight(), null); // 画首发图标
		canvas.restore();
		
		canvas.save();
		canvas.translate(mX - mDESTR.getWidth(), 0);
		canvas.drawBitmap(mDESTR, 0 , h - h * 96 / 1280
				- mFirstIcon.getHeight(), null); // 画首发图标
		canvas.restore();
		
		canvas.save();
		canvas.translate(mX, 0);
		canvas.drawBitmap(mFirstIcon, 0 , h - h * 96 / 1280
				- mFirstIcon.getHeight(), null); // 画首发图标
		canvas.restore();
		
		if (mIsMove) {
			mX += 5;
			if (mX > getWidth()) {
				mX %= getWidth();
			}
			invalidate();
		}
		
//		mPaint.setColor(Color.WHITE);
//		float left = (float) (w / 2.0f - mDESTR.getWidth() / 2.0f - mDESTR.getHeight() / 2.0f);
//		float right = (float) (w / 2.0f + mDESTR.getWidth() / 2.0f + mDESTR.getHeight() / 2.0f);
//		float top = (float) (h * 226.0f / 1280 + dip2px(42.0f));
//		float bottom = (float) (h * 226.0f / 1280 + +dip2px(42.0f) + mDESTR.getHeight() * 1.0f);
//		long mNowtime = System.currentTimeMillis();
//		// paint.reset();
//		if (mDistance == 10000) { // 第一次
//			mDistance = mDESTR.getWidth();
//		}
//		if (mStartime == 0) { // 动画开始时间点
//			mStartime = mNowtime;
//			mDelay = mNowtime;
//		}
//
//		if (mDelay != 0) {
//			if (mNowtime - mDelay <= 400) { // 延迟时间段，不执行动画
//				return;
//			} else if (mStartime == mDelay) { // 开始执行动画
//				mStartime = mNowtime;
//			}
//		}
//		mOval3.set(left, top, right - mDistance, bottom); // 设置个新的长方形
//
//		int sc = canvas.saveLayer(left, top, right, bottom, mPaint, Canvas.MATRIX_SAVE_FLAG
//				| Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
//				| Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
//		// canvas.drawRect(left, top, right, bottom, paint);
//
//		mPaint.setStyle(Paint.Style.FILL); // 充满
//		mPaint.setColor(Color.BLACK);
//		mPaint.setAntiAlias(true); // 设置画笔的锯齿效果
//		canvas.drawRoundRect(mOval3, mDESTR.getHeight() / 2.0f, mDESTR.getHeight() / 2.0f, mPaint); // 第二个参数是x半径，第三个参数是y半径
//		mPaint.reset();
//		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//		mPaint.setAntiAlias(true); // 设置画笔的锯齿效果
//		if (mDESTR != null && !mDESTR.isRecycled()) {
//			canvas.drawBitmap(mDESTR, (float) (w / 2.0f - mDESTR.getWidth() / 2.0f),
//					(float) (h * 226.0f / 1280 + dip2px(42.0f)), mPaint); // 写文字
//		}
//		mPaint.setXfermode(null);
//		canvas.restoreToCount(sc);
//		mRuntime = mNowtime - mStartime;
//		mDistance = mDESTR.getWidth() * (sANITIME - mRuntime) / sANITIME * 1.0f;
//
//		super.onDraw(canvas);
	}

	public int dip2px(float dipVlue) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		sDensity = metrics.density;

		return (int) (dipVlue * sDensity + 0.5f);
	}

}
