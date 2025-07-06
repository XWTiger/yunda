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
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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


    /**
     * {
     *   "page": 1,
     *   "limit": 10,
     *   "sorts": [],
     *   "search": "",
     *   "id": "string"
     * }
     * @param body
     * @return
     */
    @POST("/api/PatrolTask/GetAllowUpdateSubtask")
    Call<MissionResult> queryUpdateSubtaskPage(@Body Map<String, Object> body);

    @POST("/api/PatrolTask/Create")
    Call<Map<String, String>> createMission(@Body CreateMission createMission);


    /**
     * {
     *   "id": "string", 主任务id
     *   "subtasks": [
     *     {
     *       "id": "string", 子任务id
     *       "inspectorId": 0, 巡检人id
     *       "positionId": 0 巡检列位
     *     }
     *   ]
     * }
     * @param body
     * @return
     */
    @POST("/api/PatrolTask/UpdateAllowUpdateSubtask")
    Call<ResponseBody> updateSubtaskMission(@Body SaveMission body);

    /**
     * 查看任务
     * @param id
     * @return
     */
    @POST("/api/PatrolTask/ViewTask")
    Call<List<DeliverMissionDTO>> queryCreatedMission(@Query("id") String id);


    @POST("/api/Train/GetNameList")
    Call<List<Train>> queryTrains();

    @POST("/api/Position/GetNameList")
    Call<List<Train>> queryPositions();
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

    @POST("/api/PatrolTask/Refuse/{taskId}")
    Call<ResponseBody> rejectMission(@Path("taskId") String taskId);


    /**
     * 通过列车号 查询 列车位置
     * @param trainNo 车号
     * @return
     */
    @POST("/api/Train/GetPosId/{trainNo}")
    Call<ResponseBody> getPositionByTrainNo(@Path("trainNo") String trainNo);
}
