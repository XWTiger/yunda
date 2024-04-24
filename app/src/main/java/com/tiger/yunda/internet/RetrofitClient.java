package com.tiger.yunda.internet;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tiger.yunda.MainActivity;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static volatile RetrofitClient retrofitClient = null;
    private static final int DEFAULT_TIMEOUT = 20;
    private Cache cache = null;

    private Retrofit retrofit;
    private File httpCacheDirectory;
    private static Context mContext;

    private MainActivity mainActivity;

    private  AuthInterceptor authInterceptor;

    private BaseInterceptor baseInterceptor;

    //http://ip:port/
    private static final String BASE_URL = "http://120.26.110.91:9291";
    private RetrofitClient(Context context) {
        this(context, BASE_URL, null);
    }
    private RetrofitClient(Context context, String url) {

        this(context, url, null);
    }

    private RetrofitClient(Context context, String url, Map<String, String> headers) {

        if (TextUtils.isEmpty(url)) {
            url = BASE_URL;
        }

        if ( httpCacheDirectory == null) {
            httpCacheDirectory = new File(mContext.getCacheDir(), "tamic_cache");
        }

        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
            }
        } catch (Exception e) {
            Log.e("OKHttp", "Could not create http cache", e);
        }
        authInterceptor  = new AuthInterceptor(context, mainActivity);
        baseInterceptor = new BaseInterceptor(headers);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()

                .cookieJar(new NovateCookieManger(context))
                .cache(cache)
                .addInterceptor(baseInterceptor)
                .addInterceptor(authInterceptor)
               /* .addInterceptor(new CaheInterceptor(context))*/
               /* .addNetworkInterceptor(new CaheInterceptor(context))*/
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build();

    }

    public  <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }


    public static synchronized RetrofitClient getInstance(Context context) {

        if (retrofitClient == null) {
            synchronized (BASE_URL) {
                if (context != null) {
                    mContext = context;
                }
                return new RetrofitClient(context);
            }
        }
        return retrofitClient;
    }

    public static RetrofitClient getInstance(Context context, String url, Map<String, String> headers) {

        if (retrofitClient == null) {
            synchronized (retrofitClient) {
                if (context != null) {
                    mContext = context;
                }
                return new RetrofitClient(context, url, headers);
            }
        }

        return retrofitClient;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        authInterceptor.setMainActivity(mainActivity);

    }

    public void addHeaders(Map<String, String> headers) {
        Map<String, String> header = baseInterceptor.getHeaders();
        if (Objects.isNull(header)) {
            header = headers;
        } else {
            header.putAll(headers);
        }
        baseInterceptor.setHeaders(header);

    }
}
