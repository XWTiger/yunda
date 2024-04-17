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

    private String id;
    private String ext;
    private int fileSize;
    private String contentType;
    private String url;
    private int type;


}
