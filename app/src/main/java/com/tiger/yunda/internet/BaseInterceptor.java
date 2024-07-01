package com.tiger.yunda.internet;

import com.tiger.yunda.MainActivity;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseInterceptor implements Interceptor {
    private Map<String, String> headers;
    public BaseInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request()
                .newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }
        if (chain.request().headers().size() > 0 && Objects.isNull(MainActivity.loggedInUser)) {
            Request request = builder
                    .headers(chain.request().headers())
                    .build();
            return chain.proceed(request);
        }
        return chain.proceed(builder.build());

    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void clearHeader() {

    }

}
