package com.example.yagesi.okhttputils.okhttphelper;

import java.io.IOException;

import okhttp3.Response;

/**
 * 回调.
 */

public interface HttpCallback {

    void onSuccess(Response response);

    void onFailed(IOException exception);
}
