package com.tiger.yunda.ui.home;

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

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.databinding.FragmentHomeBinding;
import com.tiger.yunda.enums.RoleType;
import com.tiger.yunda.ui.login.LoginActivity;

import java.util.Objects;

public class MissionFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProgressBar progressBar;

    private TextView textView;

    private Button myButton; //一键接受

    private Button missionyButton;


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
            View customView = LayoutInflater.from(getContext()).inflate(R.layout.header_mission_layout, null);

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
        MissionViewModel missionViewModel =
                new ViewModelProvider(this).get(MissionViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ListView listView = binding.listItem;
        progressBar = binding.progressBar;
        textView = binding.missionResultTv;



        NavController navController = NavHostFragment.findNavController(this);

        missionViewModel.getData().observe(getViewLifecycleOwner(), new Observer<MissionResult>() {
            @Override
            public void onChanged(MissionResult missionResult) {
                progressBar.setVisibility(View.GONE);
                if (missionResult.getData().size() == 0) {
                    //设置暂无任务
                    textView.setVisibility(View.VISIBLE);
                } else {

                    ListViewAdapter listViewAdapter = new ListViewAdapter(getActivity(), listView.getId(), missionViewModel.getData().getValue().getData(), getActivity());
                    listViewAdapter.setNavController(navController);
                    listViewAdapter.setFragmentManager(getFragmentManager());
                    listViewAdapter.setAcceptAll(myButton);
                    listView.setAdapter(listViewAdapter);
                }
            }
        });

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
    public void onResume() {
        super.onResume();
        if (Objects.nonNull(MainActivity.loggedInUser) &&  MainActivity.loggedInUser.getRole() == RoleType.WORKER_LEADER) {
            missionyButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}