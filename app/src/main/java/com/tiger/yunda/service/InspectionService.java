package com.tiger.yunda.service;

import com.tiger.yunda.service.params.PauseParams;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
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
    @POST("/api/PatrolTask/PatrolTrainLocationAction")
    Call<ResponseBody>  finishedInspection(@Query("id") String id);



    /**
     * 任务完成
     * @param taskId 任务id
     * @return
     */
    @POST("/api/PatrolTask/DoneTask")
    Call<ResponseBody>  finishedMission(@Query("id") String taskId);
}
