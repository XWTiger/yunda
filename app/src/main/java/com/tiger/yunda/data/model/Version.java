package com.tiger.yunda.data.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Version implements Serializable {
    private String id;
    private String versionNo;
    private String versionNumber;
    private String createTime;
    private String filePath;
    private Boolean isForceUpdate;
}
