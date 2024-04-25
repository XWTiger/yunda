package com.tiger.yunda.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.DeliverMissionDTO;
import com.tiger.yunda.data.model.DeliverMssion;
import com.tiger.yunda.data.model.SaveMission;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.service.MissionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliverMissionViewModel extends ViewModel {

    private MutableLiveData<List<DeliverMssion>> deliverMissions = new MutableLiveData<>();

    private MutableLiveData<List<User>> users = new MutableLiveData<>();

    private MissionService missionService;


    public DeliverMissionViewModel() {
            missionService = MainActivity.retrofitClient.create(MissionService.class);
    }

    /**
     * type 0 正常查询 1  调用查看任务
     * @param taskId
     * @param type
     * @return
     */
    public LiveData<List<DeliverMssion>> getDatas(String taskId, int type) {
        List<DeliverMssion> list = new ArrayList<>();
        if (type == 0) {
            list.add(new DeliverMssion(1, "tiger",1, "二段", "A单元","5h"));
            list.add(new DeliverMssion(1, "tiger",1, "二段", "B单元","5h"));
            deliverMissions.setValue(list);
        } else {
            Call<DeliverMissionDTO> result = missionService.queryCreatedMission(taskId);
            result.enqueue(new Callback<DeliverMissionDTO>() {
                @Override
                public void onResponse(Call<DeliverMissionDTO> call, Response<DeliverMissionDTO> response) {
                    if (response.code() == MissionService.HTTP_OK) {

                    }
                }

                @Override
                public void onFailure(Call<DeliverMissionDTO> call, Throwable throwable) {

                }
            });
        }
        return deliverMissions;
    }

    public MutableLiveData<List<User>> getUsers(String deptId) {

        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "张三"));
        userList.add(new User(2, "王力宏"));
        users.setValue(userList);
        return users;
    }

    public boolean saveSubMissions(SaveMission saveMission) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final int[] resultCode = {0};
        MainActivity.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call = missionService.deliverMission(saveMission);
                try {
                    int code = call.execute().code();
                    if (code == MissionService.HTTP_OK) {
                        resultCode[0] = 1;
                    }
                    countDownLatch.countDown();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (resultCode[0] == 1) {
            return true;
        } else {
            return false;
        }
    }
}
