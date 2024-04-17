package com.tiger.yunda.data.model;

import java.util.List;

/**
 * 故障记录
 */
public class BreakRecord {

    private String id;
    private String subtaskId;
    private String trainLocationId;
    private String trainLocationName;
    private int type;
    private String typeText;
    private String desc;
    private String isDiscretion;
    private String reportTime;
    private int reportUserId;
    private String reportUserName;
    private String handleDesc;
    private String handleTime;
    private int handleUserId;
    private String handleUserName;
    private int state;
    private String stateText;
    private String trainNo;
    private String deptName;
    private List<Attachments> attachments;
    private List<HandleAttachments> handleAttachments;
}
