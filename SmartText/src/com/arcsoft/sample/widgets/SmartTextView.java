package com.arcsoft.sample.widgets;

import com.arcsoft.sample.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SmartTextView extends LinearLayout {
	static final int DEFAULTVALUE_DEGREES = 0;
	static final int MAX_LENGHT_OF_TEXT = 200;
	static final int ID_COREEDIT = Integer.MAX_VALUE;

	static final int WORKMODE_MOVE = 1;
	static final int WORKMODE_INPUT = 2;
	static final int WORKMODE_ROTATESCALE = 3;
	
	static final int CORNERRADIUS = 10;
	static final int PAINT_STROKEWIDTH = 1;
	static final int EDGE_OFFSET  = 0;

	int mRotateDegress;

	String mText;
	int mTextColor;
	int mTextSize;
	Typeface mTextFont;

	CoreEdit mCoreEdit;

	SharedPreferences mPreferences;
	boolean mNeedCenterInView;
	public SmartTextView(Context context) {
		this(context, null);
	}

	public SmartTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SmartTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void onDrawRotateScaleIcon(Canvas canvas){
		BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_rotate_scale_control);
		Bitmap bitmap = drawable.getBitmap();
		
		int width  = bitmap.getWidth();
		int height = bitmap.getHeight();
		Rect rt = mCoreEdit.getFrameRect();
		
		int left = rt.right - width/2;
		int top  = rt.bottom - height /2;
		canvas.drawBitmap(bitmap, left, top, null);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		 final int action = ev.getAction();
         final int actionMasked = action & MotionEvent.ACTION_MASK;

         boolean handled = false;
         // Handle an initial down.
         if (actionMasked == MotionEvent.ACTION_DOWN) {
        	/**
        	 * if handled = true, CoreEdit is touched.
        	 */
        	handled = mCoreEdit.dispatchTouchEvent(ev);
			if(!handled){
				mCoreEdit.setWorkMode(WORKMODE_MOVE);
			}
         }
         
		return handled =  super.dispatchTouchEvent(ev);
	}

	/***************************************************************************************
	 * These are protected methods below
	 ***************************************************************************************/
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		if(mNeedCenterInView){	
			setGravity(Gravity.CENTER);
		}else{
			setGravity(Gravity.NO_GRAVITY);
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		onDrawRotateScaleIcon(canvas);
	}
	/***************************************************************************************
	 * These are private methods below
	 ***************************************************************************************/
	
	OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				// TODO something here...
			}
			return false;
		}
	};

	TextWatcher mTextWatcher = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		public void afterTextChanged(Editable s) {
			int nSelStart = 0;
			int nSelEnd = 0;
			boolean nOverMaxLength = false;

			nSelStart = mCoreEdit.getSelectionStart();
			nSelEnd = mCoreEdit.getSelectionEnd();

			nOverMaxLength = (s.length() > MAX_LENGHT_OF_TEXT) ? true : false;
			if (nOverMaxLength) {
				String msg = getContext()
						.getString(R.string.max_lenght_of_text);
				msg = String.format(msg, MAX_LENGHT_OF_TEXT);
				MyToast.show(getContext(), msg);
				s.delete(nSelStart - 1, nSelEnd);
				mCoreEdit.setTextKeepState(s);
			}
		}
	};

	OnDragListener mDragListener = new OnDragListener(){
		@Override
		public boolean onDrag(View v, DragEvent event) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	void getDefaultPreference() {
		mText = "";
		mTextColor = Color.WHITE;
		mTextFont = Typeface.DEFAULT;
	}

	void setPreferences(SharedPreferences pref) {
		mPreferences = pref;
	}

	SharedPreferences getPreferences() {
		return mPreferences;
	}

	void init() {
		mNeedCenterInView = true;
		setBackgroundResource(0);
		LayoutParams editLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mCoreEdit = new CoreEdit(getContext());
		mCoreEdit.setLayoutParams(editLayout);
		mCoreEdit.setId(ID_COREEDIT);

		mCoreEdit.setText("«Î ‰»ÎŒƒ◊÷");

		mCoreEdit.setBackgroundResource(0);
		mCoreEdit.setTextColor(Color.WHITE);
		
		
		mCoreEdit.setLongClickable(false);
		mCoreEdit.setVisibility(VISIBLE);
		mCoreEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
		mCoreEdit.setSingleLine(true);
		mCoreEdit.setOnEditorActionListener(mOnEditorActionListener);
		mCoreEdit.addTextChangedListener(mTextWatcher);
		mCoreEdit.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
		mCoreEdit.setWorkMode(WORKMODE_INPUT);
		mCoreEdit.setOnDragListener(mDragListener);
		
		addView(mCoreEdit);
		SharedPreferences preference = getPreferences();
		if (null == preference) {
			getDefaultPreference();
		} else {
			// TODO init some parameters
		}
	}

	static class MyToast {
		static String mMessage = null;
		static Toast mToast = null;

		public static void show(Context context, String msg) {
			boolean empty = TextUtils.isEmpty(msg);
			if (empty) {
				return;
			}

			if (TextUtils.equals(mMessage, msg)) {
				if (null != mToast) {
					mToast.show();
					return;
				}
			}

			mMessage = msg;
			mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
			mToast.show();
		}
	}
	
	class CoreEdit extends EditText{
		int mMode;
		Paint mFramePaint;
		RectF mRtfFrame;
		Rect  mFrameRect;
		public CoreEdit(Context context) {
			super(context,null);
			initMembers();
		}
		
		public void initMembers(){
			mMode = WORKMODE_INPUT;
			
			mFramePaint  = new Paint();  
			mRtfFrame    = new RectF();
			mFrameRect = new Rect();
			PathEffect effs = new DashPathEffect(new float[] {5, 5, 5, 5}, 5);
			mFramePaint.setAlpha(200);
			mFramePaint.setPathEffect(effs);
			mFramePaint.setStyle(Paint.Style.STROKE);  
			mFramePaint.setColor(Color.RED);  
			mFramePaint.setStrokeWidth(PAINT_STROKEWIDTH);  
		}

		@Override
		protected MovementMethod getDefaultMovementMethod() {
			
			return null;
		}

		public int getWorkMode(){
			return mMode;
		}
		
		public void setWorkMode(int mode){
			mMode = mode;
			switch(mMode){
			case WORKMODE_INPUT:
				setFocusable(true);
				setFocusableInTouchMode(true);
				requestFocusFromTouch();
				break;
			case WORKMODE_MOVE:
				setFocusable(false);
				setFocusableInTouchMode(false);
				requestFocusFromTouch();
				break;
			case WORKMODE_ROTATESCALE:
				break;
			}
		}
		
		public Rect getFrameRect(){
			return mFrameRect;
		}

		private void drawFrame(Canvas canvas){
			mFrameRect = canvas.getClipBounds();  
			mFrameRect.bottom--;  
			mFrameRect.right--;  

			mRtfFrame.left   = mFrameRect.left   + EDGE_OFFSET;
			mRtfFrame.top    = mFrameRect.top    + EDGE_OFFSET;
			mRtfFrame.right  = mFrameRect.right  - EDGE_OFFSET;
			mRtfFrame.bottom = mFrameRect.bottom - EDGE_OFFSET;

			canvas.drawRoundRect(mRtfFrame, CORNERRADIUS, CORNERRADIUS,
					mFramePaint);
		}

		@Override
		public void draw(Canvas canvas) {
			super.draw(canvas);
			drawFrame(canvas);
		}
	}
	
}
