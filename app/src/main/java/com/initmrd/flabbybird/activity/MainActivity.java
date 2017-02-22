package com.initmrd.flabbybird.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.initmrd.flabbybird.view.FlabbyBird;

public class MainActivity extends Activity {

    FlabbyBird mFlabbyBird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFlabbyBird = new FlabbyBird(this);
        setContentView(mFlabbyBird);

    }
}
