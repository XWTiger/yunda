package com.tiger.yunda.data.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HandleAttachments implements Serializable {

    private String id;
    private String ext;
    private int fileSize;
    private String contentType;
    private String url;
    private int type;
}
