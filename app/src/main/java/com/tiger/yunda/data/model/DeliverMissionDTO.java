package com.tiger.yunda.data.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeliverMissionDTO implements Serializable {
    private int inspectorId;
    private String inspector;
    private int positionId;
    private String positionName;
    private String inspectionUnit;
    private String duration;


    public DeliverMssion covertToDeliverMission() {
        DeliverMssion deliverMssion = new DeliverMssion(inspectorId, inspector, positionId, positionName, inspectionUnit, duration);
        return deliverMssion;
    }
}
