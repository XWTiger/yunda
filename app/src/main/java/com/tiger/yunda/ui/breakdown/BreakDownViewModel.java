package com.tiger.yunda.ui.breakdown;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.data.model.BreakRecord;

import java.util.ArrayList;
import java.util.List;

public class BreakDownViewModel extends ViewModel {

    private MutableLiveData<List<BreakRecord>> breakrecords = new MutableLiveData<>();

    public LiveData<List<BreakRecord>> getBreakRecords(Integer pageNo, Integer pageSize, String startTime, String endTime, Integer deptId) {

        List<BreakRecord> breakRecordList = new ArrayList<>();
        BreakRecord record = BreakRecord.builder()
                .id("1")
                .type(1)
                .reportUserId(1)
                .state(1)
                .trainNo("0887A")
                .typeText("PIS")
                .stateText("未处理")
                .build();
        breakRecordList.add(record);
        breakRecordList.add(BreakRecord.builder()
                .id("1")
                .type(1)
                .reportUserId(1)
                .state(2)
                .trainNo("8888")
                .stateText("已处理")
                .typeText("AIS")
                .build());

        breakrecords.setValue(breakRecordList);
        return breakrecords;
    }


}
