package com.tiger.yunda.service;

import com.tiger.yunda.data.model.CreateMission;
import com.tiger.yunda.data.model.DeliverMissionDTO;
import com.tiger.yunda.data.model.DeliverTask;
import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.data.model.SaveMission;
import com.tiger.yunda.data.model.Train;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.ui.home.MissionResult;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MissionService {


    public static int HTTP_OK = 200;

    /**
     * 查询子任务
     * @param body
     * @return
     */
    @POST("/api/PatrolTask/GetSubtaskPage")
    Call<MissionResult> query(@Body Map<String, Object> body);

    @POST("/api/PatrolTask/Create")
    Call<Map<String, String>> createMission(@Body CreateMission createMission);

    /**
     * 查看任务
     * @param id
     * @return
     */
    @POST("/api/PatrolTask/ViewTask")
    Call<List<DeliverMissionDTO>> queryCreatedMission(@Query("id") String id);


    @POST("/api/Train/GetNameList")
    Call<List<Train>> queryTrains();

    /**
     * 通过部门id 查用户列表
     * @param deptId
     * @return
     */
    @POST("/api/User/LoadByDept")
    Call<List<User>> queryUsers(@Query("deptid") String deptId);

    /**
     * 任务下发/保存
     * @return
     */
    @POST("/api/PatrolTask/IssuTask")
    Call<ResponseBody>  deliverMission(@Body SaveMission saveMission);

    /**
     * 接受任务
     * @param subtaskId
     * @return
     */
    @POST("/api/PatrolTask/ReceiveTask")
    Call<ResponseBody>  acceptMission(@Query("subtaskId") String  subtaskId);


    /**
     * 巡检
     * @param id
     * @return
     */
    @POST("/api/PatrolTask/PatrolTask")
    Call<ResponseBody> inspection(@Query("id") String id);


    /**
     * 查询主任务
     * @param body
     * @return
     */
    @POST("/api/PatrolTask/GetPage")
    Call<PageResult<DeliverTask>> queryMasterMission(@Body Map<String, Object> body);
}
