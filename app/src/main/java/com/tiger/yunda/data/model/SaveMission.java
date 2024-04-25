package com.tiger.yunda.data.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveMission implements Serializable {
    private String taskId;
    private int action;
    private List<DeliverMissionDTO> subtasks;
}
