package com.tiger.yunda.ui.log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.data.model.Task;
import com.tiger.yunda.data.model.WorkLog;

import java.util.ArrayList;
import java.util.List;

public class LogViewModel extends ViewModel {
    private MutableLiveData<List<WorkLog>> logs = new MutableLiveData<>();

    public LiveData<List<WorkLog>> getLogs(Integer pageNo, Integer pageSize, Integer deptId, String startTime, String endTime) {
        List<WorkLog> workLogList = new ArrayList<>();
        workLogList.add(WorkLog.builder()
                        .id("1")
                        .inspectionUnit("A374CP-A")
                        .task(new Task("夜班巡检"))
                        .score("5")
                .inspectorId(1).positionId(1).duration(23).state(4).faultStateText("无故障").faultState(3).build());
        workLogList.add(WorkLog.builder()
                        .id("2")
                .inspectionUnit("A374CP-B")
                .task(new Task("白班巡检"))
                        .score("9.7")
                .inspectorId(1).positionId(1).duration(23).state(4).faultStateText("无故障").faultState(3).build());
        logs.setValue(workLogList);
        return logs;
    }
}
