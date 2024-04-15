package com.tiger.yunda.data.model;

import com.tiger.yunda.enums.RoleType;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Serializable {

    private String userId;

    private String token;

    private String expires;

    private String tokenType;

    private String displayName;


    private String deptId;

    private RoleType role;

    public LoggedInUser(String userId, String displayName, RoleType role) {
        this.userId = userId;
        this.displayName = displayName;
        this.role = role;
    }

    public LoggedInUser(String userId, String token, String expires, String tokenType, String displayName, RoleType role) {
        this.userId = userId;
        this.token = token;
        this.expires = expires;
        this.tokenType = tokenType;
        this.displayName = displayName;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }
}