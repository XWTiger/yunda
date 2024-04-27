package com.tiger.yunda.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tiger.yunda.R;
import com.tiger.yunda.data.BreakDownType;
import com.tiger.yunda.ui.home.viewmodel.DeliverMissionAdapter;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter  {

    private List<BreakDownType> types;
    private Context context;

    private SpinnerCallBack deliverMissionAdapter;

    private int parentIndex;


    public SpinnerAdapter(List<BreakDownType> types, Context context, SpinnerCallBack deliverMissionAdapter, int parentIndex) {
        this.types = types;
        this.context = context;
        this.deliverMissionAdapter = deliverMissionAdapter;
        this.parentIndex = parentIndex;
    }

    public void setData(List<BreakDownType> types) {
        this.types = types;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_breakdownt_dialog_list_dialog, null);
            TextView view =(TextView)convertView.findViewById(R.id.spinner_item);
            view.setTag(position);
            view.setText(types.get(position).getName());
        return convertView;

    }

    @Override
    public Object getItem(int i) {
        return types.get(i);
    }

    @Override
    public long getItemId(int i) {
        return types.get(i).getType();
    }

    @Override
    public int getCount() {
        return types.size();
    }


}
