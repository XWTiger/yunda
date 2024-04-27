package com.tiger.yunda;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.tiger.yunda.data.model.LoggedInUser;
import com.tiger.yunda.databinding.ActivityMainBinding;
import com.tiger.yunda.internet.RetrofitClient;
import com.tiger.yunda.ui.home.MissionFragment;
import com.tiger.yunda.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static int LOGIN_INTENT_RESULT_CODE = 9;
    public static String SYSTEM_VERSION = "V0.0.1";
    public static String TOKEN_STR_KEY = "token";

    public static String TOKEN_FLAG = "login_token";

    private ActivityMainBinding binding;

    public static LoggedInUser loggedInUser;


    public static RetrofitClient retrofitClient;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Instantiates the queue of Runnables as a LinkedBlockingQueue
    public static  final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 6;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // Creates a thread pool manager
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            1,       // Initial pool size
            NUMBER_OF_CORES,       // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Objects.isNull(retrofitClient)) {
            retrofitClient = RetrofitClient.getInstance(getApplicationContext());
            retrofitClient.setMainActivity(this);
        } else {
            //从缓存中获取 token 并放在header
            SharedPreferences sharedPreferences =  getApplicationContext().getSharedPreferences(TOKEN_FLAG, Context.MODE_PRIVATE);
            String token = sharedPreferences.getString(TOKEN_STR_KEY, "");
            if (Objects.nonNull(token)) {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                retrofitClient.addHeaders(headers);
            }

        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setSelectedItemId(R.id.navigation_mission);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_breakdown, R.id.navigation_mission, R.id.navigation_log, R.id.navigation_person, R.id.navigation_resource)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

/*
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });*/



    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_INTENT_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            loggedInUser = (LoggedInUser) Objects.requireNonNull(data).getSerializableExtra(LoginActivity.LOGIN_RESULT_KEY);
            SharedPreferences sharedPreferences =  getApplicationContext().getSharedPreferences(TOKEN_FLAG, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TOKEN_STR_KEY, loggedInUser.getToken());
            Log.i("xiaweihu", "login result =============> " + loggedInUser.getDisplayName());
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + loggedInUser.getToken());
            MainActivity.retrofitClient.addHeaders(headers);

        }
    }
}