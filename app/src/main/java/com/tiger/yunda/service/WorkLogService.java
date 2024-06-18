package com.tiger.yunda.service;

import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.data.model.WorkLog;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WorkLogService {


    @POST("/api/PatrolRecord/GetPage")
    Call<PageResult<WorkLog>> queryByPage(@Body Map body);




    @POST("/api/PatrolRecord/Get/{id}")
    Call<WorkLog> queryById(@Path("id") String id);


    //@POST("/api/PatrolTask/Appeal")
    @Multipart
    @POST("/api/PatrolEvaluation/CreateAppeal")
    Call<ResponseBody> reject(@PartMap Map<String, RequestBody> body);

    @POST("/api/PatrolEvaluation/ChangeScore")
    Call<ResponseBody> changeScore(@Body Map<String, Object> body);
}
