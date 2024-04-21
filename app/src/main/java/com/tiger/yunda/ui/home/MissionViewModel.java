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
        Mission mission = new Mission("1", "", 1, "测试用户", 1, "车头", "AC12344", "AC12344-A、AC12344-B", "1200", 2, "已下发", "", "", "", "", "", "", "", null);

        List<TrainLocations> trainLocations = new ArrayList<>();
        trainLocations.add(new TrainLocations("1","1", "2024-01-01 00:00:00", "2024-05-01 00:00:00", 1, "车头", "", 0, 0, "初始"));
        trainLocations.add(new TrainLocations("2","2", "2024-01-01 00:00:00", "2024-05-01 00:00:00", 2, "车顶", "", 0, 0, "初始"));
        data.add(mission);
        mission.setTrainLocations(trainLocations);
        data.add(new Mission("", "123", 1, "测试用户", 1, "车头", "AC12344", "AC12344-A、AC12344-B", "1200", 2,"已下发", "","","","","","","", null));
        MissionResult missionResult = new MissionResult(data.size(), data);
        resultMutableLiveData.setValue(missionResult);
    }

    public LiveData<MissionResult> getData() {
        return resultMutableLiveData;
    }

    public void addOne() {
        List<Mission> data = resultMutableLiveData.getValue().getData();
                data.add(new Mission("", "123", 1, "tiger", 1, "车头", "AC12344", "AC33333-A、AC444444-B", "1200", 2, "已下发", "", "", "", "", "", "", "", null));
        MissionResult missionResult = new MissionResult(data.size(), data);
        resultMutableLiveData.setValue(missionResult);
    }
}