package com.tiger.yunda.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.tiger.yunda.entity.ResourceLocationEntity;
import com.tiger.yunda.entity.UserLoginInfo;

@Database(entities = {UserLoginInfo.class, ResourceLocationEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserLoginInfoDao userLoginInfoDao();

    public abstract ResourceLocationDao resourceLocationDao();
}
