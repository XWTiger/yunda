package com.tiger.yunda;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.MacAddress;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.tiger.yunda.broadcast_receiver.NetworkChangeReceiver;
import com.tiger.yunda.dao.AppDatabase;
import com.tiger.yunda.dao.UserLoginInfoDao;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.LoggedInUser;
import com.tiger.yunda.databinding.ActivityMainBinding;
import com.tiger.yunda.entity.UserLoginInfo;
import com.tiger.yunda.enums.RoleType;
import com.tiger.yunda.internet.AuthInterceptor;
import com.tiger.yunda.internet.RetrofitClient;
import com.tiger.yunda.service.DeviceService;
import com.tiger.yunda.ui.home.MissionFragment;
import com.tiger.yunda.ui.home.MissionResult;
import com.tiger.yunda.ui.login.LoginActivity;
import com.tiger.yunda.utils.JWTUtil;
import com.tiger.yunda.utils.JsonUtil;
import com.tiger.yunda.utils.NetworkUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static int LOGIN_INTENT_RESULT_CODE = 9;

    public static String TOKEN_STR_KEY = "token";

    public static String TOKEN_FLAG = "login_token";

    public static String SERVICE_ADDR_FLAG = "service_addr";
    private ActivityMainBinding binding;

    public static LoggedInUser loggedInUser;

    public static  AppDatabase appDatabase;

    private NetworkChangeReceiver networkChangeReceiver;

    private String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
    };


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
        setTheme(R.style.Theme_Yunda);
        this.requestPermissions(REQUIRED_PERMISSIONS, 10);
        if (!NetworkUtil.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finish();
            return;
        }
        //注册广播接收
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        if (Objects.isNull(appDatabase)) {
            appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "yunda-database").allowMainThreadQueries().build();

        }


        //从缓存中获取 token 并放在header
        SharedPreferences sharedPreferences =  getApplicationContext().getSharedPreferences(TOKEN_FLAG, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(TOKEN_STR_KEY, "");
        String serviceAddr = sharedPreferences.getString(SERVICE_ADDR_FLAG, "");
        if (Objects.isNull(retrofitClient) && StringUtils.isNotBlank(serviceAddr)) {
            retrofitClient = RetrofitClient.getInstance(getApplicationContext(), "http://" + serviceAddr , new HashMap<>());
            retrofitClient.setMainActivity(this);
        } else {
            retrofitClient = RetrofitClient.getInstance(getApplicationContext());
            retrofitClient.setMainActivity(this);
        }
        if (StringUtils.isNotBlank(token)) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + token);
            loggedInUser = new LoggedInUser();
            JWTUtil.decoder(token, loggedInUser);
            retrofitClient.addHeaders(headers);
        }  else {
            //从数据库里面拿
            UserLoginInfoDao userLoginInfoDao = appDatabase.userLoginInfoDao();
            UserLoginInfo userLoginInfo = userLoginInfoDao.getSignedIn();
            if (Objects.nonNull(userLoginInfo)) {
               /* Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userLoginInfo.token);
                retrofitClient.addHeaders(headers);*/
                loggedInUser = new LoggedInUser();
                JWTUtil.decoder(userLoginInfo.getToken(), loggedInUser);
                loggedInUser.setUserId(userLoginInfo.getUid());
                loggedInUser.setAccount(userLoginInfo.getAccount());
                loggedInUser.setDeptId(userLoginInfo.getDeptId());
                loggedInUser.setDeptName(userLoginInfo.getDeptName());
                loggedInUser.setRole(userLoginInfo.getRole());
                loggedInUser.setRoleId(userLoginInfo.getRoleId());
                loggedInUser.setRoleName(userLoginInfo.getRoleName());
                if (loggedInUser.getRole() == RoleType.WORKER_LEADER) {
                    MissionFragment.leader = true;
                    MissionFragment.masterMission = true;
                } else {
                    MissionFragment.leader = false;
                    MissionFragment.masterMission = false;
                }
            }
        }

        if (Objects.isNull(loggedInUser) && StringUtils.isBlank(token) && AuthInterceptor.loginFlag.get() == 0) {
            AuthInterceptor.loginFlag.getAndIncrement();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivityForResult(intent, MainActivity.LOGIN_INTENT_RESULT_CODE);
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_INTENT_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            loggedInUser = (LoggedInUser) Objects.requireNonNull(data).getSerializableExtra(LoginActivity.LOGIN_RESULT_KEY);
            SharedPreferences sharedPreferences =  getApplicationContext().getSharedPreferences(TOKEN_FLAG, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TOKEN_STR_KEY, loggedInUser.getToken());
            editor.apply();
            Log.i("xiaweihu", "login result =============> " + loggedInUser.getDisplayName());
            retrofitClient = RetrofitClient.getInstance(getApplicationContext(), "http://" + LoginActivity.baseUrl , new HashMap<>());
            retrofitClient.setMainActivity(this);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + loggedInUser.getToken());
            MainActivity.retrofitClient.addHeaders(headers);
            if (AuthInterceptor.loginFlag.get() >= 1) {
                AuthInterceptor.loginFlag.getAndDecrement();
            }
            //MissionFragment.missionFlag.getAndDecrement();



            UserLoginInfoDao userLoginInfoDao = appDatabase.userLoginInfoDao();
            UserLoginInfo userLoginInfo = userLoginInfoDao.getByAccount(loggedInUser.getAccount());
            if (Objects.isNull(userLoginInfo)) {
                userLoginInfoDao.insert(UserLoginInfo.builder()
                        .uid(loggedInUser.getUserId())
                        .account(loggedInUser.getAccount())
                        .bindStatus(true)
                        .token(loggedInUser.getToken())
                        .deptId(loggedInUser.getDeptId())
                        .deptName(loggedInUser.getDeptName())
                        .role(loggedInUser.getRole())
                        .roleId(loggedInUser.getRoleId())
                        .roleName(loggedInUser.getRoleName())
                        .build());

            } else {
                userLoginInfo.setToken(loggedInUser.getToken());
                userLoginInfo.setBindStatus(true);
                userLoginInfoDao.update(userLoginInfo);
            }

        }
    }





}