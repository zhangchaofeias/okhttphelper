package com.example.yagesi.okhttputils.okhttphelper.body;

import android.support.annotation.Nullable;

import java.util.Map;

/**
 * bodyFactory.
 */

public class BodyFactory {

    public static final int BODY_FORM = 1;
    public static final int BODY_JSON = 2;
    private static String mDefaultCharset = "UTF-8";

    public static HttpBody createBody(int type, @Nullable Map<String, String> params, @Nullable String
            jsonContent) {
        switch (type) {
            case BODY_FORM:
                return new ZFormBody(params, mDefaultCharset);
            case BODY_JSON:
                return new JsonBody(jsonContent, mDefaultCharset);
            default:
                return null;
        }
    }

    public static HttpBody createBody(int type, String charset, @Nullable Map<String, String> params, @Nullable String
            jsonContent) {
        switch (type) {
            case BODY_FORM:
                return new ZFormBody(params, charset);
            case BODY_JSON:
                return new JsonBody(jsonContent, charset);
            default:
                return null;
        }
    }
}
