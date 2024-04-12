package com.tiger.yunda.data;

import android.net.Uri;

import com.tiger.yunda.ui.common.CameraContentBean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//上报故障对象

@Data
public class BreakDownInfo {


    private String SubtaskId;

    private String TrainLocationId;

    private Integer Type;
    private int typePosition;

    private String Desc;

    private Boolean discretion;


    //故障文件
    private List<CameraContentBean> Files;

    private String HandleDesc;

    private List<CameraContentBean> HandleFiles;


    //false 故障 true 恢复拍照
    private boolean typeOfCameraAction;


    public BreakDownInfo(String subtaskId, String trainLocationId, Integer type, String desc, Boolean isDiscretion, List<CameraContentBean> files, String handleDesc, List<CameraContentBean> handleFiles, boolean typeOfCameraAction) {
        SubtaskId = subtaskId;
        TrainLocationId = trainLocationId;
        Type = type;
        Desc = desc;
        discretion = isDiscretion;
        Files = files;
        HandleDesc = handleDesc;
        HandleFiles = handleFiles;
        this.typeOfCameraAction = typeOfCameraAction;
    }

    public BreakDownInfo() {
    }

    public String getSubtaskId() {
        return SubtaskId;
    }

    public void setSubtaskId(String subtaskId) {
        SubtaskId = subtaskId;
    }

    public String getTrainLocationId() {
        return TrainLocationId;
    }

    public void setTrainLocationId(String trainLocationId) {
        TrainLocationId = trainLocationId;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        Type = type;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public Boolean getDiscretion() {
        return discretion;
    }

    public void setDiscretion(Boolean discretion) {
        this.discretion = discretion;
    }

    public List<CameraContentBean> getFiles() {
        return Files;
    }

    public void setFiles(List<CameraContentBean> files) {
        Files = files;
    }

    public String getHandleDesc() {
        return HandleDesc;
    }

    public void setHandleDesc(String handleDesc) {
        HandleDesc = handleDesc;
    }

    public List<CameraContentBean> getHandleFiles() {
        return HandleFiles;
    }

    public void setHandleFiles(List<CameraContentBean> handleFiles) {
        HandleFiles = handleFiles;
    }

    public boolean isTypeOfCameraAction() {
        return typeOfCameraAction;
    }

    public void setTypeOfCameraAction(boolean typeOfCameraAction) {
        this.typeOfCameraAction = typeOfCameraAction;
    }
}
