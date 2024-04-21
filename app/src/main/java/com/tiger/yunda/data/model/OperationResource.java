package com.tiger.yunda.data.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作手册资料
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationResource {

    /**
     * 主键
     */
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
     * 文件大小 单位：byte
     */
    private int fileSize;
    /**
     * 媒体类型
     */
    private String contentType;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 文件分类
     */
    private int fileType;
    /**
     * 文件分类名称
     */
    private String fileTypeText;
    /**
     * 文件描述
     */
    private String desc;
    /**
     * 上传人id
     */
    private int uploadUserId;
    /**
     * 上传人姓名
     */
    private String uploadUserName;
    /**
     * 上传人时间
     */
    private String uploadTime;

}
