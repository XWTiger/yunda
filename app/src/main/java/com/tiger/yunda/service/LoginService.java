package com.tiger.yunda.service;

import com.tiger.yunda.data.internet.TokenResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {


    @POST("/api/Authentication/GetToken")
    Call<TokenResult> login(@Body Map<String, String> body);
}
