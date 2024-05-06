package com.tiger.yunda.ui.breakdown;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.service.BreakDownService;
import com.tiger.yunda.service.MissionService;
import com.tiger.yunda.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreakDownViewModel extends ViewModel {

    private BreakDownService breakDownService;
    private MutableLiveData<List<BreakRecord>> breakrecords = new MutableLiveData<>();

    private MutableLiveData<List<User>> users = new MutableLiveData<>();

    private int count;

    private int pageNo;

    private int pageSize;

    private Context context;

    public BreakDownViewModel(Context context) {
        breakDownService = MainActivity.retrofitClient.create(BreakDownService.class);
        this.context = context;
    }


    public LiveData<List<BreakRecord>> getBreakRecords(Integer page, Integer limit, String startTime, String endTime, Integer deptId) {

        Map<String, Object> body = new HashMap<>();
        body.put("page", page);
        body.put("limit", limit);//createTime
        List<Map> sortList = new ArrayList<>();

        Map<String, String> sorts = new HashMap<>();
        sorts.put("filed", "ReportTime");
        sorts.put("type", "1");
        sortList.add(sorts);
        body.put("sorts", sortList);
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            body.put("startTime", startTime);
            body.put("endTime", endTime);
        }
        body.put("deptId", deptId);

        List<BreakRecord> breakRecordList = new ArrayList<>();
        Call<PageResult<BreakRecord>> resultCall = breakDownService.queryBreakRecordList(body);
        resultCall.enqueue(new Callback<PageResult<BreakRecord>>() {
            @Override
            public void onResponse(Call<PageResult<BreakRecord>> call, Response<PageResult<BreakRecord>> response) {
                if (response.isSuccessful()) {
                    PageResult<BreakRecord> result = response.body();
                    breakrecords.setValue(result.getData());
                    count = result.getCount();
                    pageNo = page;
                    pageSize = limit;
                } else {
                    try {
                        String errStr = response.errorBody().string();
                        ErrorResult errorResult = JsonUtil.getObject(errStr, context);
                        breakrecords.setValue(breakRecordList);
                        Log.e("xiaweihu", "查询故障列表失败: ===========>" + errStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PageResult<BreakRecord>> call, Throwable throwable) {

            }
        });

        return breakrecords;
    }


    public BreakRecord getBreakRecordById(String id) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<BreakRecord> breakRecord = new AtomicReference<>();
        Call<BreakRecord> breakRecordCall = breakDownService.queryByBreakId(id);
        MainActivity.threadPoolExecutor.execute(() -> {
            try {
                Response<BreakRecord> response = breakRecordCall.execute();
                if (response.isSuccessful()) {
                    breakRecord.set(response.body());
                } else {
                    String errStr = response.errorBody().string();
                    ErrorResult errorResult = JsonUtil.getObject(errStr, context);
                    Log.e("xiaweihu", "查询故障详情失败: ===========>" + errStr);
                }
                countDownLatch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            countDownLatch.await();
            return breakRecord.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MutableLiveData<List<User>> getUsers(String deptId) {

        Call<List<User>> call = breakDownService.queryUsers(deptId);
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

            }
        });

        return users;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
