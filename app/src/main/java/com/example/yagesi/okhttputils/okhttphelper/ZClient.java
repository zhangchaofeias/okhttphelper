package com.example.yagesi.okhttputils.okhttphelper;

import android.text.format.DateUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * okHttpClient.
 */

public class ZClient extends OkHttpClient {

    private static final long TIME_OUT_DEFAULT = 15 * DateUtils.SECOND_IN_MILLIS;
    private static ZClient sHttpClient;
    private OkHttpClient.Builder mBuilder = new OkHttpClient().newBuilder();
    private Request.Builder mRequestBuilder = new Request.Builder();
    private long mTimeOut;
    private int mRetryTimes = 1;

    /**
     * 获取HTTPClient.
     *
     * @return client
     */
    public static ZClient getInstance() {
        if (sHttpClient == null) {
            synchronized (ZClient.class) {
                if (sHttpClient == null) {
                    sHttpClient = new ZClient();
                }
            }
        }
        return sHttpClient;
    }

    public void addHeaders(Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
            mRequestBuilder.headers(headerBuilder.build());
        }
    }

    public void setRetryTimes(int times) {
        mRetryTimes = times;
    }

    public void setTimeOut(long timeOut) {
        mTimeOut = timeOut;
    }

    public Response syncGet(String url) throws IOException {
        Request request = mRequestBuilder.url(url).build();
        return execute(request);
    }

    public void get(String url, HttpCallback callback) {
        Request request = mRequestBuilder.url(url).build();
        enqueue(request, callback);
    }

    public Response syncPut(String url, RequestBody body) throws IOException {
        Request request = mRequestBuilder.url(url).put(body).build();
        return execute(request);
    }

    public void put(String url, RequestBody body, HttpCallback callback) {
        Request request = mRequestBuilder.url(url).put(body).build();
        enqueue(request, callback);
    }

    public Response syncPost(String url, RequestBody body) throws IOException {
        Request request = mRequestBuilder.url(url).post(body).build();
        return execute(request);
    }

    public void post(String url, RequestBody body, HttpCallback callback) {
        Request request = mRequestBuilder.url(url).post(body).build();
        enqueue(request, callback);
    }

    private Response execute(Request request) throws IOException {
        int retryTimes = 0;
        while (retryTimes < mRetryTimes) {
            mBuilder.connectTimeout(mTimeOut == 0 ? TIME_OUT_DEFAULT : mTimeOut, TimeUnit.MILLISECONDS);
            Call call = mBuilder.build().newCall(request);
            try {
                return call.execute();
            } catch (SocketTimeoutException e) {
                retryTimes++;
            }
        }
        return null;
    }

    private void enqueue(final Request request, final HttpCallback callback) {
        final int[] retryTimes = new int[1];
        mBuilder.connectTimeout(mTimeOut == 0 ? TIME_OUT_DEFAULT : mTimeOut, TimeUnit.MILLISECONDS);
        Call call = mBuilder.build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException && retryTimes[0] < mRetryTimes) {
                    retryTimes[0]++;
                    mBuilder.build().newCall(request).enqueue(this);
                } else {
                    callback.onFailed(e);
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response);
            }
        });

    }

}
