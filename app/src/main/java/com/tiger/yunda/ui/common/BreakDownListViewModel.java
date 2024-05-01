package com.tiger.yunda.ui.common;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.data.BreakDownType;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.service.InspectionService;
import com.tiger.yunda.ui.home.MissionResult;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreakDownListViewModel extends ViewModel {

    private InspectionService inspectionService;

    private MutableLiveData<List<BreakDownType>> types = new MutableLiveData<>();


    public BreakDownListViewModel() {
        /*List<BreakDownType> list = new ArrayList<>();
        list.add(BreakDownType.builder()
                        .type(1)
                        .name("PIS")
                .build());
        list.add(BreakDownType.builder()
                .type(2)
                .name("AIS")
                .build());
        this.types.setValue(list);*/
    }




    public LiveData<List<BreakDownType>> getTypes() {

      /*  List<BreakDownType> list = new ArrayList<>();
       *//* list.add(BreakDownType.builder()
                .type(1)
                .name("PIS")
                .build());
        list.add(BreakDownType.builder()
                .type(2)
                .name("AIS")
                .build());*//*
        this.types.setValue(list);*/
        Call<List<Map<String, Object>>> result = inspectionService.queryDictList("41");

        result.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    List<BreakDownType> list = new ArrayList<>();
                    List<Map<String, Object>> resultList = response.body();
                    if (!CollectionUtil.isEmpty(resultList)) {
                        resultList.forEach(stringObjectMap -> {
                            String text = (String) stringObjectMap.get("text");
                            Integer type = Integer.parseInt(stringObjectMap.get("value").toString());
                            list.add(BreakDownType.builder()
                                            .type(type)
                                            .name(text)
                                    .build());
                        });
                        types.setValue(list);
                    }
                } else {
                    try {
                        String errStr = response.errorBody().string();
                        Log.e("xiaweihu", "查询故障列表失败: ===========>" + errStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable throwable) {

            }
        });


        return types;
    }

    public InspectionService getInspectionService() {
        return inspectionService;
    }

    public void setInspectionService(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }
}
