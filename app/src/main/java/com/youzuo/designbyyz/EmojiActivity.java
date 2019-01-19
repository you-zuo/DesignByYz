package com.youzuo.designbyyz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.youzuo.library.Base.BaseActivity;

public class EmojiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);
        setStatusBarFontColot(true,true);
    }

    @Override
    public void widgetClick(View view) {

    }
}
