package com.arcsoft.sample;

import com.arcsoft.sample.R;
import com.arcsoft.sample.widgets.EditTextScaleRotateView;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;

public class MainActivity extends Activity {
    EditTextScaleRotateView mTextView;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (EditTextScaleRotateView)findViewById(R.id.EditTextScaleRotateView);
        restoreTextState();
    }
	

	protected void restoreTextState() {
		EditTextScaleRotateView.TextState state = new EditTextScaleRotateView.TextState();
		String STORE_NAME = "TextStateInfo";
        SharedPreferences settings = getSharedPreferences(STORE_NAME, MODE_PRIVATE);
        state.mOutlineEllipse = settings.getInt("mOutlineEllipse", 0xff00ff00);
        state.mOutlineStrokeColor = settings.getInt("mOutlineStrokeColor", 0xffdddddd);
        state.mPadding = settings.getInt("mPadding", 15);
        state.mTextColor = settings.getInt("mTextColor", 0xffffffff);
        state.mDegree = settings.getFloat("mDegree", 0);
        state.mRectCenterX = settings.getFloat("mRectCenterX", 0);
        state.mRectCenterY = settings.getFloat("mRectCenterY", 0);
        state.mStrokeWidth = settings.getFloat("mStrokeWidth", 5);
        state.mTextSize = settings.getFloat("mTextSize", 24);
        state.mText = settings.getString("mText", "");
        
        mTextView.initBy(state);
	}
	
	protected void saveTextState() {
		EditTextScaleRotateView.TextState state = mTextView.getTextState();
        String STORE_NAME = "TextStateInfo";
        SharedPreferences settings = getSharedPreferences(STORE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("mOutlineEllipse", state.mOutlineEllipse);
        editor.putInt("mOutlineStrokeColor", state.mOutlineStrokeColor);
        editor.putInt("mPadding", state.mPadding);
        editor.putInt("mTextColor", state.mTextColor);
        editor.putFloat("mDegree", state.mDegree);
        editor.putFloat("mRectCenterX", state.mRectCenterX);
        editor.putFloat("mRectCenterY", state.mRectCenterY);
        editor.putFloat("mStrokeWidth", state.mStrokeWidth);
        editor.putFloat("mTextSize", state.mTextSize);
        editor.putString("mText", state.mText);
	    editor.commit();
	}

	@Override
	protected void onStop() {
		saveTextState();
		super.onStop();
	}

	
	
	
}
