package com.tiger.yunda.data.model;

import com.tiger.yunda.enums.RoleType;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoggedInUser implements Serializable {

    private String userId;

    private String token;

    private String expires;

    private String tokenType;

    private String displayName;


    private String deptId;

    private String deptName;

    private RoleType role;

    private String roleId;

    private String roleName;

    private String account;

    private String serviceUrl;


}