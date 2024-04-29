package com.tiger.yunda.ui.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiger.yunda.data.BreakDownType;
import com.tiger.yunda.service.InspectionService;

import java.util.ArrayList;
import java.util.List;

public class BreakDownListViewModel extends ViewModel {

    private InspectionService inspectionService;

    private MutableLiveData<List<BreakDownType>> types = new MutableLiveData<>();


    public BreakDownListViewModel() {
        List<BreakDownType> list = new ArrayList<>();
        list.add(BreakDownType.builder()
                        .type(1)
                        .name("PIS")
                .build());
        list.add(BreakDownType.builder()
                .type(2)
                .name("AIS")
                .build());
        this.types.setValue(list);
    }




    public LiveData<List<BreakDownType>> getTypes() {

        List<BreakDownType> list = new ArrayList<>();
        list.add(BreakDownType.builder()
                .type(1)
                .name("PIS")
                .build());
        list.add(BreakDownType.builder()
                .type(2)
                .name("AIS")
                .build());
        this.types.setValue(list);


        return types;
    }

    public InspectionService getInspectionService() {
        return inspectionService;
    }

    public void setInspectionService(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }
}
