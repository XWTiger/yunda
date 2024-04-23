package com.tiger.yunda.data;

import android.util.Log;
import android.widget.Toast;

import com.tiger.yunda.data.internet.TokenResult;
import com.tiger.yunda.data.model.LoggedInUser;
import com.tiger.yunda.enums.RoleType;
import com.tiger.yunda.internet.RetrofitClient;
import com.tiger.yunda.service.LoginService;
import com.tiger.yunda.ui.login.LoginActivity;
import com.tiger.yunda.utils.JWTUtil;

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
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "tiger", RoleType.WORKER_LEADER );

            LoginService loginService = LoginActivity.retrofitClient.create(LoginService.class);
            Map<String, String> body = new HashMap<>();
            body.put("user", username);
            body.put("password", password);
            Call<TokenResult> resultCall = loginService.login(body);
            TokenResult[] tokenResult = {null};
            resultCall.enqueue(new Callback<TokenResult>() {
                @Override
                public void onResponse(Call<TokenResult> call, Response<TokenResult> response) {
                    tokenResult[0] = response.body();

                }

                @Override
                public void onFailure(Call<TokenResult> call, Throwable throwable) {
                    Log.e("xiaweihu", "onFailure: ========> ", throwable.fillInStackTrace());
                }
            });
            if (Objects.isNull(tokenResult[0])) {
                return new Result.Error(new Exception("登录失败"));
            }
            fakeUser.setToken(tokenResult[0].getToken());
            fakeUser.setDisplayName(JWTUtil.decoder(tokenResult[0].getToken()));
            fakeUser.setDeptId("123");
            fakeUser.setUserId("1");
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}