package com.tiger.yunda.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tiger.yunda.data.BreakDownType;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<BreakDownType> {

    private List<BreakDownType> types;
    private Context context;


    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<BreakDownType> objects) {
        super(context, resource, objects);
        this.types = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       //super.getView(position, convertView, parent);
        TextView view = (TextView) new TextView(context);
       // view.setWidth(ConstraintLayout.LayoutParams.WRAP_CONTENT);
        view.setTag(types.get(position).getType());
        view.setText(types.get(position).getName());
        return view;
    }

    @Override
    public int getCount() {
        return types.size();
    }
}
