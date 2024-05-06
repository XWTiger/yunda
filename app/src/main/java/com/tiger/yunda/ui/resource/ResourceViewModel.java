package com.tiger.yunda.ui.resource;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.OperationResource;
import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.service.ResourceService;
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

public class ResourceViewModel extends ViewModel {

    private ResourceService resourceService;
    private final MutableLiveData<List<OperationResource>> mText;

    private Context context;

    public ResourceViewModel(Context context) {
        mText = new MutableLiveData<>();
        resourceService = MainActivity.retrofitClient.create(ResourceService.class);
        this.context = context;
    }

    public LiveData<List<OperationResource>> queryData(Integer pageNo, Integer pageSize , String search) {
        List<OperationResource> data = new ArrayList<>();
        /*data.add(OperationResource.builder()
                        .id("1")
                        .fileName("轮对擦伤检验规范标准")
                .fileSize(100)
                .fileType(1)
                .uploadUserId(1)
                .build());
        data.add(OperationResource.builder()
                .id("2")
                .fileName("PIS系统巡检指南")
                .fileSize(100)
                .fileType(1)
                .uploadUserId(1)
                .build());
        data.add(OperationResource.builder()
                .id("3")
                .fileName("内饰巡检标准")
                .fileSize(100)
                .fileType(1)
                .uploadUserId(1)
                .build());*/
        Map<String, Object> body = new HashMap<>();
        body.put("page", pageNo);
        body.put("limit", pageSize);//createTime
        if (StringUtils.isNotBlank(search)) {
            body.put("search", search);
        } else {
            body.put("search", "");
        }
        Call<PageResult<OperationResource>> result = resourceService.queryResource(body);
        result.enqueue(new Callback<PageResult<OperationResource>>() {
            @Override
            public void onResponse(Call<PageResult<OperationResource>> call, Response<PageResult<OperationResource>> response) {
                if (response.isSuccessful()) {
                    PageResult<OperationResource> resourceList = response.body();
                    mText.setValue(resourceList.getData());
                } else {
                    String errStr = null;
                    try {
                        errStr = response.errorBody().string();
                        ErrorResult errorResult = JsonUtil.getObject(errStr, context);
                        Log.e("xiaweihu", "查询资料列表: ===========>" + errStr);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<PageResult<OperationResource>> call, Throwable throwable) {
                Log.e("xiaweihu", "resource list query: =========", throwable);
            }
        });

        return mText;

    }

}