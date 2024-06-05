package com.tiger.yunda.service;

import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.data.model.WorkLog;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WorkLogService {


    @POST("/api/PatrolRecord/GetPage")
    Call<PageResult<WorkLog>> queryByPage(@Body Map body);




    @POST("/api/PatrolRecord/Get/{id}")
    Call<WorkLog> queryById(@Path("id") String id);


    @POST("/api/PatrolTask/Appeal")
    Call<ResponseBody> reject(@Body Map<String, Object> body);
}
