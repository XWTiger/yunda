package com.tiger.yunda.ui.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tiger.yunda.data.model.WorkLog;
import com.tiger.yunda.databinding.LogListBinding;

import java.util.List;
import java.util.Objects;

public class LogAdapter extends ArrayAdapter<WorkLog> {
    private List<WorkLog> workLogList;


    public LogAdapter(@NonNull Context context, int resource, @NonNull List<WorkLog> objects) {
        super(context, resource, objects);
        this.workLogList = objects;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (Objects.isNull(convertView)) {

            LogListBinding  logListBinding = LogListBinding.inflate(LayoutInflater.from(getContext()), parent, false);

            logListBinding.workerDetailButton.setTag(workLogList.get(position).getId());
            //动态设置分数的位置 由于五角星是底图要对正咯
            ViewGroup.LayoutParams layoutParams = logListBinding.textScore.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                // 设置layout_marginStart的值
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                       if (Objects.nonNull(workLogList.get(position).getScore()) && workLogList.get(position).getScore().length() >= 2) {
                           marginLayoutParams.setMarginStart(-65); // 设置layout_marginStart的值为24dp
                        } else {
                           marginLayoutParams.setMarginStart(-50);
                        }

                logListBinding.textScore.setLayoutParams(marginLayoutParams); // 将修改后的LayoutParams重新设置给View
            }
            ViewHolder viewHolder = new ViewHolder(logListBinding.workerDetailButton);
            logListBinding.setLog(workLogList.get(position));
            convertView = logListBinding.getRoot();
        }


        return convertView;

    }

    @Override
    public int getCount() {
        return workLogList.size();
    }


    public static class ViewHolder implements View.OnClickListener {

        private  Button detail;


        public ViewHolder(Button detail) {
            this.detail = detail;
            detail.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            String id = (String) v.getTag();
        }
    }

    public List<WorkLog> getWorkLogList() {
        return workLogList;
    }

    public void setWorkLogList(List<WorkLog> workLogList) {
        this.workLogList = workLogList;
    }
}

