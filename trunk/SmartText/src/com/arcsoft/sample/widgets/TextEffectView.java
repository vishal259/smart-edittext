package com.arcsoft.sample.widgets;

import com.arcsoft.sample.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 使用矩阵(Matrix)控制图片(文字)移动、缩放、旋转
 * TextEffectView 将伪装成为EditText控件，但是多出缩放、旋转、平移、等特性
 */
public class TextEffectView extends View {
	Context mContext;
	Bitmap mTextBmp;
	Bitmap mControlBmp;
	int mTextBmpWidth;
	int mTextBmpHeight;
	int mControlBmpWidth;
	int mControlBmpHeight;
	Matrix mMatrix;
	
	
	float[] mSrcPoints;
	float[] mDstPoints;
	RectF mSrcRect;    
	RectF mDstRect;

	
	Paint mTextBmpPaint;
	Paint mPaintRect;
	Paint mPaintFrame;
	
	float mDeltaX = 0, mDeltaY = 0; // 位移值
	float mScaleValue = 1; 		    // 缩放值
	
	Point mLastPoint;
	Point mCurrPivot, mLastPivot;
	
	float mCurrDegree, mLastDegree;
	Point mCenterPoint = new Point(); // 旋转缩放的中心点,图片控制点'8'所在的点坐标
	/*
	 * 图片的四个顶点坐标
	 */
	Point mPointLeftTop;
	Point mPointRightTop;
	Point mPointRightBottom;
	Point mPointLeftBottom;
	/**
	 * 图片控制点 
	 * 0---1---2 
	 * |       | 
	 * 7   8   3 
	 * |       | 
	 * 6---5---4
	 * TextEffectView作为EditText控件，暂时只使用到中心点'8'和控制点'4'
	 */
	public static final int CTR_NONE = -1;
	public static final int CTR_LEFT_TOP = 0;
	public static final int CTR_MID_TOP = 1;
	public static final int CTR_RIGHT_TOP = 2;
	public static final int CTR_RIGHT_MID = 3;
	public static final int CTR_RIGHT_BOTTOM = 4;
	public static final int CTR_MID_BOTTOM = 5;
	public static final int CTR_LEFT_BOTTOM = 6;
	public static final int CTR_LEFT_MID = 7;
	public static final int CTR_MID_MID = 8;
	public int mCurrCtrl = CTR_NONE;
	
	/**
	 * 操作类型
	 */
	public static final int OPER_DEFAULT          = -1;  // 默认
	public static final int OPER_TRANSLATE        = 0; // 移动
	public static final int OPER_ROTATE_AND_SCALE = 1; //旋转同时缩放
	public static final int OPER_ENTERTEXT        = 2; // 光标闪动输入文字
	public int mLastOper = OPER_DEFAULT;
    boolean mTouchDownOnText = false;
	

	public TextEffectView(Context context) {
		super(context);
		mContext = context;
		initData();
	}

