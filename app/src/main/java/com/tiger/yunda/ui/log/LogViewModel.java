package com.tiger.yunda.ui.log;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import java.util.Objects;

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

        Map<String, Object> body = new HashMap<>();
        body.put("page", pageNo);
        body.put("limit", pageSize);
        List<Map> sortsList = new ArrayList<>();
        Map<String, Object> sorts = new HashMap<>();
        sorts.put("filed", "IssuTime");
        sorts.put("type", 1);
        sortsList.add(sorts);
        body.put("sorts", sortsList);
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            body.put("startTime", startTime);
            body.put("endTime", endTime);
        }
        if (deptId > 0) {
            body.put("deptId", deptId);
        } else {
            if (Objects.nonNull(MainActivity.loggedInUser)) {
                body.put("deptId", Integer.valueOf(MainActivity.loggedInUser.getDeptId()));
            }
        }
        body.put("faultState", 0);
        if (Objects.nonNull(MainActivity.loggedInUser)) {
            body.put("inspectorId", Integer.valueOf(MainActivity.loggedInUser.getUserId()));
        }
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
                Log.e("xiaweihu", "work detail failed=========: ",  throwable);
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
