package com.example.yagesi.okhttputils.okhttphelper.body;

import com.example.yagesi.okhttputils.okhttphelper.body.HttpBody;

import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * jsonBody.
 */

public class JsonBody implements HttpBody {

    private String mContent;
    private String mCharset;
    private MediaType mMediaType = MediaType.parse("application/json");

    public JsonBody(String content, String charset) {
        mContent = content;
        mCharset = charset;
    }

    @Override
    public RequestBody genRequestBody() {
        RequestBody body = RequestBody.create(mMediaType, mContent);
        body.contentType().charset(Charset.forName(mCharset));
        return body;
    }
}
