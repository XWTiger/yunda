package com.tiger.yunda.data.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMission {

    private String name ;
    private String deptId;
    private String leaderId;
    private String planStartTime = "";
    private String planEndTime = "";
    private List<String> trainNo;
    private String trainNoStr = "";


    public void covertTrainNoStr() {
        if (StringUtils.isNotBlank(trainNoStr)) {
            String[] arr = trainNoStr.split(",");
            List<String> buffer = new ArrayList<>();
            for (int i = 0; i < arr.length; i++) {
                buffer.add(arr[i]);
            }
            trainNo = buffer;
        }
    }
}
