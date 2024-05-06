package com.tiger.yunda.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "resource_location")
@Data
@Builder
@NoArgsConstructor
public class ResourceLocationEntity implements Serializable {
    /**
     * 主键
     */
    @PrimaryKey
    @NonNull
    private String id;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件扩展 如：.jpg、.pdf、.doc等
     */
    private String ext;

    /**
     * 媒体类型
     */
    private String contentType;
    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 本地位置
     */
    private String location;

    @Ignore
    public ResourceLocationEntity(String id, String fileName, String ext, String contentType, String filePath, String location) {
        this.id = id;
        this.fileName = fileName;
        this.ext = ext;
        this.contentType = contentType;
        this.filePath = filePath;
        this.location = location;
    }
}
