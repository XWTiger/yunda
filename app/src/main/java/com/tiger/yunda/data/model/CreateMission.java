package com.tiger.yunda.data.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMission {

    private String name;
    private int deptId;
    private int leaderId;
    private String planStartTime;
    private String planEndTime;
    private List<String> trainNo;
}
