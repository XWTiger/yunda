package com.tiger.yunda.service;

import com.tiger.yunda.data.model.OperationResource;
import com.tiger.yunda.data.model.PageResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ResourceService {

    @POST("/api/PatrolData/GetPage")
    Call<PageResult<OperationResource>> queryResource(@Body Map<String, Object> map);
}
