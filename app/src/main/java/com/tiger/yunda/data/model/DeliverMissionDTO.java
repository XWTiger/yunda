package com.tiger.yunda.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliverMissionDTO {
    private int inspectorId;
    private String inspector;
    private int positionId;
    private String positionName;
    private String inspectionUnit;
    private String duration;
}
