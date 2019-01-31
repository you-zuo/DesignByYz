package com.youzuo.designbyyz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.luolc.emojirain.EmojiRainLayout;
import com.youzuo.library.Base.BaseActivity;
import com.youzuo.library.Utils.PhoneUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.group_emoji_container)
    EmojiRainLayout groupEmojiContainer;
    @BindView(R.id.mButton)
    Button mButton;
    @BindView(R.id.mButtonStop)
    Button mButtonStop;
    @BindView(R.id.mButtons)
    Button mButtons;
    @BindView(R.id.mShortMessage)
    Button mShortMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setStatusBarFontColot(true, true);
// add emoji sources
        groupEmojiContainer.addEmoji(R.drawable.emoji_1_3);
        groupEmojiContainer.addEmoji(R.mipmap.ic_launcher);
        groupEmojiContainer.addEmoji(R.drawable.emoji_3_3);
        groupEmojiContainer.addEmoji(R.drawable.emoji_4_3);
        groupEmojiContainer.addEmoji(R.drawable.emoji_5_3);

        // set emojis per flow, default 6
        groupEmojiContainer.setPer(10);

        // set total duration in milliseconds, default 8000
        groupEmojiContainer.setDuration(7200);

        // set average drop duration in milliseconds, default 2400
        groupEmojiContainer.setDropDuration(2400);

        // set drop frequency in milliseconds, default 500
        groupEmojiContainer.setDropFrequency(500);
    }

    @Override
    public void widgetClick(View view) {
        System.out.println();
    }

    @OnClick({R.id.mButton, R.id.mButtonStop, R.id.mButtons,R.id.mShortMessage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mButton:
                groupEmojiContainer.startDropping();
                break;
            case R.id.mButtonStop:
                groupEmojiContainer.stopDropping();
                break;
            case R.id.mButtons:
//                Intent intent = new Intent(MainActivity.this,. class);
//                startActivity(intent);
                break;
            case R.id.mShortMessage:
                PhoneUtil.sendMessage(MainActivity.this, "111111111111", "你好啊");
                break;
        }
    }

}
