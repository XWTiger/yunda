package com.tiger.yunda.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.CreateMission;
import com.tiger.yunda.data.model.Train;

import java.util.ArrayList;
import java.util.List;

public class CreateMissionViewModel extends ViewModel {

    private MutableLiveData<List<Train>> trains = new MutableLiveData<>();
    private MutableLiveData<CreateMission> creation = new MutableLiveData<>();


    public LiveData<List<Train>> getTrains() {
        trains.setValue(new ArrayList<>());
        trains.getValue().add(Train.builder()
                        .text("0832")
                        .value(1)
                .build())
        ;
        trains.getValue().add(Train.builder()
                .text("0833")
                .value(2)
                .build())
        ;
        return trains;
    }

    public LiveData<CreateMission> getCreation() {


        CreateMission createMission = CreateMission.builder()
                .deptId(MainActivity.loggedInUser.getDeptId())
                .leaderId(MainActivity.loggedInUser.getUserId())
                .build();

        creation.setValue(createMission);
        return creation;
    }


}
