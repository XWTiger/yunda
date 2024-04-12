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

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

    private List<BreakDownType> types;
    private Context context;


    public SpinnerAdapter(List<BreakDownType> types, Context context) {
        this.types = types;
        this.context = context;
    }

    public void setData(List<BreakDownType> types) {
        this.types = types;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       //super.getView(position, convertView, parent);
        convertView = LayoutInflater.from(context).inflate(R.layout.fragment_breakdownt_dialog_list_dialog, null);
        if(convertView!=null)
        {
            TextView view =(TextView)convertView.findViewById(R.id.spinner_item);
            view.setTag(types.get(position).getType());
            view.setText(types.get(position).getName());
        }
        return convertView;

      /*  TextView view = (TextView) new TextView(context);
       // view.setWidth(ConstraintLayout.LayoutParams.WRAP_CONTENT);
        view.setTag(types.get(position).getType());
        view.setText(types.get(position).getName());
        return view;*/
    }

    @Override
    public Object getItem(int i) {
        return types.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return types.size();
    }
}
