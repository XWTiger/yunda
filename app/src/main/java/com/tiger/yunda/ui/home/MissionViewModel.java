package com.tiger.yunda.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MissionViewModel extends ViewModel {

    private final MutableLiveData<MissionResult> resultMutableLiveData;

    public MissionViewModel() {
        resultMutableLiveData = new MutableLiveData<>();
        List<Mission> data = new ArrayList<>();
        data.add(new Mission("1", "123", 1, "测试用户", 1, "车头", "AC12344", "AC12344-A、AC12344-B", "1200", 2,"已下发", "","","","","","","", null));
        data.add(new Mission("1", "123", 1, "测试用户", 1, "车头", "AC12344", "AC12344-A、AC12344-B", "1200", 2,"已下发", "","","","","","","", null));
        MissionResult missionResult = new MissionResult(data.size(), data);
        resultMutableLiveData.setValue(missionResult);
    }

    public LiveData<MissionResult> getData() {
        return resultMutableLiveData;
    }
}