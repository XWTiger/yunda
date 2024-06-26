package com.tiger.yunda.service;

import com.tiger.yunda.data.ResetPassword;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DeviceService {

    /**
     * 绑定设备
     *
     * @param body
     * @return
     */
    @POST("/api/Pad/BindDevice")
    Call<ResponseBody> bindMac(@Header ("Authorization") String auth,  @Body Map<String, String> body);


    @POST("/api/Pad/UnbindDevice")
    Call<ResponseBody> unbindMac(@Body Map<String, String> body);

    @POST("api/User/UpdatePassword")
    Call<ResponseBody> updatePassword(@Body ResetPassword body);
}
