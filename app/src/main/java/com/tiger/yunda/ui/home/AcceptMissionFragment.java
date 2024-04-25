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
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.DeliverMssion;
import com.tiger.yunda.data.model.SaveMission;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.databinding.DeliverMissionBinding;
import com.tiger.yunda.databinding.FragmentAcceptMissionBinding;
import com.tiger.yunda.ui.home.viewmodel.DeliverMissionAdapter;
import com.tiger.yunda.ui.home.viewmodel.DeliverMissionViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//接受任务界面
public class AcceptMissionFragment extends Fragment implements View.OnClickListener {

    private FragmentAcceptMissionBinding binding;

    private DeliverMissionBinding deliverMissionBinding;

    private DeliverMissionViewModel acceptMissionViewModel;

    private Mission mission;

    private NavController navController;

    public static String BTN_CANCEL_TAG = "cancel";

    public static String BTN_DELIVER_TAG = "deliver";
    public static String BTN_SAVE_TAG = "save";

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
        if (Objects.isNull(binding)) {
            binding = FragmentAcceptMissionBinding.inflate(inflater, container, false);
            binding.cancelButton.setTag(BTN_CANCEL_TAG);
            binding.cancelButton.setOnClickListener(this);
            binding.deliverButton.setTag(BTN_DELIVER_TAG);
            binding.deliverButton.setOnClickListener(this);
            binding.saveBtn.setTag(BTN_SAVE_TAG);
            binding.saveBtn.setOnClickListener(this);
        }


        if (Objects.isNull(navController)) {
            navController = NavHostFragment.findNavController(this);
        }

        if (Objects.isNull(acceptMissionViewModel)) {
            acceptMissionViewModel = new DeliverMissionViewModel();
        }

        List<User> dusers = new ArrayList<>();
        acceptMissionViewModel.getUsers(MainActivity.loggedInUser.getDeptId()).observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                dusers.clear();
                dusers.addAll(users);
            }
        });
        acceptMissionViewModel.getDatas(mission.getTaskId(), mission.getId().equals("-1")?1:0).observe(getViewLifecycleOwner(), new Observer<List<DeliverMssion>>() {
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


    @Override
    public void onClick(View view) {
        String tag = (String) view.getTag();

        if (tag.equals(BTN_CANCEL_TAG)) {
            navController.navigate(R.id.back_to_mission);
            return;
        }
        SaveMission saveMission = SaveMission.builder()
                .taskId(mission.getTaskId())

                .build();

        if (tag.equals(BTN_DELIVER_TAG)) {


        }
        if (tag.equals(BTN_SAVE_TAG)) {


            acceptMissionViewModel.saveSubMissions();
        }
        navController.navigate(R.id.back_to_mission);
    }
}