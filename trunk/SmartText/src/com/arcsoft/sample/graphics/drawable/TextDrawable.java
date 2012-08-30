package com.arcsoft.sample.graphics.drawable;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;

public class TextDrawable extends Drawable implements ITextEditable {
    protected final List<Integer> linesBreak = new ArrayList<Integer>();
    protected final RectF mBoundsF = new RectF(0.0F, 0.0F, 0.0F, 0.0F);
    protected final Paint mPaint = new Paint(451);//256,128,56,8,2,1
    protected final long  mCursorBlinkTime = 300L;
  

    protected int	mWidth;
    protected int 	mHeight;
    protected int   mMinTextWidth;
    
    protected float minTextSize = 16.0F;
    protected float mMinHeight;
    protected float mMinWidth;
    
    protected long    mNow = 0L;
    protected boolean mShowCursor = false;
    protected boolean mTextHint = false;
    protected boolean mEditing = false;
    
    protected Paint   mStrokePaint;
    protected String  mText = "";


    Paint.FontMetrics metrics = new Paint.FontMetrics();

	public TextDrawable(String text, float textSize) {
		mPaint.setDither(true);
		mPaint.setColor(-1);
		mPaint.setStyle(Paint.Style.FILL);
		if (textSize < minTextSize){
			textSize = minTextSize;
		}
			
		mPaint.setTextSize(textSize);
		mStrokePaint = new Paint(mPaint);
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mStrokePaint.setStrokeWidth(textSize / 10.0F);
		mWidth = 0;
		mHeight = 0;
		setText(text);
		computeMinSize();
	}
	
	void computeMinSize() {
		mMinWidth = getMinWidth();
		mMinHeight = minTextSize;
	}
	
	protected float getMinWidth(){
		float[] arrayOfFloat = new float[1];
		mPaint.getTextWidths(" ", arrayOfFloat);
		return arrayOfFloat[0] / 2.0F;
	}
	
	protected void computeMinWidth() {
		mMinTextWidth = (int) getMinWidth();
	}

	protected void computeSize() {
		computeMinWidth();
		computeTextWidth();
		computeTextHeight();
	}

	protected void computeTextWidth() {
		if (mText.length() > 0) {
			int lineNum = getNumLines();
			int lineWidth = 0;
			if (lineNum == 1) {
				lineWidth = (int) getTextWidth(0, mText.length());
			} else {
				/**
				 * 循环找最宽的行，它的宽度就是文字的宽度。
				 */
				int k = 0;
				for (int m = 0; m < linesBreak.size(); m++) {
					int n = linesBreak.get(m).intValue();
					lineWidth = (int) Math.max(lineWidth, getTextWidth(k, n));
					k = n + 1;
				}
			}
			mWidth = lineWidth + mMinTextWidth;
		}
	}
	
	
	protected void computeTextHeight() {
		mHeight = (int) Math.max(getTextSize(), 
				getNumLines()* getTextSize());
	}

	protected int getNumLines() {
		return Math.max(linesBreak.size(), 1);
	}
	
	protected void copyBounds(RectF outRectF) {
		outRectF.set(mBoundsF);
	}

	protected float getTextWidth(int startIndex, int endIndex) {
		float[] arrayOfFloat = new float[endIndex - startIndex];
		mPaint.getTextWidths(mText, startIndex, endIndex,
				arrayOfFloat);
		return getTotal(arrayOfFloat);
	}
	
	protected float getTotal(float[] arrayOfTextWidths) {
		float f = 0.0F;
		int i = arrayOfTextWidths.length;
		for (int j = 0; j < i; j++) {
			f += arrayOfTextWidths[j];
		}
		return f;
	}
	
	@Override
	public void draw(Canvas canvas) {
		RectF localRectF = new RectF();
		copyBounds(localRectF);
		int numLines = getNumLines();
		float txtSize = getTextSize();
		getFontMetrics(metrics);
		long currTime;
		if (numLines == 1) {
			/**
			 * 单行文字的绘制
			 */
			if (!mTextHint) {
				canvas.drawText(mText, 
						localRectF.left, 
						localRectF.top - metrics.top - metrics.bottom, 
						mStrokePaint);
			}

			canvas.drawText(mText, 
					localRectF.left, 
					localRectF.top - metrics.top - metrics.bottom,
					mPaint);
		}else{
			/**
			 * 绘制多行文字
			 */
			int j = 0;
			float x = localRectF.left;
			float y = localRectF.top;
			for (int k = 0; k < linesBreak.size(); k++) {
				int m = linesBreak.get(k).intValue();
				String str = mText.substring(j, m);
				if (!mTextHint){
					canvas.drawText(str, x, y, mStrokePaint);
				}
				canvas.drawText(str, x, y, mPaint);
				j = m + 1;
				y += txtSize;
			}
		}
		
		/**
		 * 处于编辑状态要绘制光标
		 */
		if (mEditing) {
			currTime = System.currentTimeMillis();
			if (currTime - mNow > mCursorBlinkTime){
				mShowCursor = !mShowCursor;
			}
			mNow = currTime;	
			
			if (mShowCursor) {
				Rect localRect = new Rect();
				getLineBounds(-1 + getNumLines(), localRect);
				float left = 2.0F + (localRectF.left + localRect.width());
				float top = localRectF.top;
				float right = 4.0F + (localRectF.left + localRect.width());
				float bottom = localRectF.top - metrics.top * (numLines - 1)
						- metrics.top - metrics.bottom;
				canvas.drawRect(left, top, right, bottom, mStrokePaint);
				canvas.drawRect(left, top, right, bottom, mPaint);
			}
		}
	}
	
