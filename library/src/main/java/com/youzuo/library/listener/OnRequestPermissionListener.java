package com.youzuo.library.listener;

public interface OnRequestPermissionListener {

    //成功
    void onSuccessful(int[] grantResults);

    //失败
    void onFailure();
}