	public TextEffectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initData();
	}

	/**
	 * 初始化数据
	 */
	void initData() {
		mTextBmp = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.flower);
		mControlBmp = BitmapFactory.decodeResource(
				mContext.getResources(),
				R.drawable.ic_rotate_scale_control);
		mTextBmpWidth = mTextBmp.getWidth();
		mTextBmpHeight = mTextBmp.getHeight();
		mControlBmpWidth = mControlBmp.getWidth();
		mControlBmpHeight = mControlBmp.getHeight();

		/**
		 * mSrcPoints 偶数位下标位置存放的是X坐标,奇数位置存放的是Y坐标
		 */
		mSrcPoints = new float[] { 0, 0, mTextBmpWidth / 2, 0, mTextBmpWidth, 0,
				mTextBmpWidth, mTextBmpHeight / 2, mTextBmpWidth, mTextBmpHeight,
				mTextBmpWidth / 2, mTextBmpHeight, 0, mTextBmpHeight, 0,
				mTextBmpHeight / 2, mTextBmpWidth / 2, mTextBmpHeight / 2 };
		mDstPoints = mSrcPoints.clone();
		mSrcRect = new RectF(0, 0, mTextBmpWidth, mTextBmpHeight);
		mDstRect = new RectF();

		mMatrix = new Matrix();

		mCurrPivot = new Point(mTextBmpWidth / 2, mTextBmpHeight / 2);
		mLastPivot = new Point(mTextBmpWidth / 2, mTextBmpHeight / 2);

		mLastPoint = new Point(0, 0);

		mPointLeftTop     = new Point((int)mDstPoints[CTR_LEFT_TOP*2], (int)mDstPoints[CTR_LEFT_TOP*2 + 1]);
		mPointRightTop    = new Point((int)mDstPoints[CTR_RIGHT_TOP*2], (int)mDstPoints[CTR_RIGHT_TOP*2 + 1]);
		mPointRightBottom = new Point((int)mDstPoints[CTR_RIGHT_BOTTOM*2], (int)mDstPoints[CTR_RIGHT_BOTTOM*2 + 1]);
		mPointLeftBottom  = new Point((int)mDstPoints[CTR_LEFT_BOTTOM*2], (int)mDstPoints[CTR_LEFT_BOTTOM*2 + 1]);
		
		mTextBmpPaint = new Paint();

		mPaintRect = new Paint();
		mPaintRect.setColor(Color.RED);
		mPaintRect.setAlpha(100);
		mPaintRect.setAntiAlias(true);

		mPaintFrame = new Paint();
		mPaintFrame.setColor(Color.GREEN);
		mPaintFrame.setAntiAlias(true);

		mLastDegree = computeDegree(
				new Point((int) mDstPoints[CTR_RIGHT_BOTTOM*2], (int) mDstPoints[CTR_RIGHT_BOTTOM*2 + 1]),
				new Point((int) mDstPoints[CTR_MID_MID*2], (int) mDstPoints[CTR_MID_MID*2 + 1]));
		mCurrDegree = mLastDegree;
		setMatrix(OPER_DEFAULT);
	}

	/**
	 * 矩阵变换，达到图形平移旋转缩放等目的
	 */
	void setMatrix(int operationType) {
		switch (operationType) {
		case OPER_TRANSLATE:
			mMatrix.postTranslate(mDeltaX, mDeltaY);
			break;
		case OPER_ROTATE_AND_SCALE:
			mMatrix.postRotate(mCurrDegree - mLastDegree, mCenterPoint.x,
					mCenterPoint.y);
			mMatrix.postScale(mScaleValue, mScaleValue, mCenterPoint.x,
					mCenterPoint.y);
			break;
		}

		mMatrix.mapPoints(mDstPoints, mSrcPoints);
		mMatrix.mapRect(mDstRect, mSrcRect);
	}

	void getVertexPoints(){
		mPointLeftTop.set((int)mDstPoints[CTR_LEFT_TOP*2], (int)mDstPoints[CTR_LEFT_TOP*2 + 1]);
		mPointRightTop.set((int)mDstPoints[CTR_RIGHT_TOP*2], (int)mDstPoints[CTR_RIGHT_TOP*2 + 1]);
		mPointRightBottom.set((int)mDstPoints[CTR_RIGHT_BOTTOM*2], (int)mDstPoints[CTR_RIGHT_BOTTOM*2 + 1]);
		mPointLeftBottom.set((int)mDstPoints[CTR_LEFT_BOTTOM*2], (int)mDstPoints[CTR_LEFT_BOTTOM*2 + 1]);
	}
	
	boolean isTouchOnTextPic(int x, int y) {
		Point point = new Point(x,y);
		getVertexPoints();
		boolean isInQuadrangle = Quadrangle.pInQuadrangle(
				mPointLeftTop, mPointRightTop, 
				mPointRightBottom, mPointLeftBottom, 
				point);
		
		Log.i("XXXXXXXXX", "isInQuadrangle " + isInQuadrangle);
		if (isInQuadrangle) {
			return true;
		} else{
			return false;
		}
	}

	int getOperationType(MotionEvent event) {
		int evX = (int) event.getX();
		int evY = (int) event.getY();
		int curOper = mLastOper;
		
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mCurrCtrl = getCurrCtrl(evX, evY);
			Log.i("XXXXXXXXXXXX", "getOperationType MotionEvent.ACTION_DOWN");
			Log.i("XXXXXXXXXXXX", "mCurrCtrl is " + mCurrCtrl);
			if (mCurrCtrl == CTR_RIGHT_BOTTOM) {
				curOper = OPER_ROTATE_AND_SCALE;
			}else{
				curOper = OPER_DEFAULT;
				mTouchDownOnText = isTouchOnTextPic(evX, evY);
			}
			Log.i("XXXXXXXXXXXX", "mTouchDownOnText is " + mTouchDownOnText);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mCurrCtrl != CTR_RIGHT_BOTTOM && mTouchDownOnText) {
				curOper = OPER_TRANSLATE;
			}
			break;
		case MotionEvent.ACTION_UP:
			boolean isTouchUpOnText = isTouchOnTextPic(evX, evY);
			if(curOper != OPER_TRANSLATE && isTouchUpOnText){
				curOper = OPER_ENTERTEXT;
			}
			Log.i("XXXXXXXXXXXX", "isTouchUpOnText  " + isTouchUpOnText);
			break;
		default:
			break;
		}
		Log.d("XXXXXXXXXXXX", "curOper is " + curOper);
		return curOper;

	}

	/**
	 * 判断点所在的控制点
	 * 
	 * @param evX
	 * @param evY
	 * @return
	 */
	int getCurrCtrl(int evx, int evy) {
		Rect rect = new Rect(evx - mControlBmpWidth / 2, evy - mControlBmpHeight
				/ 2, evx + mControlBmpWidth / 2, evy + mControlBmpHeight / 2);
		int res = 0;
		for (int i = 0; i < mDstPoints.length; i += 2) {
			if (rect.contains((int) mDstPoints[i], (int) mDstPoints[i + 1])) {
				return res;
			}
			++res;
		}
		return CTR_NONE;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int evX = (int) event.getX();
		int evY = (int) event.getY();
		int operType = OPER_DEFAULT;
		operType = getOperationType(event);

		switch (operType) {
		case OPER_TRANSLATE:
			translate(event);
			break;
		case OPER_ROTATE_AND_SCALE:
			rotate_and_scale(event);
			break;
		case OPER_ENTERTEXT:
			
			break;
		}

		mLastPoint.x = evX;
		mLastPoint.y = evY;

		mLastOper = operType;
		invalidate();// 重绘
		return true;
	}

	/**
	 * 移动
	 * 
	 * @param evx
	 * @param evy
	 */
	void translate(MotionEvent event) {
		int evX = (int) event.getX();
		int evY = (int) event.getY();
		
		mCurrPivot.x += evX - mLastPoint.x;
		mCurrPivot.y += evY - mLastPoint.y;

		mDeltaX = mCurrPivot.x - mLastPivot.x;
		mDeltaY = mCurrPivot.y - mLastPivot.y;

		mLastPivot.x = mCurrPivot.x;
		mLastPivot.y = mCurrPivot.y;

		setMatrix(OPER_TRANSLATE); // 设置矩阵

	}

	/**
	 * 缩放
	 * @param evX
	 * @param evY
	 */
	void scale(MotionEvent event) {

		int pointIndex = mCurrCtrl * 2;

		float px = mDstPoints[pointIndex];
		float py = mDstPoints[pointIndex + 1];

		float evx = event.getX();
		float evy = event.getY();

		/**
		 * 以图片控制的中心点 CTR_MID_MID = '8'的坐标作为缩放的轴心
		 */
		float oppositeX = mDstPoints[CTR_MID_MID* 2];
		float oppositeY = mDstPoints[CTR_MID_MID* 2 + 1];
		
		float temp1 = getDistanceOfTwoPoints(px, py, oppositeX, oppositeY);
		float temp2 = getDistanceOfTwoPoints(evx, evy, oppositeX, oppositeY);

		mScaleValue = temp2 / temp1;
		mCenterPoint.x = (int) oppositeX;
		mCenterPoint.y = (int) oppositeY;

		Log.i("img", "mScaleValue is " + mScaleValue);
	}

	/**
	 * 旋转图片 
	 * 0---1---2 
	 * |       | 
	 * 7   8   3 
	 * |       | 
	 * 6---5---4
	 * 
	 * @param evX
	 * @param evY
	 */
	void rotate(MotionEvent event) {
		if (event.getPointerCount() == 2) {
			mCurrDegree = computeDegree(new Point((int) event.getX(0),
					(int) event.getY(0)), new Point((int) event.getX(1),
					(int) event.getY(1)));
		} else {
			mCurrDegree = computeDegree(
					new Point((int) event.getX(), (int) event.getY()),
					new Point((int) mDstPoints[16], (int) mDstPoints[17]));
		}
	}
	
	void rotate_and_scale(MotionEvent event){
		rotate(event);
		scale(event);
		setMatrix(OPER_ROTATE_AND_SCALE);
		
		mLastDegree = mCurrDegree;
	}

	/**
	 * 计算两点与垂直方向夹角
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public float computeDegree(Point p1, Point p2) {
		float tran_x = p1.x - p2.x;
		float tran_y = p1.y - p2.y;
		float degree = 0.0f;
		float angle = (float) (Math.asin(tran_x
				/ Math.sqrt(tran_x * tran_x + tran_y * tran_y)) * 180 / Math.PI);
		if (!Float.isNaN(angle)) {
			if (tran_x >= 0 && tran_y <= 0) {// 第一象限
				degree = angle;
			} else if (tran_x <= 0 && tran_y <= 0) {// 第二象限
				degree = angle;
			} else if (tran_x <= 0 && tran_y >= 0) {// 第三象限
				degree = -180 - angle;
			} else if (tran_x >= 0 && tran_y >= 0) {// 第四象限
				degree = 180 - angle;
			}
		}
		return degree;
	}

	/**
	 * 计算两个点之间的距离
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	float getDistanceOfTwoPoints(Point p1, Point p2) {
		return (float) (Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
				* (p1.y - p2.y)));
	}

	float getDistanceOfTwoPoints(float x1, float y1, float x2, float y2) {
		return (float) (Math
				.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
	}

	@Override
	public void onDraw(Canvas canvas) {
		drawBackground(canvas);// 绘制背景,以便测试矩形的映射
		canvas.drawBitmap(mTextBmp, mMatrix, mTextBmpPaint);// 绘制主图片
		drawFrame(canvas);// 绘制边框,以便测试点的映射
		drawControlPoints(canvas);// 绘制控制点图片
	}

	void drawBackground(Canvas canvas) {
		canvas.drawRect(mDstRect, mPaintRect);
	}

	void drawFrame(Canvas canvas) {
		canvas.drawLine(mDstPoints[0], mDstPoints[1], mDstPoints[4], mDstPoints[5], mPaintFrame);
		canvas.drawLine(mDstPoints[4], mDstPoints[5], mDstPoints[8], mDstPoints[9], mPaintFrame);
		canvas.drawLine(mDstPoints[8], mDstPoints[9], mDstPoints[12], mDstPoints[13], mPaintFrame);
		canvas.drawLine(mDstPoints[0], mDstPoints[1], mDstPoints[12], mDstPoints[13], mPaintFrame);
		//canvas.drawPoint(mDstPoints[16], mDstPoints[17], mPaintFrame);//中心有个小点,不注意看不出。
	}

	void drawControlPoints(Canvas canvas) {
		/*for (int i = 0; i < mDstPoints.length; i += 2) {
			canvas.drawBitmap(mControlBmp, mDstPoints[i] - mControlBmpWidth / 2,
				mDstPoints[i + 1] - mControlBmpHeight / 2, mTextBmpPaint);
		}*/
		int i = CTR_RIGHT_BOTTOM * 2;
		canvas.drawBitmap(mControlBmp, mDstPoints[i] - mControlBmpWidth / 2,
				mDstPoints[i + 1] - mControlBmpHeight / 2, mTextBmpPaint);
	}
	
	static public class Quadrangle 
	{
		/**
		 * 点P是否在(a,b,c,d)所组成的四边形内
		 */
		/*
  			通过面积法，判断点P是否在四边形(A,B,C,D)内。
  			如果在四边形内则，  四边形的面积=面积(P,A,B)+面积(P,B,C)+面积(P,C,D)+面积(P,D,A)
   			反之不在四边形内。
		 */
		public static boolean pInQuadrangle(Point a, Point b, Point c,Point d,Point p)
		{
			Log.d("Walk Game","Quadrangle:"+
					 "new Point("+a.x+","+a.y+"),"+
					 "new Point("+b.x+","+b.y+"),"+
					 "new Point("+c.x+","+c.y+"),"+
					 "new Point("+d.x+","+d.y+"),"+
					 "new Point("+p.x+","+p.y+"),"
					);
			 
			 
			double dTriangle = triangleArea(a,b,p)+triangleArea(b,c,p)
						+triangleArea(c,d,p)+triangleArea(d,a,p);
			double dQuadrangle = triangleArea(a,b,c)+triangleArea(c,d,a);		
			return dTriangle==dQuadrangle;	
		}
		
		// 返回三个点组成三角形的面积
		private static double triangleArea(Point a, Point b, Point c) 
		{
	        double result = Math.abs((a.x * b.y + b.x * c.y + c.x * a.y - b.x * a.y
	                - c.x * b.y - a.x * c.y) / 2.0D);
	        return result;
	    }
		
	}

}
