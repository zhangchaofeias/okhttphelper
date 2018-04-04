package com.example.yagesi.okhttputils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yagesi.okhttputils.okhttphelper.HttpCallback;
import com.example.yagesi.okhttputils.okhttphelper.ZClient;

import java.io.IOException;

import okhttp3.Response;

public class MainActivity extends Activity {

    private static final int MESSAGE_SUCCESS = 1;
    private static final int MESSAGE_ERROR = 2;
    private Button mAsyncButton;
    private Button mSyncButton;
    private ImageView mImageView;
    private ZClient mZClient;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SUCCESS:
                    byte[] bytes = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    mImageView.setImageBitmap(bitmap);
                    break;
                case MESSAGE_ERROR:
                    Exception exception = (Exception) msg.obj;
                    Toast.makeText(MainActivity.this, getString(R.string.image_load_failed) + exception.getMessage(), Toast
                            .LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mZClient = ZClient.getInstance();
        initView();
    }

    private void initView() {
        mAsyncButton = findViewById(R.id.async_btn);
        mSyncButton = findViewById(R.id.sync_btn);
        mImageView = findViewById(R.id.imageView);
        asyncDownload();
        syncDownload();
    }

    private void syncDownload() {
        mSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = mZClient.syncGet(Constants.LEI_JIE);
                            sendMessage(MESSAGE_SUCCESS, response.body().bytes());
                        } catch (IOException exception) {
                            sendMessage(MESSAGE_ERROR, exception);
                        }
                    }
                }).start();

            }
        });
    }

    private void asyncDownload() {
        mAsyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZClient.get(Constants.LEI_JIE, new HttpCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        if (response.code() == 200) {
                            byte[] bytes = null;
                            try {
                                bytes = response.body().bytes();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sendMessage(MESSAGE_SUCCESS, bytes);
                        } else {
                            sendMessage(MESSAGE_ERROR, new Exception(getString(R.string.image_load_failed) + response.code()));
                        }

                    }

                    @Override
                    public void onFailed(IOException exception) {
                        sendMessage(MESSAGE_ERROR, exception);
                    }
                });
            }
        });
    }

    private void sendMessage(int what, Object obj) {
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        mHandler.sendMessage(message);
    }
}
