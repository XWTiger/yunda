package com.tiger.yunda.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.tiger.yunda.enums.RoleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "user_login_info")
@Data
@Builder
@NoArgsConstructor
public class UserLoginInfo {
    @PrimaryKey
    @NonNull
    public String uid; //userid

    public String token;

    @ColumnInfo(name = "bind_status")
    public Boolean bindStatus;

    public String account;

    private String deptId;

    private String deptName;

    private RoleType role;

    private String roleId;

    private String roleName;

    @Ignore
    public UserLoginInfo(@NonNull String uid, String token, Boolean bindStatus, String account, String deptId, String deptName, RoleType role, String roleId, String roleName) {
        this.uid = uid;
        this.token = token;
        this.bindStatus = bindStatus;
        this.account = account;
        this.deptId = deptId;
        this.deptName = deptName;
        this.role = role;
        this.roleId = roleId;
        this.roleName = roleName;
    }
}
