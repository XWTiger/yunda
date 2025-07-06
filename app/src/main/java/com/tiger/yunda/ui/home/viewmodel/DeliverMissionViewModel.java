package com.tiger.yunda.ui.home.viewmodel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.DeliverMissionDTO;
import com.tiger.yunda.data.model.DeliverMssion;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.SaveMission;
import com.tiger.yunda.data.model.Train;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.service.MissionService;
import com.tiger.yunda.ui.home.MissionResult;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliverMissionViewModel extends ViewModel {

    private MutableLiveData<List<DeliverMssion>> deliverMissions = new MutableLiveData<>();

    private MutableLiveData<List<User>> users = new MutableLiveData<>();

    private MutableLiveData<Map<String, Object>> mapMutableLiveData = new MutableLiveData<>();

    private MissionService missionService;

    private Context context;

    private MutableLiveData<List<Train>> positions = new MutableLiveData<>();


    public DeliverMissionViewModel(Context context) {
        missionService = MainActivity.retrofitClient.create(MissionService.class);
        this.context = context;
    }
    /**
     * {
     *   "id": "string", 主任务id
     *   "subtasks": [
     *     {
     *       "id": "string", 子任务id
     *       "inspectorId": 0, 巡检人id
     *       "positionId": 0 巡检列位
     *     }
     *   ]
     * }
     * @param body
     * @return
     */
    public LiveData<Map<String, Object>> updateSubtaskMission(SaveMission body) {

        Call<ResponseBody> result = missionService.updateSubtaskMission(body);
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == MissionService.HTTP_OK) {
                    mapMutableLiveData.setValue(new HashMap<>());
                } else {
                    try {
                        String errStr = response.errorBody().string();
                        ErrorResult errorResult = JsonUtil.getObject(errStr, context);
                        Log.e("xiaweihu", "查询任务: ===========>" + errStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                if (StringUtils.isNotBlank(throwable.getMessage())) {
                    Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }


        });
        return mapMutableLiveData;
    }

    public LiveData<List<DeliverMssion>> queryUpdateSubtaskMission(String masterTaskId) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", 1);
        params.put("limit", 20);
        params.put("id", masterTaskId);

        List<DeliverMssion> list = new ArrayList<>();
        Call<MissionResult> resultCall =  missionService.queryUpdateSubtaskPage(params);
        resultCall.enqueue(new Callback<MissionResult>() {
            @Override
            public void onResponse(Call<MissionResult> call, Response<MissionResult> response) {
                if (response.code() == MissionService.HTTP_OK) {
                    MissionResult missionResult = response.body();
                    if (missionResult.getCount() > 0) {
                        missionResult.getData().forEach(mission -> {
                            list.add(JsonUtil.covertToDeliverMssion(mission));
                        });
                    }
                    deliverMissions.setValue(list);
                } else {
                    try {
                        String errStr = response.errorBody().string();
                        ErrorResult errorResult = JsonUtil.getObject(errStr, context);
                        Log.e("xiaweihu", "查询任务: ===========>" + errStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MissionResult> call, Throwable throwable) {
                if (StringUtils.isNotBlank(throwable.getMessage())) {
                    Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return deliverMissions;
    }


    /**
     * type 0 正常查询 1  调用查看任务
     * @param taskId
     * @param type
     * @return
     */
    public LiveData<List<DeliverMssion>> getDatas(String taskId, int type) {
        List<DeliverMssion> list = new ArrayList<>();
       /* if (type == 0) {
            list.add(new DeliverMssion(1, "tiger",1, "二段", "A单元","5h"));
            list.add(new DeliverMssion(1, "tiger",1, "二段", "B单元","5h"));
            deliverMissions.setValue(list);
        } else {*/
            Call<List<DeliverMissionDTO>> result = missionService.queryCreatedMission(taskId);
            result.enqueue(new Callback<List<DeliverMissionDTO>>() {
                @Override
                public void onResponse(Call<List<DeliverMissionDTO>> call, Response<List<DeliverMissionDTO>> response) {
                    if (response.code() == MissionService.HTTP_OK) {
                        List<DeliverMissionDTO> resultList = response.body();
                        if (!CollectionUtil.isEmpty(resultList)) {
                            resultList.forEach(deliverMissionDTO -> {
                                list.add(deliverMissionDTO.covertToDeliverMission());
                            });
                        }
                        deliverMissions.setValue(list);
                    } else {
                        try {
                            String errStr = response.errorBody().string();
                            ErrorResult errorResult = JsonUtil.getObject(errStr, context);
                            Log.e("xiaweihu", "查询任务: ===========>" + errStr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<DeliverMissionDTO>> call, Throwable throwable) {
                    if (StringUtils.isNotBlank(throwable.getMessage())) {
                        Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
      /*  }*/
        return deliverMissions;
    }

    public MutableLiveData<List<User>> getUsers(String deptId) {

        Call<List<User>> call = missionService.queryUsers(deptId);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> list = new ArrayList<>();
                if (response.code() == MissionService.HTTP_OK) {
                     list = response.body();
                } else {
                    try {
                        Log.e("xiaweihu", "通过部门查询用户: ===========>" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                users.setValue(list);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                if (StringUtils.isNotBlank(throwable.getMessage())) {
                    Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

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
}
