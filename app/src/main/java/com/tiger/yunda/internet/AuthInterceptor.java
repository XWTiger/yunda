package com.tiger.yunda.internet;

import android.content.Context;
import android.content.Intent;

import com.tiger.yunda.ui.login.LoginActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;


    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        // 检查HTTP状态码是否为401
        if (response.code() == 401) {
            // 这里执行401错误的处理逻辑
            // 例如：重新登录、更新token等
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        return response;
    }
}
