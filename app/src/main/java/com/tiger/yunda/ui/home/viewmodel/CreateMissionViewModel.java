package com.tiger.yunda.ui.home.viewmodel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.CreateMission;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.Train;
import com.tiger.yunda.service.MissionService;
import com.tiger.yunda.utils.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

public class CreateMissionViewModel extends ViewModel {

    private MutableLiveData<List<Train>> trains = new MutableLiveData<>();
    private MutableLiveData<CreateMission> creation = new MutableLiveData<>();

    private MutableLiveData<List<Train>> positions = new MutableLiveData<>();
    private MissionService missionService;

    private MutableLiveData<Integer> postion = new MutableLiveData<>();

    private Context context;

    public CreateMissionViewModel(Context context) {
        missionService = MainActivity.retrofitClient.create(MissionService.class);
        this.context = context;
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

    public LiveData<List<Train>> getPositions() {
        Call<List<Train>> listCall = missionService.queryPositions();
        listCall.enqueue(new Callback<List<Train>>() {
            @Override
            public void onResponse(Call<List<Train>> call, Response<List<Train>> response) {
                positions.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Train>> call, Throwable throwable) {

            }
        });
        return positions;
    }

    public LiveData<Integer> getPositionByTrainNo(String trainNo) {
        Call<ResponseBody> responseBodyCall = missionService.getPositionByTrainNo(trainNo);
           responseBodyCall.enqueue(new Callback<ResponseBody>() {
               @Override
               public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                   try {
                       if (Objects.isNull(response.body())) {
                           postion.setValue(0);
                       } else {
                           postion.setValue(Integer.valueOf(response.body().string()));
                       }
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
               }

               @Override
               public void onFailure(Call<ResponseBody> call, Throwable throwable) {

               }
           });
        return postion;

    }

    public LiveData<CreateMission> getCreation() {
        if (Objects.isNull(MainActivity.loggedInUser)) {
            Toast.makeText(context, "未登录，请先登录", Toast.LENGTH_SHORT).show();
            return creation;
        }
        CreateMission createMission = CreateMission.builder()
                .deptIdStr(MainActivity.loggedInUser.getDeptId())
                .leaderIdStr(MainActivity.loggedInUser.getUserId())
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
                } else {
                    try {

                        String errStr = response.errorBody().string();
                        ErrorResult errorResult = JsonUtil.getObject(errStr, context);
                        Log.e("xiaweihu", "创建任务失败: ===========>" + errStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable throwable) {

            }
        });

        return data;
    }


}
