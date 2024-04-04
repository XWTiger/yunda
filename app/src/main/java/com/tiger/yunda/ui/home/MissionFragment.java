package com.tiger.yunda.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.tiger.yunda.R;
import com.tiger.yunda.databinding.FragmentHomeBinding;
import com.tiger.yunda.ui.login.LoginViewModel;
import com.tiger.yunda.ui.login.LoginViewModelFactory;

public class MissionFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ProgressBar progressBar;

    private TextView textView;

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

                    ListViewAdapter listViewAdapter = new ListViewAdapter(getActivity(), listView.getId(), missionViewModel.getData().getValue().getData());
                    listViewAdapter.setNavController(navController);
                    listViewAdapter.setFragmentManager(getFragmentManager());
                    listView.setAdapter(listViewAdapter);
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}