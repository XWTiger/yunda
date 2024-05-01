package com.tiger.yunda.service;

import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.data.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BreakDownService {

    /**
     * 查询巡检故障分页
     * {"page":0,"limit":0,"sorts":[{"filed":"","type":""}],"search":"","deptId":0,"trainNo":"","type":0,"reportUserId":0,"startTime":"","endTime":"","state":0}
     * @param body
     * @return
     */
    @POST("/api/PatrolFault/GetPage")
    Call<PageResult<BreakRecord>> queryBreakRecordList(@Body Map<String, Object> body);

    /**
     * 根据id查询故障详情
     * @param id 故障id
     * @return
     */
    @POST("/api/PatrolFault/Get/{id}")
    Call<BreakRecord> queryByBreakId(@Path("id") String id);

    /**
     * 查询用户列表
     *
     * @param deptId
     * @return
     */
    @POST("/api/User/LoadByDept")
    Call<List<User>> queryUsers(@Query("deptid") String deptId);
}
