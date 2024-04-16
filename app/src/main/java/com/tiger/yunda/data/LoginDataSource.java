package com.tiger.yunda.data;

import com.tiger.yunda.data.model.LoggedInUser;
import com.tiger.yunda.enums.RoleType;

import java.io.IOException;

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
            fakeUser.setToken("aaasd");
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