	public void getLineBounds(int lineIndex, Rect bounds) {
		if (mText.length() <= 0) {
			mPaint.getTextBounds(mText, 0, mText.length(), bounds);
			bounds.left = 0;
			bounds.right = 0;
			return;
		}

		if (getNumLines() == 1) {
			mPaint.getTextBounds(mText, 0, mText.length(), bounds);

			bounds.left = 0;
			bounds.right = (int) getTextWidth(0, mText.length());
		} else {
			if (bounds.width() < mMinTextWidth) {
				bounds.right = mMinTextWidth;
			}
			// 获取多行文字top,bottom
			bounds.offset(0, (int) (getTextSize() * getNumLines()));

			// 获取多行文字left,right
			int start = 1 + linesBreak.get(lineIndex - 1).intValue();
			int end = linesBreak.get(lineIndex).intValue();
			mPaint.getTextBounds(mText, start, end, bounds);
			bounds.left = 0;
			bounds.right = (int) getTextWidth(start, end);
		}
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf){
		mPaint.setColorFilter(cf);
		mStrokePaint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		int alpha = mPaint.getAlpha();
		return alpha;
	}

	@Override
	public void beginEdit() {
		mEditing = true;
	}

	@Override
	public void endEdit() {
		mEditing = false;
	}

	public int getIntrinsicHeight() {
		return mHeight;
	}

	public int getIntrinsicWidth() {
		return mWidth;
	}
	
	@Override
	public float getFontMetrics(FontMetrics fontMetrics) {
		return mPaint.getFontMetrics(fontMetrics);
	}

	@Override
	public CharSequence getText() {
		return mText;
	}

	@Override
	public int getTextColor() {
		return mPaint.getColor();
	}

	@Override
	public float getTextSize() {
		return mPaint.getTextSize();
	}

	@Override
	public int getTextStrokeColor() {
		return mStrokePaint.getColor();
	}

	@Override
	public boolean isEditing() {
		return mEditing;
	}

	@Override
	public boolean isTextHint() {
		return mTextHint;
	}

	@Override
	public void setBounds(float left, float top, float right, float bottom) {
		if ((left != this.mBoundsF.left)
				|| (top != this.mBoundsF.top)
				|| (right != this.mBoundsF.right)
				|| (bottom != this.mBoundsF.bottom)) {
			mBoundsF.set(left, top, right,
					bottom);
			setTextSize(bottom - top);
		}
	}

	public void setMinTextSize(float minSize) {
		minTextSize = minSize;
	}
	
	public void setTextSize(float size) {
		if (size / getNumLines() != mPaint.getTextSize()){
			int i = getNumLines();
			mPaint.setTextSize(size / i);
			mStrokePaint.setTextSize(size / i);
			mStrokePaint.setStrokeWidth(size / i / 10.0F);
		}
	}
	
	@Override
	public void setText(CharSequence text) {
		setText((String)text);
	}

	@Override
	public void setText(String text) {
		mText = text;
		mTextHint = false;
		invalidate();
	}

	@Override
	public void setTextColor(int color) {
		mPaint.setColor(color);
	}

	@Override
	public void setTextHint(CharSequence hint) {
		setTextHint((String) hint);
	}

	@Override
	public void setTextHint(String hint) {
		mText = hint;
		mTextHint = true;
		invalidate();
	}

	@Override
	public void setTextStrokeColor(int strokeColor) {
		mStrokePaint.setColor(strokeColor);
	}

	protected void invalidate() {
		linesBreak.clear();
		int i = 0;
		while (true) {
			int j = mText.indexOf('\n', i);
			if (j <= -1) {
				linesBreak.add(Integer.valueOf(mText.length()));
				computeSize();
				return;
			}
			i = j + 1;
			linesBreak.add(Integer.valueOf(j));
		}
	}
	
	public boolean validateSize(RectF rtF) {
		int result = 1;
		if ((rtF.height() / getNumLines() < mMinHeight)
				|| (mText.length() < 1)){
			result = 0;
		}
		return result == 1 ? true : false;
	}
}
