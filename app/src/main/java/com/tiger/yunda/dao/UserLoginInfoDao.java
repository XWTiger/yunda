package com.tiger.yunda.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.tiger.yunda.entity.UserLoginInfo;

@Dao
public interface UserLoginInfoDao {


    @Query("select * from user_login_info where account=:account")
    UserLoginInfo getByAccount(String account);

    @Insert
    void insert(UserLoginInfo... userLoginInfos);

    @Query("delete from user_login_info where uid=:uid")
    void deleteByUid(String uid);
    @Query("select * from user_login_info where 1=1 limit 1")
    UserLoginInfo getSignedIn();

    @Update
    void update(UserLoginInfo userLoginInfo);
}
