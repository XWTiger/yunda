package com.tiger.yunda.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
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

import com.google.android.material.tabs.TabLayout;
import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.Version;
import com.tiger.yunda.databinding.FragmentHomeBinding;
import com.tiger.yunda.databinding.HeaderMissionLayoutBinding;
import com.tiger.yunda.enums.RoleType;
import com.tiger.yunda.service.LoginService;
import com.tiger.yunda.service.MissionService;
import com.tiger.yunda.ui.common.Constraints;
import com.tiger.yunda.utils.DownLoadUtil;
import com.tiger.yunda.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MissionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

    private FragmentHomeBinding binding;


    private TextView textView;

    private Button myButton; //一键接受

    private Button missionyButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private  ListViewAdapter listViewAdapter;
    private static MissionViewModel missionViewModel;


    private ListView listView;

    public static AtomicInteger missionFlag = new AtomicInteger(0);

    private NavController navController;

    private LoginService loginService;
    private View customView;

    private HeaderMissionLayoutBinding headerMissionLayoutBinding;

    private TabLayout tabLayout;
    private TextView todoTv;

    public static Boolean leader = false;

    public static Boolean masterMission = false;

    public static String needUpdatePath = null;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Objects.isNull(loginService)) {
            loginService = MainActivity.retrofitClient.create(LoginService.class);
        }
       // setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        this.customHeaderBar(actionBar);
        if (Objects.nonNull(MainActivity.loggedInUser) && MainActivity.loggedInUser.getRole().name().equals(RoleType.WORKER_LEADER.name())) {
            leader = true;
            masterMission = false;
            if (masterMission) {
                tabLayout.getTabAt(0).select();
            } else {
                tabLayout.getTabAt(1).select();
            }
        }


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (Objects.nonNull(MainActivity.loggedInUser) && MainActivity.loggedInUser.getRole().name().equals(RoleType.WORKER_LEADER.name())) {
            leader = true;
            masterMission = false;
        } else {
            leader = false;
            masterMission = false;
        }
        if (Objects.isNull(missionViewModel)) {
            missionViewModel = new ViewModelProvider(this).get(MissionViewModel.class);
            missionViewModel.setContext(getContext());
        }

        if (masterMission) {
            tabLayout.getTabAt(0).select();
        } else {
            tabLayout.getTabAt(1).select();
        }

        if (Objects.isNull(binding)) {
            binding = FragmentHomeBinding.inflate(inflater, container, false);
        }

        View root = binding.getRoot();


        listView = binding.listItem;
        listView.setOnScrollListener(this);

        textView = binding.missionResultTv;
        swipeRefreshLayout = binding.freshList;
        swipeRefreshLayout.setOnRefreshListener(this);

        //自定义header
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) { //自定义应用栏
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // 可选，如果不需要显示默认标题
            if (Objects.isNull(customView)) {
                customHeaderBar(actionBar);
            }
        }

        missionViewModel.getData(1, 300 , null, null, masterMission).observe(getViewLifecycleOwner(), new Observer<MissionResult>() {
            @Override
            public void onChanged(MissionResult missionResult) {
                if (missionFlag.get() < 1) {
                    missionFlag.getAndIncrement();
                }

                if (Objects.isNull(missionResult.getData()) || missionResult.getData().size() <= 0) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                }

                listViewAdapter = new ListViewAdapter(activity, listView.getId(), missionResult.getData(), getActivity(), missionViewModel);
                listViewAdapter.setNavController(navController);
                listViewAdapter.setFragmentManager(getFragmentManager());
                listViewAdapter.setAcceptAll(myButton);
                listView.setAdapter(listViewAdapter);


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
            leader = true;
            masterMission = false;
        } else {
            leader = false;
            masterMission = false;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) { //自定义应用栏
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            // 设置自定义视图

            customHeaderBar(actionBar);


        }
        if (Objects.nonNull(headerMissionLayoutBinding)) {
            myButton = headerMissionLayoutBinding.acceptAll;

            missionyButton = headerMissionLayoutBinding.createMission;
            tabLayout = headerMissionLayoutBinding.tabLayout;
            todoTv = headerMissionLayoutBinding.todoMission;
        }

       /* if (Objects.nonNull(listView)) {
            listView.setAdapter(listViewAdapter);
        }*/

        if (Objects.nonNull(missionFlag) && missionFlag.get() ==  1) {
            checkUpdate();
            missionViewModel.updateService();
            missionViewModel.getData(1, 300, null, null, masterMission);
            missionFlag.getAndIncrement();
            if (masterMission) {
                tabLayout.getTabAt(0).select();
            } else {
                tabLayout.getTabAt(1).select();
            }
        }
        if (leader) {
            missionyButton.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            todoTv.setVisibility(View.GONE);
        } else {
            missionyButton.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            todoTv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRefresh() {
        missionViewModel.addOne(masterMission);
        swipeRefreshLayout.setRefreshing(false);
    }


    private void customHeaderBar(ActionBar actionBar) {

        if (actionBar != null) { //自定义应用栏
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // 可选，如果不需要显示默认标题
            if (Objects.isNull(headerMissionLayoutBinding)) {
                headerMissionLayoutBinding = HeaderMissionLayoutBinding.inflate(LayoutInflater.from(getContext()));
            }
            // 创建自定义视图
            //customView = LayoutInflater.from(getContext()).inflate(R.layout.header_mission_layout, null);

            View headerView = actionBar.getCustomView();
            if (Objects.isNull(customView)) {

                customView = headerMissionLayoutBinding.getRoot();

                myButton = headerMissionLayoutBinding.acceptAll;

                missionyButton = headerMissionLayoutBinding.createMission;
                tabLayout = headerMissionLayoutBinding.tabLayout;
                todoTv = headerMissionLayoutBinding.todoMission;
                if (leader) {
                    masterMission = true;
                    //missionyButton.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                } else {
                    //missionyButton.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                }
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        Log.i("xiaweihu", "onTabSelected: ========>" + tab.getText());
                        if (Constraints.MISSION_TYPE_MASTER.equals(tab.getText())) {
                            masterMission = true;
                        } else {
                            masterMission = false;
                        }
                        if (Objects.nonNull(missionFlag) && missionFlag.get() >=  1) {
                            missionViewModel.getData(1, 300, null, null, masterMission);
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        Log.i("xiaweihu", "unSelected: ========>" + tab.getText());
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        Log.i("xiaweihu", "reSelected: ========>" + tab.getText());
                    }
                });

                NavController navController = NavHostFragment.findNavController(this);
                // 按钮的点击事件,一键领取
              /*  myButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 按钮点击后的操作
                        Log.i("xiaweihu", "header on click:  =========================");
                    }
                });*/


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

            }
            // 设置自定义视图
            actionBar.setCustomView(customView);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0) {
            if (Objects.nonNull(swipeRefreshLayout)) {
                swipeRefreshLayout.setEnabled(true);
            }
            // 列表滚动到了顶部
            // 在这里执行刷新数据的操作
        }
        if (firstVisibleItem + visibleItemCount == totalItemCount) {
            // 列表滚动到了底部
            // 在这里执行加载更多数据的操作
            if (Objects.nonNull(swipeRefreshLayout)) {
                swipeRefreshLayout.setEnabled(false);
            }

        }
    }

    public void checkUpdate() {
        loginService.version().enqueue(new Callback<Version>() {
            @Override
            public void onResponse(Call<Version> call, Response<Version> response) {
                if (response.code() == MissionService.HTTP_OK) {
                    Version version = response.body();
                    PackageManager manager = getActivity().getPackageManager();
                    PackageInfo info = null;
                    try {
                        info = manager.getPackageInfo(getActivity().getPackageName(), 0);
                        long versionCode = info.getLongVersionCode();
                        if (versionCode < Long.parseLong(version.getVersionNumber())) {
                            needUpdatePath = version.getFilePath();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("更新")
                                    .setMessage("当前版本：" +info.versionName + "\n存在新版本: " + version.getVersionNo())
                                    .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (StringUtils.isNotBlank(version.getFilePath())) {
                                                DownLoadUtil.downLoad(version.getFilePath(), getContext(), "Yunda.apk");
                                            }
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 点击“Cancel”按钮后的操作
                                            if (version.getIsForceUpdate()) {
                                                System.exit(0);
                                            }
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {

                    try {

                        String errStr = response.errorBody().string();
                        ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                        Log.e("xiaweihu", "查询主任务列表失败: ===========>" + errStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Version> call, Throwable throwable) {

            }
        });
    }
}