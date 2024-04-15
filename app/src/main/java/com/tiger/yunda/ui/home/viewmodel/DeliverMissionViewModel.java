package com.tiger.yunda.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.data.model.DeliverMssion;
import com.tiger.yunda.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class DeliverMissionViewModel extends ViewModel {

    private MutableLiveData<List<DeliverMssion>> deliverMissions = new MutableLiveData<>();

    private MutableLiveData<List<User>> users = new MutableLiveData<>();


    public DeliverMissionViewModel() {
    }

    public LiveData<List<DeliverMssion>> getDatas() {
        List<DeliverMssion> list = new ArrayList<>();
        list.add(new DeliverMssion(1, "tiger",1, "二段", "A单元","5h"));
        list.add(new DeliverMssion(1, "tiger",1, "二段", "B单元","5h"));
        deliverMissions.setValue(list);
        return deliverMissions;
    }

    public MutableLiveData<List<User>> getUsers(String deptId) {

        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "张三"));
        userList.add(new User(2, "王力宏"));
        users.setValue(userList);
        return users;
    }
}
