package com.tiger.yunda.ui.home;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.DeliverTask;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.service.MissionService;
import com.tiger.yunda.ui.login.LoginActivity;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.JsonUtil;

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

    private Context context;

    private  int page;
    private  int pageSize;
    private  String startTime;
    private String endTime;

    private int count = 0;

    private int masterCount = 0;
    private  int masterPage ;
    private  int masterPageSize;

    public MissionViewModel() {
        resultMutableLiveData = new MutableLiveData<>();

    }

    public LiveData<MissionResult> getData(int page, int pageSize, String startTime, String endTime, Boolean masterMission) {
        if (Objects.isNull(missionService)) {
            missionService = MainActivity.retrofitClient.create(MissionService.class);
        }


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
        if (!masterMission) { //巡检员的子任务
            this.page = page;
            this.pageSize = pageSize;

            Call<MissionResult> result =  missionService.query(body);
            result.enqueue(new Callback<MissionResult>() {
                @Override
                public void onResponse(Call<MissionResult> call, Response<MissionResult> response) {
                    if (response.code() == MissionService.HTTP_OK) {
                        count = response.body().getCount();
                        MissionResult missionResult = response.body();
                      /*  missionResult.getData().add(Mission.builder()
                                        .id("788edbf0-f511-4f16-a8d7-201f5823ad34")
                                        .taskId("28992644-eec7-4e84-9b98-d54f1b6e8e23")
                                .inspectorId(1).positionId(1).positionName("L1-A").inspectionUnit("0412-A").state(5).faultState(3).appealState(0).build());*/
                        resultMutableLiveData.setValue(missionResult);
                    } else {

                        try {
                            resultMutableLiveData.setValue(new MissionResult(0, new ArrayList<>()));
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
        } else { //主任务
            this.masterPage = page;
            this.masterPageSize = pageSize;


            if (Objects.nonNull(MainActivity.loggedInUser)) {
                body.put("deptId", MainActivity.loggedInUser.getDeptId());
            }
            //默认查待分发的任务
            body.put("state", 0);
            Call<PageResult<DeliverTask>> resultCall = missionService.queryMasterMission(body);
            resultCall.enqueue(new Callback<PageResult<DeliverTask>>() {
                @Override
                public void onResponse(Call<PageResult<DeliverTask>> call, Response<PageResult<DeliverTask>> response) {
                    MissionResult missionResult =  new MissionResult(0, new ArrayList<>());
                    if (response.code() == MissionService.HTTP_OK) {
                        PageResult<DeliverTask> result  = response.body();

                        if (result.getCount() > 0) {
                            List<Mission> data = new ArrayList<>();
                            result.getData().forEach(deliverTask -> {
                                data.add(deliverTask.allInOne());
                            });
                            missionResult.setData(data);
                            missionResult.setCount(result.getCount());
                            masterCount = result.getCount();

                        }
                        resultMutableLiveData.setValue(missionResult);
                    } else {

                        try {
                            resultMutableLiveData.setValue(new MissionResult(0, new ArrayList<>()));
                            Log.e("xiaweihu", "查询主任务列表失败: ===========>" + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<PageResult<DeliverTask>> call, Throwable throwable) {

                    Log.e("xiaweihu", "onFailure: ============", throwable );
                }
            });
        }

        return resultMutableLiveData;
    }

    public void addOne(Boolean masterMission) {

        if (!masterMission) {
            if ( (page)  * pageSize < count) {
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
        } else {
            if ( (masterPage)  * masterPageSize < masterCount) {
                Map<String, Object> body = new HashMap<>();
                body.put("page", masterPage + 1);
                body.put("limit", masterPageSize);
                Map<String, String> sorts = new HashMap<>();
                sorts.put("filed", "createTime");
                sorts.put("type", "1");
                body.put("sorts", sorts);
                if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                    body.put("startTime", startTime);
                    body.put("endTime", endTime);
                }
                if (Objects.nonNull(MainActivity.loggedInUser)) {
                    body.put("deptId", MainActivity.loggedInUser.getDeptId());
                }

                Call<PageResult<DeliverTask>> resultCall = missionService.queryMasterMission(body);
                resultCall.enqueue(new Callback<PageResult<DeliverTask>>() {
                    @Override
                    public void onResponse(Call<PageResult<DeliverTask>> call, Response<PageResult<DeliverTask>> response) {
                        MissionResult missionResult =  new MissionResult(0, new ArrayList<>());
                        if (response.code() == MissionService.HTTP_OK) {
                            PageResult<DeliverTask> result  = response.body();
                            masterCount = result.getCount();
                            masterPage += 1;
                            if (result.getCount() > 0) {
                                List<Mission> data = new ArrayList<>();
                                result.getData().forEach(deliverTask -> {
                                    data.add(deliverTask.allInOne());
                                });
                                missionResult.setData(data);
                                missionResult.setCount(result.getCount());
                                masterCount = result.getCount();
                            }

                        } else {

                            try {
                                resultMutableLiveData.setValue(new MissionResult(0, new ArrayList<>()));
                                String errStr = response.errorBody().string();
                                ErrorResult errorResult = JsonUtil.getObject(errStr, context);
                                Log.e("xiaweihu", "查询主任务列表失败: ===========>" + errStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        resultMutableLiveData.setValue(missionResult);
                    }

                    @Override
                    public void onFailure(Call<PageResult<DeliverTask>> call, Throwable throwable) {

                    }
                });
            }
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
                    Response<ResponseBody> response = responseBodyCall.execute();
                    if (response.isSuccessful()) {
                        result.set(true);
                    } else {
                        String errStr = response.errorBody().string();
                        Log.e("xiaweihu", "查询主任务列表失败: ===========>" + errStr);
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


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void updateService() {
        missionService = MainActivity.retrofitClient.create(MissionService.class);
    }
}