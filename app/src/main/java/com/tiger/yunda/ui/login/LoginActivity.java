package com.tiger.yunda.ui.login;

import android.Manifest;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.DeliverTask;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.data.model.Version;
import com.tiger.yunda.entity.ResourceLocationEntity;
import com.tiger.yunda.internet.AuthInterceptor;
import com.tiger.yunda.internet.RetrofitClient;
import com.tiger.yunda.service.LoginService;
import com.tiger.yunda.service.MissionService;
import com.tiger.yunda.ui.home.Mission;
import com.tiger.yunda.ui.home.MissionResult;
import com.tiger.yunda.ui.login.LoginViewModel;
import com.tiger.yunda.ui.login.LoginViewModelFactory;
import com.tiger.yunda.databinding.ActivityLoginBinding;
import com.tiger.yunda.utils.DownLoadUtil;
import com.tiger.yunda.utils.JsonUtil;
import com.tiger.yunda.utils.NetworkUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    private LoginService loginService;

    public static String LOGIN_RESULT_KEY = "loginResult";

    public static RetrofitClient retrofitClient;

    public static String baseUrl = "";
    private String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestPermissions(REQUIRED_PERMISSIONS, 10);
        //校验网络是否正常
        if (!NetworkUtil.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        String androidId = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        if (Objects.isNull(loginService)) {
            loginService = MainActivity.retrofitClient.create(LoginService.class);
        }
        //重置登录标识
        if (AuthInterceptor.loginFlag.get() > 0) {
            AuthInterceptor.loginFlag.getAndDecrement();
        }

        /*if (!allPermissionsGranted()) {
            Log.w("xiaweihu", "application has no permission ");
            return;
        }*/
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        binding.deviceId.setText(androidId);
        setContentView(binding.getRoot());
        SharedPreferences addSp =  getApplicationContext().getSharedPreferences(MainActivity.TOKEN_FLAG, Context.MODE_PRIVATE);
        String serviceAddr = addSp.getString(MainActivity.SERVICE_ADDR_FLAG, "");
        if (StringUtils.isNotBlank(serviceAddr)) {
            binding.serviceAddr.setText(serviceAddr);
        } else {
            binding.serviceAddr.setText("10.60.0.190:9291");
        }
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        this.getSupportActionBar().hide();



        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    Intent intent = new Intent();
                    intent.putExtra(LOGIN_RESULT_KEY,  loginResult.getLoggedInUser());

                    setResult(Activity.RESULT_OK, intent);
                    //Complete and destroy login activity once successful
                    finish();
                }


            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://" + binding.serviceAddr.getText().toString() + "/hub/Notification/negotiate?negotiateVersion=1";
                CountDownLatch countDownLatch = new CountDownLatch(1);
                AtomicBoolean flag = new AtomicBoolean(false);
                MainActivity.threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!NetworkUtil.connectionNetwork(url)) {
                            flag.set(false);
                        } else {
                            flag.set(true);
                        }
                        countDownLatch.countDown();
                    }
                });
                try {
                    countDownLatch.await();
                    if (!flag.get()) {
                        binding.serviceAddr.setError("服务器无法连接");
                        binding.serviceAddr.setFocusedByDefault(true);
                        Toast.makeText(getApplicationContext(), "服务器无法连接", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        binding.serviceAddr.setError(null);
                        baseUrl = binding.serviceAddr.getText().toString();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                retrofitClient = RetrofitClient.getInstance(getApplicationContext(), "http://" + binding.serviceAddr.getText().toString(), new HashMap<>());
                SharedPreferences sharedPreferences =  getApplicationContext().getSharedPreferences(MainActivity.TOKEN_FLAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MainActivity.SERVICE_ADDR_FLAG, binding.serviceAddr.getText().toString());
                editor.apply();
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }





    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }




}