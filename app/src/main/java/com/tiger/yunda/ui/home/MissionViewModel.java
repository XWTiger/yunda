package com.tiger.yunda.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.service.MissionService;
import com.tiger.yunda.ui.login.LoginActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionViewModel extends ViewModel {

    private final MutableLiveData<MissionResult> resultMutableLiveData;

    private MissionService missionService;

    private  int page;
    private  int pageSize;
    private  String startTime;
    private String endTime;

    private int count = 0;
    public MissionViewModel() {
        resultMutableLiveData = new MutableLiveData<>();
      /*  List<Mission> data = new ArrayList<>();
        Mission mission = new Mission("1", "", 1, "测试用户", 1, "车头", "AC12344", "AC12344-A、AC12344-B", "1200", 2, "已下发", "", "", "", "", "", "", "", null);

        List<TrainLocations> trainLocations = new ArrayList<>();
        trainLocations.add(new TrainLocations("1","1", "2024-01-01 00:00:00", "2024-05-01 00:00:00", 1, "车头", "", 0, 0, "初始"));
        trainLocations.add(new TrainLocations("2","2", "2024-01-01 00:00:00", "2024-05-01 00:00:00", 2, "车顶", "", 0, 0, "初始"));
        data.add(mission);
        mission.setTrainLocations(trainLocations);
        data.add(new Mission("", "123", 1, "测试用户", 1, "车头", "AC12344", "AC12344-A、AC12344-B", "1200", 2,"已下发", "","","","","","","", null));
        MissionResult missionResult = new MissionResult(data.size(), data);
        resultMutableLiveData.setValue(missionResult);*/

    }

    public LiveData<MissionResult> getData(int page, int pageSize, String startTime, String endTime) {
        if (Objects.isNull(missionService)) {
            missionService = MainActivity.retrofitClient.create(MissionService.class);
        }
        this.page = page;
        this.pageSize = pageSize;

        Map<String, Object> body = new HashMap<>();
        body.put("page", page);
        body.put("limit", pageSize);//createTime
        List<Map> sortList = new ArrayList<>();

        Map<String, String> sorts = new HashMap<>();
        sorts.put("filed", "createTime");
        sorts.put("type", "1");
        sortList.add(sorts);
        body.put("sorts", sortList);
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            body.put("startTime", startTime);
            body.put("endTime", endTime);
        }
        Call<MissionResult> result =  missionService.query(body);
        result.enqueue(new Callback<MissionResult>() {
            @Override
            public void onResponse(Call<MissionResult> call, Response<MissionResult> response) {
                if (response.code() == MissionService.HTTP_OK) {
                    count = response.body().getCount();
                    resultMutableLiveData.setValue(response.body());
                } else {

                    try {
                        Log.e("xiaweihu", "查询任务列表: ===========>" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MissionResult> call, Throwable throwable) {
                throwable.printStackTrace();
                Log.e("xiaweihu", "onFailure: ===========", throwable);

            }
        });
        return resultMutableLiveData;
    }

    public void addOne() {

        if ( (page +1)  * pageSize < count) {
            Map<String, Object> body = new HashMap<>();
            body.put("page", page + 1);
            body.put("limit", pageSize);
            Map<String, String> sorts = new HashMap<>();
            sorts.put("filed", "createTime");
            sorts.put("type", "1");
            body.put("sorts", sorts);
            if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                body.put("startTime", startTime);
                body.put("endTime", endTime);
            }
            Call<MissionResult> result =  missionService.query(body);
            result.enqueue(new Callback<MissionResult>() {
                @Override
                public void onResponse(Call<MissionResult> call, Response<MissionResult> response) {
                    count = response.body().getCount();
                    page = page + 1;
                    List<Mission> data = resultMutableLiveData.getValue().getData();
                    data.addAll(response.body().getData());
                    MissionResult missionResult = new MissionResult(data.size(), data);
                    resultMutableLiveData.setValue(missionResult);
                }

                @Override
                public void onFailure(Call<MissionResult> call, Throwable throwable) {

                }
            });
        }
    }

    public Boolean acceptMission(String subtaskId) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Call<ResponseBody> responseBodyCall = missionService.acceptMission(subtaskId);
        AtomicBoolean result = new AtomicBoolean();
        MainActivity.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int code = responseBodyCall.execute().code();
                    if (MissionService.HTTP_OK == code) {
                        result.set(true);
                    }
                    countDownLatch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
           e.printStackTrace();
        }

        if (result.get()) {
            return true;
        }
        return false;
    }

    public LiveData<Boolean> acceptMissions(List<String>  subtaskIds) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        subtaskIds.forEach(subtaskId -> {
            Call<ResponseBody> responseBodyCall = missionService.acceptMission(subtaskId);
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == MissionService.HTTP_OK) {
                        atomicInteger.getAndIncrement();
                        if (atomicInteger.get() == subtaskIds.size()) {
                            result.setValue(true);
                        }
                    } else {
                        try {
                            Log.e("xiaweihu", "批量接受任务: ===========>" + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    throwable.printStackTrace();
                    Log.e("xiaweihu", "批量接受任务失败: ===========>", throwable);
                    result.setValue(false);
                }
            });
        });

        return result;
    }



}