package com.tiger.yunda.data.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 故障
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faults {
    private String id;
    /**
     * 子任务id
     */
    private String subtaskId;
    /**
     * 巡检车位置id
     */
    private String trainLocationId;
    /**
     * 巡检车位置
     */
    private String trainLocationName;
    /**
     * 故障类型，字典表
     */
    private int type;
    private String typeText;
    /**
     * 故障描述
     */
    private String desc;
    /**
     * 是否自行处理
     */
    private String isDiscretion;
    /**
     * 提报时间 2024-04-08 19:18:50
     */
    private String reportTime;
    /**
     * 提报人id
     */
    private int reportUserId;
    /**
     * 提报人
     */
    private String reportUserName;
    /**
     * 故障处理描述
     */
    private String handleDesc;
    /**
     * 故障处理时间
     */
    private String handleTime;
    /**
     * 处理人id
     */
    private int handleUserId;
    /**
     * 处理人名称
     */
    private String handleUserName;
    /**
     * 状态，1:未处理、2:已处理
     */
    private int state;
    private String stateText;
    /**
     * 故障车辆
     */
    private String trainNo;
    /**
     * 巡检班组
     */
    private String deptName;

    private Boolean isAppeal;
    /**
     *
     * 故障附件
     */
    private List<Attachments> attachments;
    /**
     * 故障处理附件
     */
    private List<HandleAttachments> handleAttachments;
}
