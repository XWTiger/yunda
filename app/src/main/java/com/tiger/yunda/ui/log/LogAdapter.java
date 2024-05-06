package com.tiger.yunda.ui.log;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.data.model.WorkLog;
import com.tiger.yunda.databinding.LogListBinding;
import com.tiger.yunda.service.WorkLogService;
import com.tiger.yunda.utils.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogAdapter extends ArrayAdapter<WorkLog> {
    private List<WorkLog> workLogList;
    private WorkLogService workLogService;

     FragmentManager fragmentManager;




    public LogAdapter(@NonNull Context context, int resource, @NonNull List<WorkLog> objects,  FragmentManager fragmentManager) {
        super(context, resource, objects);
        this.workLogList = objects;
        workLogService = MainActivity.retrofitClient.create(WorkLogService.class);
        this.fragmentManager = fragmentManager;
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
            ViewHolder viewHolder = new ViewHolder(logListBinding.workerDetailButton, workLogService, fragmentManager);
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
        private String WORK_LOG_SHOW = "work_log";

        private WorkLogService workLogService;
        private FragmentManager fragmentManager;

        public ViewHolder(Button detail, WorkLogService workLogService, FragmentManager fragmentManager) {
            this.detail = detail;
            detail.setOnClickListener(this);
            this.workLogService = workLogService;
            this.fragmentManager = fragmentManager;
        }


        @Override
        public void onClick(View v) {
            String id = (String) v.getTag();

            Call<WorkLog> call = workLogService.queryById(id);
            call.enqueue(new Callback<WorkLog>() {
                @Override
                public void onResponse(Call<WorkLog> call, Response<WorkLog> response) {

                    if (response.isSuccessful()) {
                        WorkLog workLog = response.body();
                        LogDialogFragment.newInstance(workLog).show(fragmentManager, WORK_LOG_SHOW);
                    } else {
                        try {
                            String errStr = response.errorBody().string();
                            ErrorResult errorResult = JsonUtil.getObject(errStr, detail.getContext());
                            Log.e("xiaweihu", "查询作业详情失败: ===========>" + errStr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<WorkLog> call, Throwable throwable) {
                    Log.e("xiaweihu", "query subtask detail failed=======: ", throwable);
                }
            });

        }
    }

    public List<WorkLog> getWorkLogList() {
        return workLogList;
    }

    public void setWorkLogList(List<WorkLog> workLogList) {
        this.workLogList = workLogList;
    }
}

