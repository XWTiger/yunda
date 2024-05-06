package com.tiger.yunda.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.tiger.yunda.entity.ResourceLocationEntity;

@Dao
public interface ResourceLocationDao {

    @Insert
    void insert(ResourceLocationEntity ...resourceLocationEntity);

    @Query("select * from resource_location where id=:id")
    ResourceLocationEntity getById(String id);

    @Delete
    void delete(ResourceLocationEntity resourceLocationEntity);
}
