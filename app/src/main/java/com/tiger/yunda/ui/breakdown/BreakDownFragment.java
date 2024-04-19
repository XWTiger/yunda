package com.tiger.yunda.ui.breakdown;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loper7.date_time_picker.dialog.CardDatePickerDialog;
import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.databinding.BreakdownRecordeListBinding;
import com.tiger.yunda.databinding.FragmentBreakDownBinding;
import com.tiger.yunda.utils.TimeUtil;

import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BreakDownFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BreakDownFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private BreakdownRecordeListBinding breakdownRecordeListBinding;
    private FragmentBreakDownBinding fragmentBreakDownBinding;

    private static final String TIME_ACTION_START = "start";
    private static final String TIME_ACTION_END = "end";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BreakDownFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BreakDownFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BreakDownFragment newInstance(String param1, String param2) {
        BreakDownFragment fragment = new BreakDownFragment();
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
        breakdownRecordeListBinding = BreakdownRecordeListBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        fragmentBreakDownBinding = FragmentBreakDownBinding.inflate(inflater, container, false);
        BreakDownViewModel breakDownViewModel = new BreakDownViewModel();
        ViewHolder viewHolder = new ViewHolder(fragmentBreakDownBinding.selectStartTime, fragmentBreakDownBinding.selectEndTime, getContext());
        breakDownViewModel.getBreakRecords(1000, "2024-01-01 00:00:00", "2024-04-01 00:00:00", Integer.valueOf(MainActivity.loggedInUser.getDeptId()))
                .observe(getViewLifecycleOwner(), new Observer<List<BreakRecord>>() {
                    @Override
                    public void onChanged(List<BreakRecord> breakRecords) {
                        BreakDownListAdapter breakDownListAdapter = new BreakDownListAdapter(getContext(), fragmentBreakDownBinding.list.getId(), breakRecords, breakdownRecordeListBinding);
                        fragmentBreakDownBinding.list.setAdapter(breakDownListAdapter);
                    }
                });
        fragmentBreakDownBinding.imageButton3.setOnClickListener(viewHolder);
        fragmentBreakDownBinding.imageButton3.setTag(TIME_ACTION_START);
        fragmentBreakDownBinding.imageButton.setTag(TIME_ACTION_END);
        fragmentBreakDownBinding.imageButton.setOnClickListener(viewHolder);
        return fragmentBreakDownBinding.getRoot();
    }

    public static class ViewHolder implements View.OnClickListener {

        private TextView startTime;

        private TextView endTime;

        private Context context;



        private Date startDateTime;
        private Date endDateTime;

        public ViewHolder(TextView startTime, TextView endTime, Context context) {
            this.startTime = startTime;
            this.endTime = endTime;
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
                             startDateTime = start;
                             startTime.setText(TimeUtil.getDateYmdFromMs(start));
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
                            endDateTime = end;
                            endTime.setText(TimeUtil.getDateYmdFromMs(end));
                            return null;
                        })
                        .setOnCancel("取消", () -> {
                            return null;
                        })
                        .build().show();
            }
        }
    }

}