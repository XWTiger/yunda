package com.tiger.yunda.ui.home;

import java.io.Serializable;
import java.util.List;



public class MissionResult implements Serializable {
    private int count;
    private List<Mission> data;


    public MissionResult(int count, List<Mission> data) {
        this.count = count;
        this.data = data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Mission> getData() {
        return data;
    }

    public void setData(List<Mission> data) {
        this.data = data;
    }
}
