package com.tiger.yunda.service;

import com.tiger.yunda.service.params.PauseParams;
import com.tiger.yunda.ui.home.Mission;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InspectionService {

    /**
     * 开始巡检
     * @param id
     * @return
     */
    @POST("/api/PatrolTask/PatrolTrainLocationStart")
    Call<ResponseBody> startInspection(@Query("id") String id);


    /**
     * 暂停/继续巡检
     * @param params
     * @return
     */
    @POST("/api/PatrolTask/PatrolTrainLocationAction")
    Call<ResponseBody>  pauseOrResumeInspection(@Body PauseParams params);


    /**
     * 结束巡检
     * @param id 巡检车位置id
     * @return
     */
    @POST("/api/PatrolTask/PatrolTrainLocationEnd")
    Call<ResponseBody>  finishedInspection(@Query("id") String id);



    /**
     * 任务完成
     * @param taskId 任务id
     * @return
     */
    @POST("/api/PatrolTask/DoneTask")
    Call<ResponseBody>  finishedMission(@Query("id") String taskId);


    /**
     * 根据id获取巡检作业详情
     */
    @POST("api/PatrolRecord/Get/{id}")
    Call<Mission> querySubtaskDetail(@Path("id") String id);


    @POST("/api/PatrolFault/Create")
    @Multipart
    Call<Map<String, String>> createBreakDownRecord(@Part("SubtaskId") String subtaskId, @Part("TrainLocationId") String trainLocationId, @Part("Type") Integer type, @Part("Desc") String desc,
                                                    @Part("IsDiscretion") Boolean disc, @PartMap Map<String, RequestBody>  Files, @Part("HandleDesc") String handleDesc, @PartMap Map<String, RequestBody> HandleFiles);


    @POST("/api/DataDict/GetNameList/{type}")
    Call<List<Map<String, Object>>> queryDictList(@Path("type") String type);
}
