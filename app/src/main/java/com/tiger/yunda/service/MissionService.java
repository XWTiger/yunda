package com.tiger.yunda.service;

import com.tiger.yunda.data.model.CreateMission;
import com.tiger.yunda.data.model.DeliverMissionDTO;
import com.tiger.yunda.data.model.Train;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.ui.home.MissionResult;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MissionService {


    @POST("/api/PatrolTask/GetSubtaskPage")
    Call<MissionResult> query(@Body Map<String, Object> body);

    @POST("/api/PatrolTask/Create")
    Call<Map<String, String>> createMission(@Body CreateMission createMission);

    @POST("/api/PatrolTask/ViewTask")
    Call<DeliverMissionDTO> queryCreatedMission(@Query("id") String id);


    @POST("/api/Train/GetNameList")
    Call<List<Train>> queryTrains();

    @POST("/api/User/LoadByDept")
    Call<List<User>> queryUsers(@Query("deptid") String deptId);
}
