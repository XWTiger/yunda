package com.tiger.yunda.data.model;

import androidx.databinding.ObservableField;

import com.tiger.yunda.utils.CollectionUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private String deptIdStr;
    private String leaderIdStr;

    private Integer deptId;
    private Integer leaderId;
    private String planStartTime;
    private String planEndTime;
    private List<String> trainNo;
    private ObservableField<String> trainNoStr;
    private List<TrainPlace> trains;


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

    public void fillContent() {
        deptId = Integer.valueOf(deptIdStr);
        leaderId = Integer.valueOf(leaderIdStr);
    }


    public void addOneTrain(String name) {
        if (StringUtils.isBlank(trainNoStr.get())) {
            trainNoStr.set(name);
        } else {
            trainNoStr.set(trainNoStr.get() + "," + name);
        }
    }

    public void addTrain(TrainPlace trainPlace) {
        if (CollectionUtil.isEmpty(trains)) {
            trains = new ArrayList<>();
        }
        if (Objects.nonNull(trainPlace.getIndex()) && trainPlace.getIndex() > 0) {
            if (StringUtils.isNotBlank(trainPlace.getNo())) {
                trains.get(trainPlace.getIndex() - 1).setNo(trainPlace.getNo());
            }

            if (Objects.nonNull(trainPlace.getPositionId())) {
                trains.get(trainPlace.getIndex() - 1).setPositionId(trainPlace.getPositionId());
            }
        } else {
            trains.add(trainPlace);
        }
    }
    public void subTrain(int position) {
        if (!CollectionUtil.isEmpty(trains)) {
            trains.remove(position);
        }
    }

    public void subOneTrain() {
        String content = trainNoStr.get();
        if (content.length() > 0 && !content.contains(",")) {
            trainNoStr.set(null);
        }
        if (content.length() > 0 && content.contains(",")) {
            int start = content.lastIndexOf(",");
            content = content.substring(0,start);
            trainNoStr.set(content);
        }
    }
    public void clearAll() {
        trains = new ArrayList<>();
    }
}
