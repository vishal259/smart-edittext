package com.arcsoft.sample.widgets;
import com.arcsoft.sample.R;
import com.arcsoft.sample.graphics.BaseDrawable;
import com.arcsoft.sample.graphics.TextDrawable;
import com.arcsoft.sample.widgets.DrawableHighlightView.Mode;
import com.arcsoft.sample.widgets.DrawableHighlightView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class EditTextScaleRotateView extends EditText {
	DrawableHighlightView mHighlightView;
	TextDrawable mTextDraw;
	GestureDetector mGestureDetector;
	ScaleRotateListener mScaleRotateListener;
	TextWatcher mTextWatcher;
	OnEditorActionListener mOnEditorActionListener;
	InputMethodManager mInputMethodManager;

	int mMotionEdge;
	
	/**
	 * 用于保存和恢复文字
	 */
	static public class TextState{
		public String mText;
		public String mHintText;
		public Typeface mTextFont;
		public float mTextSize;
		public int mTextColor;
		public int mPadding;
		
		public int mLeftTopX;//左上点X坐标
		public int mLeftTopY;//左上点Y坐标
		public int mDegree; //与水平方向夹角，逆时针

		public int mStrokeWidth;//字体描边
		public int mOutlineEllipse;//边框圆角
		public int mOutlineStrokeColor;//边框颜色
	};
	
	public EditTextScaleRotateView(Context context) {
		this(context, null);
	}
	
	public EditTextScaleRotateView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public EditTextScaleRotateView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}

	private void init(){
		mScaleRotateListener = new ScaleRotateListener();
		mGestureDetector     = new GestureDetector(getContext(),mScaleRotateListener);
		mMotionEdge  = DrawableHighlightView.GROW_NONE;
		mTextWatcher = new MyTextWatcher();
		mOnEditorActionListener = new MyOnEditorActionListener();
		mInputMethodManager = (InputMethodManager) getContext().getSystemService( Context.INPUT_METHOD_SERVICE );
		
		/**
		 * 不使用GPU硬件加速,如果使用的,文字无法无限放大
		 * android:hardwareAccelerated="false"
		 */
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		TextState state = new TextState();
		state.mText="";
		state.mHintText = TextUtils.isEmpty(getHint()) ?
				getContext().getString(R.string.edittext_defaut_hint) : (String)getHint();
		state.mTextColor = 0xffff0000;
		state.mTextSize  = getTextSize();
		state.mLeftTopX = 0;
		state.mLeftTopY = 0;
		
		state.mDegree = 0;
		state.mOutlineEllipse = 12;
		state.mOutlineStrokeColor = 0xff00FF00;
		state.mStrokeWidth = 5;
		state.mPadding = 15;
		initBy(state);
	}
	
	public int initBy(TextState state){
		if(null != mTextDraw){
			mTextDraw = null;
		}
		mTextDraw = new TextDrawable(state.mText, state.mTextSize);
		mTextDraw.setTextColor(state.mTextColor);
		mTextDraw.setTextHint(state.mHintText);

		if(null != mHighlightView){
			mHighlightView.dispose();
		}
		mHighlightView = null;
		DrawableHighlightView hv = new DrawableHighlightView(this, (BaseDrawable)mTextDraw);
		mHighlightView = hv;

		final Matrix localMatrix = new Matrix();

		final int width  = 800;
		final int height = 800;
		final int imageSize = Math.max( width, height );

		// width/height
		int cropWidth  = mTextDraw.getIntrinsicWidth();
		int cropHeight = mTextDraw.getIntrinsicHeight();
		
		final int cropSize = Math.max( cropWidth, cropHeight );
		
		if( cropSize > imageSize ){
			cropWidth = width/2;
			cropHeight = height/2;
		}

		final int x = ( width - cropWidth ) / 2;
		final int y = ( height - cropHeight ) / 2;

		final Matrix matrix = new Matrix( localMatrix );
		matrix.invert( matrix );

		final float[] pts = new float[] { x, y, x + cropWidth, y + cropHeight };
		matrix.mapPoints(pts);

		final RectF cropRect = new RectF( pts[0], pts[1], pts[2], pts[3] );
		final Rect imageRect = new Rect( 0, 0, width, height );

		hv.setSelected(true);
		hv.setRotateAndScale(true);
		hv.showDelete( false );
		hv.showAnchors(true);

		hv.setup(localMatrix, imageRect, cropRect, false);
		hv.drawOutlineFill( false );
		hv.drawOutlineStroke(true);
		hv.setPadding(state.mPadding);
		hv.setMinSize(12);
		hv.setOutlineStrokeColor(state.mOutlineStrokeColor);
		hv.setOutlineEllipse(state.mOutlineEllipse);

		Paint stroke = hv.getOutlineStrokePaint();
		stroke.setStrokeWidth(state.mStrokeWidth);
		
		return 0;
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		mGestureDetector.onTouchEvent( event );

		switch ( action ) {
			case MotionEvent.ACTION_UP:
				if (mHighlightView != null ){
					mHighlightView.setMode(DrawableHighlightView.Mode.None);
				}
				mMotionEdge = DrawableHighlightView.GROW_NONE;
		}

		return true;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		mHighlightView.draw(canvas);
	}

	private void beginEditText() {
		mTextDraw.beginEdit();
		removeTextChangedListener(mTextWatcher);
		setOnKeyListener( null );
		
		final String oldText = mTextDraw.isTextHint() ? "" : (String) mTextDraw.getText();
		setText( oldText );
		setSelection(length());
		setImeOptions( EditorInfo.IME_ACTION_DONE );
		requestFocusFromTouch();

		mInputMethodManager.toggleSoftInput( InputMethodManager.SHOW_FORCED, 0 );

		setOnEditorActionListener(mOnEditorActionListener);
		addTextChangedListener(mTextWatcher);
		setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey( View v, int keyCode, KeyEvent event ) {
				Log.i("XXXXXX", "onKey: " + event );
				if( keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_BACK ){
					if(mTextDraw.isTextHint() && mTextDraw.isEditing() ){
						mTextDraw.setText( "" );
						mHighlightView.forceUpdate();
					}
				}
				return false;
			}
		} );
	}
	
	private void endEditText() {
		mTextDraw.endEdit();
		removeTextChangedListener(mTextWatcher);
		setOnKeyListener( null );
		if ( mInputMethodManager.isActive(this) ) {
			mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0 );
		}
	}
	
	
	public class MyOnEditorActionListener implements OnEditorActionListener{
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {	
			if (!this.equals( v )) {
				return false;
			}
			
			if ( actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED ) {
				if ( mTextDraw.isEditing() ) {
					mTextDraw.endEdit();
					endEditText();
				}
			}
			return false;
		}
	}
	
	public class MyTextWatcher implements TextWatcher{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if ( !mTextDraw.isEditing() ) {
				return;
			}

			mTextDraw.setText(s.toString());
			mHighlightView.forceUpdate();
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
		
	}
	
	public class ScaleRotateListener extends GestureDetector.SimpleOnGestureListener{
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if ( mHighlightView != null ) {
				int edge = mHighlightView.getHit( e.getX(), e.getY() );
				if ( ( edge & DrawableHighlightView.MOVE ) == DrawableHighlightView.MOVE ) {
					if ( mHighlightView != null ){
						beginEditText();
					}
					return true;
				}
				
				if(edge == DrawableHighlightView.GROW_NONE){
					endEditText();
				}
				mHighlightView.setMode(Mode.None);
			}
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if ( e1 == null || e2 == null ) {
				return false;
			}
			
			if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 ) {
				return false;
			}

			if (mHighlightView != null && mMotionEdge != DrawableHighlightView.GROW_NONE ) {
				mHighlightView.onMouseMove( mMotionEdge, e2, -distanceX, -distanceY );
				endEditText();
				return true;
			} 
			
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			if (mHighlightView != null) {
				int edge = mHighlightView.getHit(e.getX(), e.getY());
				if (edge != DrawableHighlightView.GROW_NONE) {
					mMotionEdge = edge;
					mHighlightView.setMode((edge == DrawableHighlightView.MOVE) ? 
							DrawableHighlightView.Mode.Move : (edge == DrawableHighlightView.ROTATE ? 
							DrawableHighlightView.Mode.Rotate : DrawableHighlightView.Mode.Grow));
				}
			}
			return super.onDown(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			mHighlightView.onSingleTapConfirmed( e.getX(), e.getY() );
			return super.onSingleTapConfirmed(e);
		}
	}
}
