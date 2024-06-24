package com.tiger.yunda.data;

import android.util.Log;
import android.widget.Toast;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.internet.TokenResult;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.LoggedInUser;
import com.tiger.yunda.enums.RoleType;
import com.tiger.yunda.internet.RetrofitClient;
import com.tiger.yunda.service.LoginService;
import com.tiger.yunda.ui.home.MissionFragment;
import com.tiger.yunda.ui.login.LoginActivity;
import com.tiger.yunda.utils.JWTUtil;
import com.tiger.yunda.utils.JsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource extends Thread {
    private LoginService loginService;
    private String username;
    private String password;
    private LoggedInUser fakeUser;
    public Result<LoggedInUser> login(String username, String password) {


        try {
            LoginDataSource lds = new LoginDataSource();
            lds.setUsername(username);
            lds.setPassword(password);

            lds.setLoginService(LoginActivity.retrofitClient.create(LoginService.class));
            Thread thread = lds;

            thread.start();
            thread.join();

            if (Objects.isNull(lds.getFakeUser())) {
                return new Result.Error(new Exception("用户或密码无效"));
            }
            return new Result.Success<>(lds.getFakeUser());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    @Override
    public void run() {
        fakeUser = new LoggedInUser();



        Map<String, String> body = new HashMap<>();
        body.put("user", username);
        body.put("password", password);
        Call<TokenResult> resultCall = loginService.login(body);
        try {
            Response<TokenResult> response = resultCall.execute();
            if (response.isSuccessful()) {

                TokenResult tokenResult = response.body();
                //http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier
                JWTUtil.decoder(tokenResult.getToken(), fakeUser);
                if (fakeUser.getRole() == RoleType.WORKER_LEADER) {
                    MissionFragment.leader = true;
                    MissionFragment.masterMission = true;
                }
                Log.i("xiaweihu", "user info ===========> " + fakeUser.toString());
            } else {
                String errStr = response.errorBody().string();
                Log.e("xiaweihu", "登录失败失败: ===========>" + errStr);
                fakeUser = null;
            }
        } catch (IOException e) {
           e.printStackTrace();
            fakeUser = null;
        }

    }

    public void logout() {
        // TODO: revoke authentication
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoggedInUser getFakeUser() {
        return fakeUser;
    }

    public void setFakeUser(LoggedInUser fakeUser) {
        this.fakeUser = fakeUser;
    }
}