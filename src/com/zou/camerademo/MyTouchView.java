package com.zou.camerademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class MyTouchView extends ImageView {

	static final int NONE = 0;//表示当前没有状态
	static final int DRAG = 1;     //表示当前处于移动状态
	static final int ZOOM = 2;     //表示当前处于缩放状态
	static final int BIGGER = 3;   //表示放大图片
	static final int SMALLER = 4;  //表示缩小图片
	private int mode = NONE;      //mode用于标示当前处于什么状态

	private float beforeLenght;  //第一次触摸两点的距离
	private float afterLenght;    //移动后两点的距离
	private float scale = 0.04f;  //缩放因子

	private int screenW;//下面两句图片的移动范围，及ViewArea的范围，也就是linearLayout的范围，也就是屏幕方位（都是填满父控件属性）
	private int screenH;

	private int start_x;//开始触摸点
	private int start_y;
	private int stop_x;//结束触摸点
	private int stop_y;
	private TranslateAnimation trans; //回弹动画

	private Paint paint;
	private boolean mIsStartAnimation = false;

	public MyTouchView(Context context) {
		this(context, null);
	}
	public MyTouchView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public MyTouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (null != drawable) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			Bitmap b = getCircleBitmap(bitmap, 14);
			final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
			final Rect rectDest = new Rect(0, 0, getWidth(), getHeight());
			paint.reset();
			canvas.drawBitmap(b, rectSrc, rectDest, paint);
		} else {
			super.onDraw(canvas);
		}

	}
	
	public void startDownloadAnimation(boolean isStart) {
		mIsStartAnimation = true;
	}
	

	private Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap
				.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		int x = bitmap.getWidth();

		canvas.drawCircle(x / 2, x / 2, x / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	
	
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		
		super.dispatchDraw(canvas);
	}
	
	
	//用来计算2个触摸点的距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {     //MotionEvent.ACTION_MASK表示多点触控事件
			case MotionEvent.ACTION_DOWN :
				mode = DRAG;
				stop_x = (int) event.getRawX();//表示相对于屏幕左上角为原点的坐标
				stop_y = (int) event.getRawY();//同上
				start_x = stop_x - this.getLeft();//用(int) event.getX();一样,表示相对于当前点击Widget（控件）左上角的坐标，这里就是相对于自定义imageView左上角的坐标.建议用前者，如果不是全屏拖动，而是指定范围内，一样适用！
				start_y = stop_y - this.getTop();////用(int) event.getY();一样,this.getTop()表示其顶部相对于父控件的距离

				if (event.getPointerCount() == 2)
					beforeLenght = spacing(event);
				break;
			case MotionEvent.ACTION_POINTER_DOWN :
				if (spacing(event) > 10f) {
					mode = ZOOM;
					beforeLenght = spacing(event);
				}
				break;
			case MotionEvent.ACTION_UP :

				int disX = 0;
				int disY = 0;
				if (getHeight() <= screenH)//
				{
					if (this.getTop() < 0) {
						disY = getTop();
						//layout(left , top, right,bottom)函数表示设置view的位置。
						this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());

					} else if (this.getBottom() >= screenH) {
						disY = getHeight() - screenH + getTop();
						this.layout(this.getLeft(), screenH - getHeight(), this.getRight(), screenH);
					}
				} else {
					int Y1 = getTop();
					int Y2 = getHeight() - screenH + getTop();
					if (Y1 > 0) {
						disY = Y1;
						this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
					} else if (Y2 < 0) {
						disY = Y2;
						this.layout(this.getLeft(), screenH - getHeight(), this.getRight(), screenH);
					}
				}
				if (getWidth() <= screenW) {
					if (this.getLeft() < 0) {
						disX = getLeft();
						this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
					} else if (this.getRight() > screenW) {
						disX = getWidth() - screenW + getLeft();
						this.layout(screenW - getWidth(), this.getTop(), screenW, this.getBottom());
					}
				} else {
					int X1 = getLeft();
					int X2 = getWidth() - screenW + getLeft();
					if (X1 > 0) {
						disX = X1;
						this.layout(0, this.getTop(), 0 + getWidth(), this.getBottom());
					} else if (X2 < 0) {
						disX = X2;
						this.layout(screenW - getWidth(), this.getTop(), screenW, this.getBottom());
					}

				}
				//如果图片缩放到宽高任意一个小于100，那么自动放大，直到大于100.
				while (getHeight() < 100 || getWidth() < 100) {

					setScale(scale, BIGGER);
				}
				//根据disX和disY的偏移量采用移动动画回弹归位，动画时间为500毫秒。
				if (disX != 0 || disY != 0) {
					trans = new TranslateAnimation(disX, 0, disY, 0);
					trans.setDuration(500);
					this.startAnimation(trans);
				}
				mode = NONE;
				break;
			case MotionEvent.ACTION_POINTER_UP :
				mode = NONE;
				break;
			case MotionEvent.ACTION_MOVE :

				if (mode == DRAG) {
					//执行拖动事件的时，不断变换自定义imageView的位置从而达到拖动效果
					this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth()
							- start_x, stop_y - start_y + this.getHeight());
					stop_x = (int) event.getRawX();
					stop_y = (int) event.getRawY();

				} else if (mode == ZOOM) {
					if (spacing(event) > 10f) {
						afterLenght = spacing(event);
						float gapLenght = afterLenght - beforeLenght;
						if (gapLenght == 0) {
							break;
						}
						//图片宽度（也就是自定义imageView）必须大于70才可以缩放
						else if (Math.abs(gapLenght) > 5f && getWidth() > 70) {
							if (gapLenght > 0) {
								this.setScale(scale, BIGGER);
							} else {
								this.setScale(scale, SMALLER);
							}
							beforeLenght = afterLenght; //这句不能少。
						}
					}
				}
				break;
		}
		return true;
	}

	private void setScale(float temp, int flag) {
		if (flag == BIGGER) {
			//setFrame(left , top, right,bottom)函数表示改变当前view的框架，也就是大小。
			this.setFrame(this.getLeft() - (int) (temp * this.getWidth()), this.getTop()
					- (int) (temp * this.getHeight()),
					this.getRight() + (int) (temp * this.getWidth()), this.getBottom()
							+ (int) (temp * this.getHeight()));
		} else if (flag == SMALLER) {
			this.setFrame(this.getLeft() + (int) (temp * this.getWidth()), this.getTop()
					+ (int) (temp * this.getHeight()),
					this.getRight() - (int) (temp * this.getWidth()), this.getBottom()
							- (int) (temp * this.getHeight()));
		}
	}

	private void setPosition(int left, int top, int right, int bottom) {
		this.layout(left, top, right, bottom);
	}

}
