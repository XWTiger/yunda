package com.tiger.yunda.ui.home;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//任务
public class Mission implements Serializable {

    private String id; //子任务id
    private String taskId; //任务id
    private int inspectorId; //巡检人id
    private String inspector; //巡检人
    private int positionId; //所在列位id
    private String positionName; //所在列位
    //车号
    private String trainNo;
    //巡检单元，车分为AB两个段；eg:车号-A、车号-B
    private String inspectionUnit;
    private String duration;
    //状态，1:下发中、2:已下发、3:巡检中、4:完成、5:已接收
    private int state;
    private String stateText;
    private String issuTime;
    private String createTime;
    private String receiveTime;
    private String patrolTime;
    private String completeTime;
    private String score;
    private String finish;
    private List<TrainLocations> trainLocations;

    private Boolean masterMission;



}
