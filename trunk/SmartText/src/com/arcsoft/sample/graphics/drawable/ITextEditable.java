package com.arcsoft.sample.graphics.drawable;

import android.graphics.Paint.FontMetrics;

public abstract interface ITextEditable
{
  public abstract void beginEdit();

  public abstract void endEdit();

  public abstract float getFontMetrics(FontMetrics fontMetrics);

  public abstract CharSequence getText();

  public abstract int getTextColor();

  public abstract float getTextSize();

  public abstract int getTextStrokeColor();

  public abstract boolean isEditing();

  public abstract boolean isTextHint();

  public abstract void setBounds(float left, float top, float right, float bottom);

  public abstract void setText(CharSequence text);

  public abstract void setText(String text);

  public abstract void setTextColor(int color);

  public abstract void setTextHint(CharSequence hint);

  public abstract void setTextHint(String hint);

  public abstract void setTextStrokeColor(int strokeColor);
}
