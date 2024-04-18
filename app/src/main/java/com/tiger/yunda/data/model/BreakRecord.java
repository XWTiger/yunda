package com.tiger.yunda.data.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 故障记录
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BreakRecord implements Serializable {

    private String id;//主键
    private String subtaskId; //子任务id
    private String trainLocationId; //巡检车位置id
    private String trainLocationName;//巡检车位置
    private int type; //故障类型，字典表
    private String typeText; //故障类型
    private String desc; //故障描述
    private String isDiscretion;//是否自行处理
    private String reportTime; //提报时间
    private int reportUserId;//提报人id
    private String reportUserName;//提报人
    private String handleDesc;//故障处理描述
    private String handleTime;//处理时间
    private int handleUserId;//处理人id
    private String handleUserName;//处理人
    private int state; //状态, 1:未处理,2:已处理
    private String stateText;//状态名称
    private String trainNo;//故障车辆
    private String deptName; //巡检班组
    private List<Attachments> attachments;//故障附件
    private List<HandleAttachments> handleAttachments;//故障处理附件
}
