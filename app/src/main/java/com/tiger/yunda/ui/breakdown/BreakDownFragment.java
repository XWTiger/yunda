package com.tiger.yunda.ui.breakdown;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;

import com.loper7.date_time_picker.DateTimeConfig;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;
import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.databinding.BreakdownRecordeListBinding;
import com.tiger.yunda.databinding.FragmentBreakDownBinding;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.TimeUtil;

import java.util.Arrays;
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
            /*SpannableString title = new SpannableString("故障");
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(title);*/
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
            viewHolder = new ViewHolder(fragmentBreakDownBinding.selectStartTime, getContext(), new BreakDownViewModel(getContext()));
            viewHolder.setSwipeRefreshLayout(fragmentBreakDownBinding.freshList);
        }
        NavController navController = NavHostFragment.findNavController(this);
        viewHolder.getBreakDownViewModel().getBreakRecords(1, 100, null, null, Objects.isNull(MainActivity.loggedInUser) ? null : Integer.valueOf(MainActivity.loggedInUser.getDeptId()))
                .observe(getViewLifecycleOwner(), new Observer<List<BreakRecord>>() {
                    @Override
                    public void onChanged(List<BreakRecord> breakRecords) {
                        if (CollectionUtil.isEmpty(breakRecords)) {
                            fragmentBreakDownBinding.noContent.setVisibility(View.VISIBLE);
                        } else {
                            fragmentBreakDownBinding.noContent.setVisibility(View.GONE);
                        }

                       /* if (Objects.isNull(breakDownListAdapter)) {*/
                            breakDownListAdapter = new BreakDownListAdapter(getContext(), fragmentBreakDownBinding.list.getId(), breakRecords, getFragmentManager(), navController);
                            fragmentBreakDownBinding.list.setAdapter(breakDownListAdapter);
                            fragmentBreakDownBinding.list.setOnScrollListener(viewHolder);
                       /* } else {
                            breakDownListAdapter.setBreakRecords(breakRecords);
                            breakDownListAdapter.notifyDataSetChanged();
                        }*/
                    }
                });
        fragmentBreakDownBinding.imageButton3.setOnClickListener(viewHolder);
        fragmentBreakDownBinding.imageButton3.setTag(TIME_ACTION_START);
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

        public ViewHolder(TextView startTime,  Context context, BreakDownViewModel breakDownViewModel) {
            this.startTime = startTime;
         /*   this.endTime = endTime;*/
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
                        .setDisplayType(Arrays.asList(
                                DateTimeConfig.YEAR,//显示年
                                DateTimeConfig.MONTH,//显示月
                                DateTimeConfig.DAY//显示日DateTimeConfig.HOUR,//显示时 DateTimeConfig.MIN
                                ))//显示分
                        .setOnChoose("选择", aLong -> {
                            //aLong  = millisecond
                            Date start = new Date(aLong);
                            startDateTime = TimeUtil.getStartOfDay(start);
                            startTime.setText(TimeUtil.getDateYmdFromMs(start));
                            endDateTime = TimeUtil.getEndOfDay(start);
                            if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime)) {
                                breakDownViewModel.getBreakRecords(1, 100, TimeUtil.getSTrFromMs(startDateTime), TimeUtil.getSTrFromMs(endDateTime), Integer.valueOf(MainActivity.loggedInUser.getDeptId()));
                            }
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
                            if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime)) {
                                breakDownViewModel.getBreakRecords(1, 100, TimeUtil.getSTrFromMs(startDateTime), TimeUtil.getSTrFromMs(endDateTime), Integer.valueOf(MainActivity.loggedInUser.getDeptId()));
                            }
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

                    breakDownViewModel.getBreakRecords(1, 100, Objects.nonNull(startDateTime)?TimeUtil.getSTrFromMs(startDateTime):null, Objects.nonNull(endDateTime)?TimeUtil.getSTrFromMs(endDateTime):null,  Objects.nonNull(MainActivity.loggedInUser)?Integer.valueOf(MainActivity.loggedInUser.getDeptId()): 0);
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
            breakDownViewModel.getBreakRecords(1, 100,  Objects.nonNull(startDateTime)?TimeUtil.getSTrFromMs(startDateTime):null, Objects.nonNull(endDateTime)?TimeUtil.getSTrFromMs(endDateTime):null, Objects.nonNull(MainActivity.loggedInUser)?Integer.valueOf(MainActivity.loggedInUser.getDeptId()): 0);
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