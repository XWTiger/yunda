package com.tiger.yunda.ui.log;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.data.model.Task;
import com.tiger.yunda.data.model.WorkLog;
import com.tiger.yunda.service.LoginService;
import com.tiger.yunda.service.WorkLogService;
import com.tiger.yunda.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogViewModel extends ViewModel {
    private MutableLiveData<List<WorkLog>> logs = new MutableLiveData<>();
    private WorkLogService workLogService;

    private Context context;

    public LogViewModel() {
        workLogService = MainActivity.retrofitClient.create(WorkLogService.class);
    }

    public LiveData<List<WorkLog>> getLogs(Integer pageNo, Integer pageSize, Integer deptId, String startTime, String endTime) {
        List<WorkLog> workLogList = new ArrayList<>();
        /*workLogList.add(WorkLog.builder()
                        .id("1")
                        .inspectionUnit("A374CP-A")
                        .task(new Task("夜班巡检"))
                        .score("5")
                .inspectorId(1).positionId(1).duration(23).state(4).faultStateText("无故障").faultState(3).build());
        workLogList.add(WorkLog.builder()
                        .id("2")
                .inspectionUnit("A374CP-B")
                .task(new Task("白班巡检"))
                        .score("9.7")
                .inspectorId(1).positionId(1).duration(23).state(4).faultStateText("无故障").faultState(3).build());*/

        Map<String, Object> body = new HashMap<>();
        body.put("page", pageNo);
        body.put("limit", pageSize);
        Map<String, String> sorts = new HashMap<>();
        sorts.put("filed", "createTime");
        sorts.put("type", "1");
        body.put("sorts", sorts);
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            body.put("startTime", startTime);
            body.put("endTime", endTime);
        }
        body.put("deptId", deptId);
        body.put("inspectorId", MainActivity.loggedInUser.getUserId());
        Call<PageResult<WorkLog>> resultCall = workLogService.queryByPage(body);
        resultCall.enqueue(new Callback<PageResult<WorkLog>>() {
            @Override
            public void onResponse(Call<PageResult<WorkLog>> call, Response<PageResult<WorkLog>> response) {

                if (response.isSuccessful()) {
                    PageResult<WorkLog> result = response.body();
                    logs.setValue(result.getData());
                } else {
                    String errStr = null;
                    try {
                        errStr = response.errorBody().string();
                        ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                        Log.e("xiaweihu", "查询子任务详情: ===========>" + errStr);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<PageResult<WorkLog>> call, Throwable throwable) {

            }
        });

        logs.setValue(workLogList);
        return logs;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
