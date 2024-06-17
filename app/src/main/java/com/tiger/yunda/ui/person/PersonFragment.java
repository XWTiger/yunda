package com.tiger.yunda.ui.person;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.dao.UserLoginInfoDao;
import com.tiger.yunda.data.ResetPassword;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.data.model.WorkLog;
import com.tiger.yunda.databinding.FragmentPersonBinding;
import com.tiger.yunda.entity.UserLoginInfo;
import com.tiger.yunda.internet.AuthInterceptor;
import com.tiger.yunda.internet.BaseInterceptor;
import com.tiger.yunda.service.DeviceService;
import com.tiger.yunda.ui.home.MissionFragment;
import com.tiger.yunda.ui.login.LoginActivity;
import com.tiger.yunda.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonFragment extends Fragment implements View.OnClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG_RESET = "reset";

    private static final String TAG_RESET_OK = "ok";

    private static final String TAG_LOGOUT = "logout";
    private FragmentPersonBinding fragmentPersonBinding;

    private DeviceService deviceService;

    private UserLoginInfoDao userLoginInfoDao;
    private String androidId;

    private NavController navController;
    private String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE
    };


    public PersonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonFragment newInstance(String param1, String param2) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceService = MainActivity.retrofitClient.create(DeviceService.class);
        userLoginInfoDao = MainActivity.appDatabase.userLoginInfoDao();
        this.requestPermissions(REQUIRED_PERMISSIONS, 10);
        androidId = Settings.System.getString(getActivity().getContentResolver(), Settings.System.ANDROID_ID);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) { //自定义应用栏
           /* SpannableString title = new SpannableString("个人");
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(title);*/
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        if (Objects.isNull(navController)) {
            navController = NavHostFragment.findNavController(this);
        }
        if (Objects.isNull(fragmentPersonBinding)) {
            fragmentPersonBinding = FragmentPersonBinding.inflate(inflater);
        }
        PackageManager manager = getContext().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getContext().getPackageName(), 0);
            String version = info.versionName;
            long versionCode = info.getLongVersionCode();
            fragmentPersonBinding.systemVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
           e.printStackTrace();
        }
        if (StringUtils.isNotBlank(MissionFragment.needUpdatePath)) {
            fragmentPersonBinding.buttonUpdate.setVisibility(View.VISIBLE);
        }
        fragmentPersonBinding.systemUser.setText(MainActivity.loggedInUser.getDisplayName());
        fragmentPersonBinding.button2.setTag(TAG_RESET);
        fragmentPersonBinding.button2.setOnClickListener(this);
        fragmentPersonBinding.logout.setTag(TAG_LOGOUT);
        fragmentPersonBinding.logout.setOnClickListener(this);
        fragmentPersonBinding.ok.setTag(TAG_RESET_OK);
        fragmentPersonBinding.ok.setOnClickListener(this);
        fragmentPersonBinding.androidId.setText(androidId);

        return fragmentPersonBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Objects.nonNull(fragmentPersonBinding)) {
            fragmentPersonBinding.systemUser.setText(MainActivity.loggedInUser.getDisplayName());
        }
        if (Objects.nonNull(MainActivity.loggedInUser) ) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + MainActivity.loggedInUser.getToken());
            MainActivity.retrofitClient.addHeaders(headers);
        }

    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (TAG_RESET.equals(tag)) {
            if (fragmentPersonBinding.oldPasswordLayout.getVisibility() == View.GONE) {
                fragmentPersonBinding.oldPasswordLayout.setVisibility(View.VISIBLE);
                fragmentPersonBinding.passwordLayout.setVisibility(View.VISIBLE);
                fragmentPersonBinding.newPasswordLayout.setVisibility(View.VISIBLE);
                fragmentPersonBinding.ok.setVisibility(View.VISIBLE);
            }  else {
                fragmentPersonBinding.oldPasswordLayout.setVisibility(View.GONE);
                fragmentPersonBinding.passwordLayout.setVisibility(View.GONE);
                fragmentPersonBinding.newPasswordLayout.setVisibility(View.GONE);
                fragmentPersonBinding.ok.setVisibility(View.GONE);
            }
        }

        if (TAG_RESET_OK.equals(tag)) {
            //重置密码提交
            String old = fragmentPersonBinding.oldPassword.getText().toString();
            if (StringUtils.isBlank(old)) {
                fragmentPersonBinding.oldPassword.setError("密码不能为空");
                return;
            } else {
                fragmentPersonBinding.oldPassword.setError(null);
            }

            String newP = fragmentPersonBinding.newPassword.getText().toString();
            if (StringUtils.isBlank(newP)) {
                fragmentPersonBinding.newPassword.setError("密码不能为空");
                return;
            } else {
                fragmentPersonBinding.newPassword.setError(null);
            }

            String newPa = fragmentPersonBinding.newPassword.getText().toString();
            if (StringUtils.isBlank(newPa)) {
                fragmentPersonBinding.newPasswordAgain.setError("密码不能为空");
                return;
            } else {
                fragmentPersonBinding.newPasswordAgain.setError(null);
            }
            if (!newP.equals(newPa)) {
                fragmentPersonBinding.newPasswordAgain.setError("两次新密码不一致");
            } else {
                fragmentPersonBinding.newPasswordAgain.setError(null);
            }
            ResetPassword resetPassword = ResetPassword.builder()
                    .id(Integer.parseInt(MainActivity.loggedInUser.getUserId()))
                    .newPwd(newPa)
                    .oldPwd(old)
                    .build();
            Call<ResponseBody> responseBodyCall = deviceService.updatePassword(resetPassword);
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "修改密码成功", Toast.LENGTH_SHORT).show();
                        fragmentPersonBinding.oldPasswordLayout.setVisibility(View.GONE);
                        fragmentPersonBinding.passwordLayout.setVisibility(View.GONE);
                        fragmentPersonBinding.newPasswordLayout.setVisibility(View.GONE);
                        fragmentPersonBinding.ok.setVisibility(View.GONE);
                    } else {
                        String errStr = null;
                        try {
                            errStr = response.errorBody().string();
                            Log.e("xiaweihu", "修改密码失败: ===========>" + errStr);
                            ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Log.e("xiaweihu", "修改密码失败: ===========>" , throwable);
                }
            });
        }

        if (TAG_LOGOUT.equals(tag)) {
            // 解绑设备
            //绑定设备mac 地址

            if (AuthInterceptor.loginFlag.get() <= 0) {
                AuthInterceptor.loginFlag.getAndIncrement();
            }
            if (Objects.nonNull(MainActivity.loggedInUser)) {
                Map<String, String> body = new HashMap<>();
                body.put("mac", androidId);
                Call<ResponseBody> call = deviceService.unbindMac(body);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            UserLoginInfo userLoginInfo = userLoginInfoDao.getSignedIn();
                            MainActivity.loggedInUser = null;
                            // 删除数据库登录信息
                            if (Objects.nonNull(userLoginInfo)) {
                                userLoginInfoDao.deleteByUid(userLoginInfo.getUid());
                            }

                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            getActivity().startActivityForResult(intent, MainActivity.LOGIN_INTENT_RESULT_CODE);
                        } else {
                            try {
                                String errStr = response.errorBody().string();
                                ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                                Log.e("xiaweihu", "绑定设备失败: ===========>" + errStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        Log.e("xiaweihu", "绑定设备失败: ===========>", throwable);
                    }
                });
            } else {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                getActivity().startActivityForResult(intent, MainActivity.LOGIN_INTENT_RESULT_CODE);
            }
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(MainActivity.TOKEN_FLAG, Context.MODE_PRIVATE);
            sharedPreferences.edit().remove(MainActivity.TOKEN_STR_KEY);
            sharedPreferences.edit().apply();
            //MainActivity.retrofitClient.clearHeaders();
        }
    }
}