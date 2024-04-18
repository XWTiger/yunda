package com.tiger.yunda.ui.breakdown;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.ui.home.Mission;

import java.util.List;

public class BreakDownListAdapter extends ArrayAdapter<BreakRecord> {


    public BreakDownListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }


}
