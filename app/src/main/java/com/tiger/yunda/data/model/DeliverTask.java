package com.tiger.yunda.data.model;

import com.tiger.yunda.ui.home.Mission;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliverTask implements Serializable {

    private String id;
    private String name;
    private int deptId;
    private String deptName;
    private int leaderId;
    private String leaderName;
    private String planStartTime;
    private String planEndTime;
    private int state;
    private String stateText;
    private int createUserId;
    private String createUserName;
    private String createTime;
    private String completeTime;
    private String score;
    private List<Trains> trains;

    private List<Mission> subtasks;

    public Mission allInOne() {
        String taskName = name + ": ";
        StringBuffer stringBuffer = new StringBuffer(taskName);
        trains.forEach(ele -> {
            stringBuffer.append(ele.getNo() + "车,");
        });
        String position  = stringBuffer.toString();
        String plan = "计划开始时间: " + planStartTime +", 计划结束时间: " + planEndTime;
        Mission mission = Mission.builder()
                .taskId(id)
                .id("")
                .positionName("")
                .state(state)
                .stateText(stateText)
                .inspectionUnit(position + plan)
                .masterMission(true)
                .inspectorId(0).positionId(0).state(1).build();
        return mission;
    }


}
