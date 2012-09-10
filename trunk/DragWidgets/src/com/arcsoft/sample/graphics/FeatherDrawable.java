package com.arcsoft.sample.graphics;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

public abstract interface FeatherDrawable {
	public abstract void clearColorFilter();

	public abstract Rect copyBounds();

	public abstract void copyBounds(Rect rect);

	public abstract void draw(Canvas canvas);

	public abstract Rect getBounds();

	public abstract int getChangingConfigurations();

	public abstract Drawable getCurrent();

	public abstract int getIntrinsicHeight();

	public abstract int getIntrinsicWidth();

	public abstract int getLevel();

	public abstract int getMinimumHeight();

	public abstract int getMinimumWidth();

	public abstract int getOpacity();

	public abstract boolean getPadding(Rect rect);

	public abstract int[] getState();

	public abstract Region getTransparentRegion();

	public abstract void invalidateSelf();

	public abstract boolean isStateful();

	public abstract boolean isVisible();

	public abstract Drawable mutate();

	public abstract void scheduleSelf(Runnable what, long when);

	public abstract void setAlpha(int alpha);

	public abstract void setBounds(int left, int top, int right,
			int bottom);

	public abstract void setBounds(Rect rect);

	public abstract void setCallback(Drawable.Callback callback);

	public abstract void setChangingConfigurations(int configs);

	public abstract void setColorFilter(int color, PorterDuff.Mode mode);

	public abstract void setColorFilter(ColorFilter colorFilter);

	public abstract void setDither(boolean dither);

	public abstract void setFilterBitmap(boolean filter);

	public abstract boolean setLevel(int level);

	public abstract void setMinSize(float width, float height);

	public abstract boolean setState(int[] paramArrayOfInt);

	public abstract boolean setVisible(boolean visible, boolean restart);

	public abstract void unscheduleSelf(Runnable what);
	
	public abstract boolean validateSize(RectF rectF);
}
