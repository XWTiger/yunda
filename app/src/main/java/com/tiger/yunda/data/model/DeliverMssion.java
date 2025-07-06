package com.tiger.yunda.data.model;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



public class DeliverMssion {

    public ObservableInt inspectorId = new ObservableInt();  //巡检人id
    private ObservableField<String> inspector = new ObservableField<>();  //巡检人
    private ObservableInt positionId = new ObservableInt(); //所在列位id
    private ObservableField<String> positionName = new ObservableField<>(); //所在列位
    private ObservableField<String> inspectionUnit = new ObservableField<>();//巡检单元
    private ObservableField<String> duration = new ObservableField<>(); //计划时长、耗时


    private ObservableField<String> id = new ObservableField<>();//主任务或者子任务id


    public DeliverMssion(Integer inspectorId, String inspector, Integer positionId, String positionName, String inspectionUnit, String duration) {
        this.inspectorId.set(inspectorId);
        this.inspector.set(inspector);
        this.positionId.set(positionId);
        this.positionName.set(positionName);
        this.inspectionUnit.set(inspectionUnit);
        this.duration.set(duration);
    }



    public DeliverMssion(ObservableInt inspectorId, ObservableField<String> inspector, ObservableInt positionId, ObservableField<String> positionName, ObservableField<String> inspectionUnit, ObservableField<String> duration) {
        this.inspectorId = inspectorId;
        this.inspector = inspector;
        this.positionId = positionId;
        this.positionName = positionName;
        this.inspectionUnit = inspectionUnit;
        this.duration = duration;
    }

    public ObservableInt getInspectorId() {
        return inspectorId;
    }

    public void setInspectorId(ObservableInt inspectorId) {
        this.inspectorId = inspectorId;
    }

    public ObservableField<String> getInspector() {
        return inspector;
    }

    public void setInspector(ObservableField<String> inspector) {
        this.inspector = inspector;
    }

    public ObservableInt getPositionId() {
        return positionId;
    }

    public void setPositionId(ObservableInt positionId) {
        this.positionId = positionId;
    }

    public ObservableField<String> getPositionName() {
        return positionName;
    }

    public void setPositionName(ObservableField<String> positionName) {
        this.positionName = positionName;
    }

    public ObservableField<String> getInspectionUnit() {
        return inspectionUnit;
    }

    public void setInspectionUnit(ObservableField<String> inspectionUnit) {
        this.inspectionUnit = inspectionUnit;
    }

    public ObservableField<String> getDuration() {
        return duration;
    }

    public void setDuration(ObservableField<String> duration) {
        this.duration = duration;
    }

    public ObservableField<String> getId() {
        return id;
    }

    public void setId(ObservableField<String> id) {
        this.id = id;
    }
}
