package com.tiger.yunda.ui.home;

import java.io.Serializable;
import java.util.List;

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

    public Mission() {

    }
    public Mission(String id, String taskId, int inspectorId, String inspector, int positionId, String positionName, String trainNo, String inspectionUnit, String duration, int state, String stateText, String issuTime, String createTime, String receiveTime, String patrolTime, String completeTime, String score, String finish, List<TrainLocations> trainLocations) {
        this.id = id;
        this.taskId = taskId;
        this.inspectorId = inspectorId;
        this.inspector = inspector;
        this.positionId = positionId;
        this.positionName = positionName;
        this.trainNo = trainNo;
        this.inspectionUnit = inspectionUnit;
        this.duration = duration;
        this.state = state;
        this.stateText = stateText;
        this.issuTime = issuTime;
        this.createTime = createTime;
        this.receiveTime = receiveTime;
        this.patrolTime = patrolTime;
        this.completeTime = completeTime;
        this.score = score;
        this.finish = finish;
        this.trainLocations = trainLocations;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setInspectorId(int inspectorId) {
        this.inspectorId = inspectorId;
    }

    public int getInspectorId() {
        return inspectorId;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public String getInspector() {
        return inspector;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setInspectionUnit(String inspectionUnit) {
        this.inspectionUnit = inspectionUnit;
    }

    public String getInspectionUnit() {
        return inspectionUnit;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setStateText(String stateText) {
        this.stateText = stateText;
    }

    public String getStateText() {
        return stateText;
    }

    public void setIssuTime(String issuTime) {
        this.issuTime = issuTime;
    }

    public String getIssuTime() {
        return issuTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setPatrolTime(String patrolTime) {
        this.patrolTime = patrolTime;
    }

    public String getPatrolTime() {
        return patrolTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScore() {
        return score;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getFinish() {
        return finish;
    }

    public void setTrainLocations(List<TrainLocations> trainLocations) {
        this.trainLocations = trainLocations;
    }

    public List<TrainLocations> getTrainLocations() {
        return trainLocations;
    }

}
