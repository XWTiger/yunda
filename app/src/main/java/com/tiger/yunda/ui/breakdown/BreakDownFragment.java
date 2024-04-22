package com.tiger.yunda.ui.breakdown;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import java.util.Objects;

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

    private FragmentBreakDownBinding fragmentBreakDownBinding;

    private static final String TIME_ACTION_START = "start";
    private static final String TIME_ACTION_END = "end";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BreakDownListAdapter breakDownListAdapter;
    private ViewHolder viewHolder;

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

        // Inflate the layout for this fragment
        fragmentBreakDownBinding = FragmentBreakDownBinding.inflate(inflater, container, false);


        if (Objects.isNull(viewHolder)) {
            viewHolder = new ViewHolder(fragmentBreakDownBinding.selectStartTime, fragmentBreakDownBinding.selectEndTime, getContext(), new BreakDownViewModel());
            viewHolder.setSwipeRefreshLayout(fragmentBreakDownBinding.freshList);
        }
        viewHolder.getBreakDownViewModel().getBreakRecords(1, 100, "2024-01-01 00:00:00", "2024-04-01 00:00:00", Integer.valueOf(MainActivity.loggedInUser.getDeptId()))
                .observe(getViewLifecycleOwner(), new Observer<List<BreakRecord>>() {
                    @Override
                    public void onChanged(List<BreakRecord> breakRecords) {
                        if (Objects.isNull(breakDownListAdapter)) {
                            breakDownListAdapter = new BreakDownListAdapter(getContext(), fragmentBreakDownBinding.list.getId(), breakRecords, getFragmentManager());
                            fragmentBreakDownBinding.list.setAdapter(breakDownListAdapter);
                            fragmentBreakDownBinding.list.setOnScrollListener(viewHolder);
                        } else {
                            breakDownListAdapter.setBreakRecords(breakRecords);
                            breakDownListAdapter.notifyDataSetChanged();
                        }
                    }
                });
        fragmentBreakDownBinding.imageButton3.setOnClickListener(viewHolder);
        fragmentBreakDownBinding.imageButton3.setTag(TIME_ACTION_START);
        fragmentBreakDownBinding.imageButton.setTag(TIME_ACTION_END);
        fragmentBreakDownBinding.imageButton.setOnClickListener(viewHolder);
        fragmentBreakDownBinding.freshList.setOnRefreshListener(viewHolder);
        return fragmentBreakDownBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentBreakDownBinding = null;
    }
    public static class ViewHolder implements View.OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

        private TextView startTime;

        private TextView endTime;

        private Context context;

        private SwipeRefreshLayout swipeRefreshLayout;

        private BreakDownViewModel breakDownViewModel;

        private Date startDateTime;
        private Date endDateTime;

        public ViewHolder(TextView startTime, TextView endTime, Context context, BreakDownViewModel breakDownViewModel) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.context = context;
            this.breakDownViewModel = breakDownViewModel;
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

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                // 判断是否滚动到底部
                if (view.getLastVisiblePosition() >= view.getCount() - 1) {
                    // 加载更多数据

                    breakDownViewModel.getBreakRecords(1, 100, TimeUtil.getSTrFromMs(startDateTime), TimeUtil.getSTrFromMs(endDateTime), Integer.valueOf(MainActivity.loggedInUser.getDeptId()));
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

        public BreakDownViewModel getBreakDownViewModel() {
            return breakDownViewModel;
        }

        public void setBreakDownViewModel(BreakDownViewModel breakDownViewModel) {
            this.breakDownViewModel = breakDownViewModel;
        }

        @Override
        public void onRefresh() {
            breakDownViewModel.getBreakRecords(1, 100, TimeUtil.getSTrFromMs(startDateTime), TimeUtil.getSTrFromMs(endDateTime), Integer.valueOf(MainActivity.loggedInUser.getDeptId()));
            swipeRefreshLayout.setRefreshing(false);
        }

        public SwipeRefreshLayout getSwipeRefreshLayout() {
            return swipeRefreshLayout;
        }

        public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
            this.swipeRefreshLayout = swipeRefreshLayout;
        }
    }

}