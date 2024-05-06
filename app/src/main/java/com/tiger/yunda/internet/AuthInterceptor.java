package com.tiger.yunda.internet;

import android.content.Context;
import android.content.Intent;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.ui.login.LoginActivity;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;
    private MainActivity mainActivity;

    public static AtomicInteger loginFlag = new AtomicInteger(0);


    public AuthInterceptor(Context context, MainActivity mainActivity) {
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        // 检查HTTP状态码是否为401
        if (response.code() == 401) {
            // 这里执行401错误的处理逻辑
            // 例如：重新登录、更新token等
            if (loginFlag.get() == 0) {
                loginFlag.getAndIncrement();
                Intent intent = new Intent(context, LoginActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainActivity.startActivityForResult(intent, MainActivity.LOGIN_INTENT_RESULT_CODE);
            }
        }
        if (response.isSuccessful()) {
            if (loginFlag.get() > 0) {
                loginFlag.getAndDecrement();
            }
        }

        return response;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
