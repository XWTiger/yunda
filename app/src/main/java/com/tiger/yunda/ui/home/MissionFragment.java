package com.tiger.yunda.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.databinding.FragmentHomeBinding;
import com.tiger.yunda.enums.RoleType;
import com.tiger.yunda.ui.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MissionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentHomeBinding binding;


    private TextView textView;

    private Button myButton; //一键接受

    private Button missionyButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private  ListViewAdapter listViewAdapter;
    private MissionViewModel missionViewModel;

    private ListView listView;

    private MissionResult missionResult;

    private NavController navController;
    private View customView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) { //自定义应用栏
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // 可选，如果不需要显示默认标题

            // 创建自定义视图
            customView = LayoutInflater.from(getContext()).inflate(R.layout.header_mission_layout, null);

            myButton = customView.findViewById(R.id.accept_all);
            missionyButton = customView.findViewById(R.id.create_mission);


            // BlendModeColorFilter filter = new BlendModeColorFilter(Color.parseColor("#ffffff"), BlendMode.SRC_ATOP);

            NavController navController = NavHostFragment.findNavController(this);
            // 按钮的点击事件,一键领取
            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 按钮点击后的操作
                    Log.i("xiaweihu", "header on click:  =========================");
                }
            });

            //新建任务
            missionyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 按钮点击后的操作
                    Log.i("xiaweihu", "header on click:  =========================");
                    navController.navigate(R.id.to_create_mission);
                    actionBar.setDisplayShowCustomEnabled(false);
                    actionBar.setDisplayShowTitleEnabled(true);
                    //actionBar
                }
            });

            // 设置自定义视图
            actionBar.setCustomView(customView);
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (Objects.isNull(missionViewModel)) {
            missionViewModel =
                new ViewModelProvider(this).get(MissionViewModel.class);
        }

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        listView = binding.listItem;

        textView = binding.missionResultTv;
        swipeRefreshLayout = binding.freshList;
        swipeRefreshLayout.setOnRefreshListener(this);
        //自定义header
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) { //自定义应用栏
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // 可选，如果不需要显示默认标题
            if (Objects.nonNull(customView)) {
                // 设置自定义视图
                actionBar.setCustomView(customView);
            }
        }

        missionViewModel.getData(1, 30 , null, null).observe(getViewLifecycleOwner(), new Observer<MissionResult>() {
            @Override
            public void onChanged(MissionResult missionResult) {
                if (missionResult.getData().size() == 0) {
                    //设置暂无任务
                    textView.setVisibility(View.VISIBLE);
                } else {

                    if (Objects.isNull(missionResult.getData()) || missionResult.getData().size() <= 0) {
                        textView.setVisibility(View.VISIBLE);
                    } else {
                        textView.setVisibility(View.GONE);
                    }
                    if (Objects.isNull(listViewAdapter)) {
                        listViewAdapter = new ListViewAdapter(activity, listView.getId(), missionResult.getData(), getActivity(), missionViewModel);
                        listViewAdapter.setNavController(navController);
                        listViewAdapter.setFragmentManager(getFragmentManager());
                        listViewAdapter.setAcceptAll(myButton);
                        listView.setAdapter(listViewAdapter);
                    } else {
                        listViewAdapter.setObjects(missionResult.getData());
                        // listView.setAdapter(listViewAdapter);
                        listViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        });



        navController = NavHostFragment.findNavController(this);



        return root;
    }

  /*  @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.header_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
        Log.i("xiaweihu", "onOptionsItemSelected: ========================" + item.getItemId());
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Objects.nonNull(MainActivity.loggedInUser) && MainActivity.loggedInUser.getRole() == RoleType.WORKER_LEADER) {
            missionyButton.setVisibility(View.VISIBLE);
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) { //自定义应用栏
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

        }
        if (Objects.nonNull(listView)) {
            listView.setAdapter(listViewAdapter);
        }
        if (Objects.isNull(missionResult)) {
            missionViewModel.getData(1, 30, null, null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRefresh() {
        missionViewModel.addOne();
        swipeRefreshLayout.setRefreshing(false);
    }
}