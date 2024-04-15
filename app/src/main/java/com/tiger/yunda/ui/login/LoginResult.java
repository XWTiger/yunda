package com.tiger.yunda.ui.login;

import androidx.annotation.Nullable;

import com.tiger.yunda.data.model.LoggedInUser;
import com.tiger.yunda.enums.RoleType;

import java.io.Serializable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult implements Serializable {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;

    private  LoggedInUser loggedInUser;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    public LoginResult(@Nullable LoggedInUserView success, @Nullable LoggedInUser loggedInUser) {
        this.success = success;

        this.loggedInUser = loggedInUser;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(LoggedInUser loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}