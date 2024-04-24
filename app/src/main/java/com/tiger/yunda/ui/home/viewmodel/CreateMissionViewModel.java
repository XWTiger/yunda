package com.tiger.yunda.ui.home.viewmodel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.CreateMission;
import com.tiger.yunda.data.model.Train;
import com.tiger.yunda.service.MissionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

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
                .nameObserver(new ObservableField<String>())
                .trainNoStr(new ObservableField<String>(""))
                .build();

        creation.setValue(createMission);
        return creation;
    }

    public LiveData<String> createMission(CreateMission createMission) {
        MutableLiveData<String> data = new MutableLiveData<>();
        Call<Map<String, String>> mapCall = missionService.createMission(createMission);

        mapCall.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.code() == 200) {
                    data.setValue(response.body().get("id"));
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable throwable) {

            }
        });

        return data;
    }


}
