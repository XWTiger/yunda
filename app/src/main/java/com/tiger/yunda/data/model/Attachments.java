package com.tiger.yunda.data.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachments implements Serializable {

    private String id;//主键
    private String ext;//文件扩展 如：.jpg .pdf .doc
    private int fileSize;//文件大小 单位：byte
    private String contentType;//媒体类型
    private String url; //故障附件
    private int type;//类型， 1：发现故障 2：处理故障


}
