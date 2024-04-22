package com.tiger.yunda.ui.log;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loper7.date_time_picker.dialog.CardDatePickerDialog;
import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.WorkLog;
import com.tiger.yunda.databinding.FragmentLogBinding;
import com.tiger.yunda.databinding.LogListBinding;
import com.tiger.yunda.utils.TimeUtil;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentLogBinding fragmentLogBinding;

    private LogViewModel logViewModel;

    private LogAdapter logAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public LogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment logFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogFragment newInstance(String param1, String param2) {
        LogFragment fragment = new LogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) { //自定义应用栏
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Objects.isNull(fragmentLogBinding)) {
            fragmentLogBinding = FragmentLogBinding.inflate(inflater, container, false);
        }

        if (Objects.isNull(logViewModel)) {

            logViewModel = new ViewModelProvider(this).get(LogViewModel.class);
            logViewModel.getLogs(1, 10, 1, "", "").observe(getViewLifecycleOwner(), new Observer<List<WorkLog>>() {
                @Override
                public void onChanged(List<WorkLog> workLogs) {
                    if (Objects.isNull(logAdapter)) {
                        logAdapter = new LogAdapter(getContext(), fragmentLogBinding.list.getId(), workLogs);
                        fragmentLogBinding.list.setAdapter(logAdapter);
                    } else {
                        logAdapter.setWorkLogList(workLogs);
                        logAdapter.notifyDataSetChanged();
                    }
                }
            });
            fragmentLogBinding.freshList.setOnRefreshListener(this);
            swipeRefreshLayout = fragmentLogBinding.freshList;
            ViewHolder viewHolder = new ViewHolder(fragmentLogBinding.imageButton3,fragmentLogBinding.imageButton, getContext(), fragmentLogBinding.selectStartTime, fragmentLogBinding.selectEndTime);
        }
        // Inflate the layout for this fragment
        return fragmentLogBinding.getRoot();
    }

    @Override
    public void onRefresh() {
        logViewModel.getLogs(1, 10, 1, "", "");
        swipeRefreshLayout.setRefreshing(false);
    }

    public static class ViewHolder implements View.OnClickListener {
        private ImageButton startTimeBtn;
        private ImageButton endTimeBtn;

        private TextView startTextTime;

        private TextView endTextTime;
        private Date startTime;
        private Date endTime;

        private Context context;




        public static final String TIME_ACTION_START = "start";
        public static final String TIME_ACTION_END = "end";

        public ViewHolder(ImageButton startTimeBtn, ImageButton endTimeBtn, Context context, TextView startTextTime , TextView endTextTime) {
            this.startTimeBtn = startTimeBtn;
            this.endTimeBtn = endTimeBtn;
            startTimeBtn.setOnClickListener(this);
            startTimeBtn.setTag(TIME_ACTION_START);
            endTimeBtn.setOnClickListener(this);
            endTimeBtn.setTag(TIME_ACTION_END);
            this.startTextTime = startTextTime;
            this.endTextTime = endTextTime;
            this.context = context;

        }

        @Override
        public void onClick(View v) {
            String action = (String) v.getTag();
            if (TIME_ACTION_START.equals(action)) {

                new CardDatePickerDialog.Builder(context)
                        .setTitle("请选择开始时间")
                        .setLabelText("年","月","日","时","分", "秒")
                        .setOnChoose("选择", aLong -> {
                            //aLong  = millisecond
                            Date start = new Date(aLong);
                            startTime = start;
                            startTextTime.setText(TimeUtil.getDateYmdFromMs(start));
                            return null;
                        })
                        .setOnCancel("取消", () -> {
                            return null;
                        })
                        .build().show();
            }
            if (TIME_ACTION_END.equals(action)) {
                new CardDatePickerDialog.Builder(context)
                        .setTitle("请选择结束时间")
                        .setLabelText("年","月","日","时","分", "秒")
                        .setOnChoose("选择", aLong -> {
                            //aLong  = millisecond
                            Date end = new Date(aLong);
                            endTime = end;
                            endTextTime.setText(TimeUtil.getDateYmdFromMs(end));
                            return null;
                        })
                        .setOnCancel("取消", () -> {
                            return null;
                        })
                        .build().show();
            }
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentLogBinding = null;
    }

}