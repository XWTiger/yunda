package com.tiger.yunda.ui.resource;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.data.model.OperationResource;

import java.util.ArrayList;
import java.util.List;

public class ResourceViewModel extends ViewModel {

    private final MutableLiveData<List<OperationResource>> mText;

    public ResourceViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<List<OperationResource>> queryData(Integer pageNo, Integer pageSize , String search) {
        List<OperationResource> data = new ArrayList<>();
        data.add(OperationResource.builder()
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
                .build());
        mText.setValue(data);
        return mText;

    }

}