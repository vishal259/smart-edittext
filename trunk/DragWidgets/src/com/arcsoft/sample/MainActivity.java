package com.arcsoft.sample;

import com.arcsoft.sample.R;
import com.arcsoft.sample.widgets.EditTextScaleRotateView;

import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {
    EditTextScaleRotateView mTextView;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (EditTextScaleRotateView)findViewById(R.id.EditTextScaleRotateView);
        
        //EditTextScaleRotateView.TextState params = new EditTextScaleRotateView.TextState();
        //TODO ....
        //mTextView.initBy(params);
    }
}
