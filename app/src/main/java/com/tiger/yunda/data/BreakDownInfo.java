package com.tiger.yunda.data;

import android.net.Uri;

import com.tiger.yunda.ui.common.CameraContentBean;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//上报故障对象

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BreakDownInfo {


    private String subtaskId;

    private String trainLocationId;

    //故障类型
    private Integer type;
    private int typePosition;

    private String desc;

    private Boolean discretion;


    //故障文件
    private List<CameraContentBean> files = new ArrayList<>();

    private String handleDesc;

    private List<CameraContentBean> handleFiles = new ArrayList<>();


    //false 故障 true 恢复拍照
    private boolean typeOfCameraAction;

}
