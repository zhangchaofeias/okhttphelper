package com.example.yagesi.okhttputils.okhttphelper.body;

import com.example.yagesi.okhttputils.okhttphelper.body.HttpBody;

import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * formBody.
 */

public class ZFormBody implements HttpBody {

    private Map<String, String> mParams;
    private String mCharset = "UTF-8";

    public ZFormBody(Map<String, String> params, String charset) {
        mParams = params;
        mCharset = charset;
    }

    @Override
    public RequestBody genRequestBody() {

        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            if (entry != null) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody body = builder.build();
        body.contentType().charset(Charset.forName(mCharset));
        return body;
    }
}
