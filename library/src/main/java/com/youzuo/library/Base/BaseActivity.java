package com.youzuo.library.Base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youzuo.library.R;
import com.youzuo.library.Utils.ActivityUtil;
import com.youzuo.library.Utils.NetworkUtil;
import com.youzuo.library.Utils.VersionUtils;
import com.youzuo.library.listener.OnRequestPermissionListener;

import java.util.ArrayList;
import java.util.List;

import static com.youzuo.library.Base.BaseApplication.APP_NAME;
import static com.youzuo.library.Base.BaseApplication.isDebug;

public abstract class BaseActivity extends AppCompatActivity {

    //Activity的IntentFilter匹配规则
    private IntentFilter intentFilter;
    //判断网络的广播
    private NetworkChangeReceiver networkChangeReceiver;
    //引用的标题栏 左边返回键 网络状态加载不同布局
    private LinearLayout include_title, Layout_title_back, network_status_layout, ll_title_bar;

    //标题栏中间文字
    private TextView mTv_title_content;
    //标题栏右边文字
    private TextView mTv_title_ok;
    //整个Title
    LinearLayout layout_include;

    protected boolean useThemestatusBarColor = false;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值

    /*
     * 沉浸式状态栏
     * */
    protected boolean useStatusBarColor = false;//是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置

    /**
     * 是否允许全屏
     **/
    private boolean mAllowFullScreen = false;

    /**
     * 是否沉浸状态栏
     **/
    private boolean mIsSetStatusBar = false;

    /*
     * 设置状态栏的显示隐藏
     * */
    private boolean mIsVisibility = false;

    /**
     * 是否允许全屏
     **/
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
        Log.d("renchunlin", "==状态==: " + mAllowFullScreen);

        /** 是否允许全屏 **/
        if (mAllowFullScreen) {
            this.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /*
     * 子类设置沉浸式接口及字体颜色 isSetStatusBar是否沉浸 isSetStatusBarColor true为白色 状态栏字体颜色
     *
     * */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setSetStatusBar(boolean isSetStatusBar, boolean isSetStatusBarColor) {
        this.mIsSetStatusBar = isSetStatusBar;
        this.useStatusBarColor = isSetStatusBarColor;

        if (isSetStatusBar) {
            setStatusBar();
        }

    }

    private Activity activity;

    private int permissionRequestCode;
    private List<String> mListPermissions = new ArrayList<>();
    private OnRequestPermissionListener listener;
    private int titlebar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.setContentView(R.layout.activity_base);
        //添加Activity
        ActivityUtil.getInstance().addActivity(activity);
        findView();


    }

    private void findView() {
        ll_title_bar = findViewById(R.id.ll_title_bar);
        titlebar = getStatusBarHeight(this);
        ll_title_bar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, titlebar));
        include_title = findViewById(R.id.include_title);
        Layout_title_back = findViewById(R.id.Layout_title_back);
        Layout_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTv_title_content = findViewById(R.id.mTv_title_content);
        mTv_title_ok = findViewById(R.id.mTv_title_ok);
        network_status_layout = findViewById(R.id.network_status_layout);

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

        layout_include = findView(R.id.layout_include);

    }

    /*
     * 子类设置状态栏的显示隐藏
     * */
    public void setVisibilityTitle(boolean isVisibilityTitle) {
        this.mIsVisibility = isVisibilityTitle;

        if (mIsVisibility) {
            layout_include.setVisibility(View.GONE);
        }
    }

    //调用销毁Activity
    @Override
    protected void onDestroy() {
        // Activity销毁时，提示系统回收
        // 移除Activity
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        ActivityUtil.getInstance().removeActivity(activity);
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.BELOW, R.id.Layout_title);
        if (null != include_title)
            include_title.addView(view, lp);

    }

    //封装findViewById
    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    //开发者模式
    public void TLog(String msg) {
        if (isDebug) {
            Log.d(APP_NAME, msg);
        }
    }

    //吐司
    public void ShowToast(String toastMsg) {
        Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 隐藏软件盘
     */
    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            assert imm != null;
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (BaseActivity.this.getCurrentFocus() != null) {
                if (BaseActivity.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(BaseActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /*
     * 防止快速点击
     * */
    private boolean fastClick() {
        long lastClick = 0;
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }

    public abstract void widgetClick(View view);

    public void onClick(View view) {
        if (fastClick())
            widgetClick(view);
    }


    /**
     * 设置中间标题文字
     *
     * @param c
     */
    public void setTitleText(CharSequence c) {
        if (mTv_title_content != null)
            mTv_title_content.setText(c);
    }

    /**
     * 设置右边标题
     *
     * @param c
     */
    public void setOKText(CharSequence c) {
        if (mTv_title_ok != null)
            mTv_title_ok.setText(c);
    }

    /**
     * 设置右边按钮是否显示
     *
     * @param visible
     */
    public void setOkVisibity(boolean visible) {
        if (mTv_title_ok != null) {
            if (visible)
                mTv_title_ok.setVisibility(View.VISIBLE);
            else
                mTv_title_ok.setVisibility(View.GONE);
        }
    }

    //左边按钮点击事件
    public LinearLayout getLlBasetitleBack() {
        return Layout_title_back;
    }

    //中间文字点击事件
    public TextView getTvBasetitleTitle() {
        return mTv_title_content;
    }

    //右边按键点击事件
    public TextView getTvBasetitleOK() {
        return mTv_title_ok;
    }

    //设置状态栏沉浸
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    protected void setStatusBar() {
        if (VersionUtils.isLollipop()) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(Color.parseColor("#f1439c")));
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (VersionUtils.isKITKAT()) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (VersionUtils.isM() && !useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (NetworkUtil.isConnected(BaseActivity.this)) {
                network_status_layout.setVisibility(View.GONE);
                //Toast.makeText(BaseActivity.this, "有网络", Toast.LENGTH_SHORT).show();
            } else {
                network_status_layout.setVisibility(View.VISIBLE);
                //Toast.makeText(BaseActivity.this, "无网络", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //请求权限
    protected void checkPermissions(String[] permissions, int requestCode, OnRequestPermissionListener listener) {
        //判断不能为空
        if (permissions != null && permissions.length != 0) {
            permissionRequestCode = requestCode;
            this.listener = listener;
            for (int i = 0; i < permissions.length; i++) {
                //判断未申请
                if (!checkSelfPermissions(permissions[i])) {
                    mListPermissions.add(permissions[i]);
                }
            }
            applyPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequestCode) {
            if (grantResults.length > 0) {
                listener.onSuccessful(grantResults);
            } else {
                listener.onFailure();
            }
        }
    }

    //检查权限
    private boolean checkSelfPermissions(String permissions) {
        if (ActivityCompat.checkSelfPermission(this, permissions) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    //申请权限
    private void applyPermissions() {
        //判断权限集合是否为空
        if (!mListPermissions.isEmpty()) {
            //申请权限
            int size = mListPermissions.size();
            ActivityCompat.requestPermissions(this, mListPermissions.toArray(new String[size]), permissionRequestCode);
            mListPermissions.clear();
        } else {
            listener.onFailure();
        }
    }

    /***
     *获取系统状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
