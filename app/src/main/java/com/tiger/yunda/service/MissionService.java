package com.tiger.yunda.service;

import com.tiger.yunda.ui.home.MissionResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MissionService {

    //TODO 没有加头
    @POST("/api/PatrolTask/GetSubtaskPage")
    Call<MissionResult> query(@Body Map<String, Object> body);
}
