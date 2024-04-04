package com.tiger.yunda.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.data.model.AcceptMission;

import java.util.ArrayList;
import java.util.List;

public class AcceptMissionViewModel  extends ViewModel {

    private MutableLiveData<List<AcceptMission>> list;


    public AcceptMissionViewModel() {
        List<AcceptMission> ams = new ArrayList<>();
        ams.add(new AcceptMission("0812A", "王力宏"));
        ams.add(new AcceptMission("0812B", "王力宏"));
        ams.add(new AcceptMission("0813A", "tiger"));
        ams.add(new AcceptMission("0813B", "tiger"));
        list.setValue(ams);
    }

    public LiveData<List<AcceptMission>> getData() {
        return list;
    }




}
