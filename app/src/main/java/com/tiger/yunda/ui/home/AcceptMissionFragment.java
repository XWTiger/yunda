package com.tiger.yunda.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.DeliverMssion;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.databinding.DeliverMissionBinding;
import com.tiger.yunda.databinding.FragmentAcceptMissionBinding;
import com.tiger.yunda.ui.home.viewmodel.DeliverMissionAdapter;
import com.tiger.yunda.ui.home.viewmodel.DeliverMissionViewModel;

import java.util.ArrayList;
import java.util.List;


//接受任务界面
public class AcceptMissionFragment extends Fragment {

    private FragmentAcceptMissionBinding binding;

    private DeliverMissionBinding deliverMissionBinding;

    private Mission mission;


    public AcceptMissionFragment() {

    }
 /*   public AcceptMissionFragment(FragmentAcceptMissionBinding binding) {
        this.binding = binding;
    }

    public AcceptMissionFragment(int contentLayoutId, FragmentAcceptMissionBinding binding) {
        super(contentLayoutId);
        this.binding = binding;
    }*/

    public static AcceptMissionFragment newInstance(String param1, String param2) {
        AcceptMissionFragment fragment = new AcceptMissionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mission = (Mission) bundle.getSerializable(ListViewAdapter.MISSION_KEY);
        }
    }

    //实际这个类是派发任务的类
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAcceptMissionBinding.inflate(inflater, container, false);

        DeliverMissionViewModel acceptMissionViewModel = new DeliverMissionViewModel();

        List<User> dusers = new ArrayList<>();
        acceptMissionViewModel.getUsers(MainActivity.loggedInUser.getDeptId()).observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                dusers.clear();
                dusers.addAll(users);
            }
        });
        acceptMissionViewModel.getDatas(mission.getTaskId()).observe(getViewLifecycleOwner(), new Observer<List<DeliverMssion>>() {
            @Override
            public void onChanged(List<DeliverMssion> deliverMssions) {
                DeliverMissionAdapter deliverMissionAdapter = new DeliverMissionAdapter(getContext(), binding.listItem.getId(),  deliverMssions, dusers);
                          binding.listItem.setAdapter(deliverMissionAdapter);
            }
        });

        View root = binding.getRoot();
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}