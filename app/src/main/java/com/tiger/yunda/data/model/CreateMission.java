package com.tiger.yunda.data.model;

import androidx.databinding.ObservableField;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
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
public class CreateMission  implements Serializable {

    private ObservableField<String> nameObserver ;
    private String name;
    private String deptId;
    private String leaderId;
    private String planStartTime;
    private String planEndTime;
    private List<String> trainNo;
    private ObservableField<String> trainNoStr;


    public void covertTrainNoStr() {
        if (StringUtils.isNotBlank(trainNoStr.get())) {
            String[] arr = trainNoStr.get().split(",");
            List<String> buffer = new ArrayList<>();
            for (int i = 0; i < arr.length; i++) {
                buffer.add(arr[i]);
            }
            trainNo = buffer;
        }
        //name = nameObserver.get();
    }



    public void addOneTrain(String name) {
        if (StringUtils.isBlank(trainNoStr.get())) {
            trainNoStr.set(name);
        } else {
            trainNoStr.set(trainNoStr.get() + "," + name);
        }
    }
}
