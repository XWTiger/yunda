package com.tiger.yunda.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.CreateMission;
import com.tiger.yunda.data.model.Train;
import com.tiger.yunda.service.MissionService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMissionViewModel extends ViewModel {

    private MutableLiveData<List<Train>> trains = new MutableLiveData<>();
    private MutableLiveData<CreateMission> creation = new MutableLiveData<>();

    private MissionService missionService;

    public CreateMissionViewModel() {
        missionService = MainActivity.retrofitClient.create(MissionService.class);
    }

    public LiveData<List<Train>> getTrains() {
        Call<List<Train>> listCall = missionService.queryTrains();
        listCall.enqueue(new Callback<List<Train>>() {
            @Override
            public void onResponse(Call<List<Train>> call, Response<List<Train>> response) {
                trains.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Train>> call, Throwable throwable) {

            }
        });
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
