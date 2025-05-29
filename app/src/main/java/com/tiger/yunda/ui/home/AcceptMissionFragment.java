package com.tiger.yunda.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.DeliverMissionDTO;
import com.tiger.yunda.data.model.DeliverMssion;
import com.tiger.yunda.data.model.SaveMission;
import com.tiger.yunda.data.model.Train;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.databinding.DeliverMissionBinding;
import com.tiger.yunda.databinding.FragmentAcceptMissionBinding;
import com.tiger.yunda.ui.home.viewmodel.DeliverMissionAdapter;
import com.tiger.yunda.ui.home.viewmodel.DeliverMissionViewModel;
import com.tiger.yunda.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//派发任务界面
public class AcceptMissionFragment extends Fragment implements View.OnClickListener {

    private FragmentAcceptMissionBinding binding;

    private DeliverMissionBinding deliverMissionBinding;

    private DeliverMissionViewModel acceptMissionViewModel;

    private Mission mission;

    private NavController navController;

    public static String BTN_CANCEL_TAG = "cancel";

    public static String BTN_DELIVER_TAG = "deliver";
    public static String BTN_SAVE_TAG = "save";

    private List<Train> positionList;

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

        setHasOptionsMenu(true);
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
            acceptMissionViewModel = new DeliverMissionViewModel(getContext());
        }
        acceptMissionViewModel.getPositions().observe(getViewLifecycleOwner(), new Observer<List<Train>>() {
            @Override
            public void onChanged(List<Train> trains) {
                positionList = trains;
            }
        });



        List<User> dusers = new ArrayList<>();
        acceptMissionViewModel.getUsers(MainActivity.loggedInUser.getDeptId()).observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                dusers.clear();
                dusers.addAll(users);
                acceptMissionViewModel.getDatas(mission.getTaskId(), mission.getId().equals("-1")?1:0).observe(getViewLifecycleOwner(), new Observer<List<DeliverMssion>>() {
                    @Override
                    public void onChanged(List<DeliverMssion> deliverMssions) {
                        DeliverMissionAdapter deliverMissionAdapter = new DeliverMissionAdapter(getContext(), binding.listItem.getId(),  deliverMssions, dusers, positionList);
                        binding.listItem.setAdapter(deliverMissionAdapter);
                    }
                });
            }
        });


        return binding.getRoot();
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
            //取消
            navController.popBackStack();
            return;
        }
        DeliverMissionAdapter adapter = (DeliverMissionAdapter) binding.listItem.getAdapter();
        List<DeliverMssion> subList = adapter.getDeliverMissions();
        if (CollectionUtil.isEmpty(subList)) {
            Toast.makeText(getContext(), "子任务列表为空", Toast.LENGTH_SHORT).show();
            navController.popBackStack();
            return;
        }
        SaveMission saveMission = SaveMission.builder()
                .taskId(mission.getTaskId())
                .action(1).build();
        List<DeliverMissionDTO> subMissions = new ArrayList<>();
        subList.forEach(deliverMssion -> {
            DeliverMissionDTO deliverMissionDTO = DeliverMissionDTO.builder()
                    .inspectorId(deliverMssion.getInspectorId().get())
                    .positionId(deliverMssion.getPositionId().get())
                    .inspector(deliverMssion.getInspector().get())
                    .positionName(deliverMssion.getPositionName().get())
                    .inspectionUnit(deliverMssion.getInspectionUnit().get())
                    .duration(deliverMssion.getDuration().get())
                    .build();
            subMissions.add(deliverMissionDTO);
        });
        saveMission.setSubtasks(subMissions);

        if (tag.equals(BTN_DELIVER_TAG)) {
            saveMission.setAction(1);
            if (acceptMissionViewModel.saveSubMissions(saveMission)) {
                Toast.makeText(getContext(), "派发成功", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.back_to_mission);
            } else {
                Toast.makeText(getContext(), "派发失败", Toast.LENGTH_SHORT).show();
            }
        }
        if (tag.equals(BTN_SAVE_TAG)) {
            saveMission.setAction(2);

            if (acceptMissionViewModel.saveSubMissions(saveMission)) {
                Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.back_to_mission);
            } else {
                Toast.makeText(getContext(), "保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


}