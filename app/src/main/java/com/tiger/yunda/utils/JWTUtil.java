package com.tiger.yunda.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tiger.yunda.data.model.LoggedInUser;
import com.tiger.yunda.enums.RoleType;

public class JWTUtil {

    private static String IS_LEADER = "1";
    private static String IS_WOKER = "0";

    public static void decoder(String token, LoggedInUser loggedInUser) {
        try {
            DecodedJWT jwt= JWT.decode(token);
            loggedInUser.setDisplayName(jwt.getClaim("PersonalName").asString());
            loggedInUser.setUserId(jwt.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier").asString());
            loggedInUser.setDeptId(jwt.getClaim("DeptId").asString());
            loggedInUser.setTokenType("jwt");
            loggedInUser.setRoleName(jwt.getClaim("RoleName").asString());
            loggedInUser.setRoleId(jwt.getClaim("RoleId").asString());
            loggedInUser.setDeptId(jwt.getClaim("DeptId").asString());
            loggedInUser.setDeptName(jwt.getClaim("DeptName").asString());
            loggedInUser.setAccount(jwt.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name").asString());
            String leader = jwt.getClaim("IsTeamLeader").asString();
            if (IS_LEADER.equals(leader)) {
                loggedInUser.setRole(RoleType.WORKER_LEADER);
            } else {
                loggedInUser.setRole(RoleType.WORKER);
            }
            loggedInUser.setToken(token);
        } catch (JWTDecodeException ex){
            ex.printStackTrace();
        }
    }
}
