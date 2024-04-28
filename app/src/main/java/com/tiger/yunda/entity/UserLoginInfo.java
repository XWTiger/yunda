package com.tiger.yunda.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.tiger.yunda.enums.RoleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "user_login_info")
@Data
@Builder
@AllArgsConstructor
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

}
