package com.tiger.yunda.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.adapters.AdapterViewBindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loper7.date_time_picker.DateTimeConfig;
import com.loper7.date_time_picker.DateTimePicker;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;
import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.CreateMission;
import com.tiger.yunda.data.model.Train;
import com.tiger.yunda.data.model.TrainPlace;
import com.tiger.yunda.databinding.FragmentCreateMissionBinding;
import com.tiger.yunda.databinding.FragmentTrainPlaceBinding;
import com.tiger.yunda.ui.common.SpinnerAdapter;
import com.tiger.yunda.ui.common.TrainListDialogFragment;
import com.tiger.yunda.ui.home.viewmodel.CreateMissionViewModel;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.TimeUtil;

import org.apache.commons.lang3.StringUtils;

import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateMissionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateMissionFragment extends Fragment implements View.OnClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private static final String START_TIME_TAG = "start_time";
    private static final String END_TIME_TAG = "end_time";

    private static final String ACTION_TRAIN_ADD = "train_add";

    private static final String ACTION_TRAIN_SUB = "train_sub";
    private static final String ACTION_FINISHED = "finished";

    private String mParam1;
    private String mParam2;

    private TrainListDialogFragment trainListDialogFragment;
    private FragmentCreateMissionBinding binding;


    //private DateTimePicker dateTimePicker;

    //private DateTimePicker dateTimePickerEnd;

    private ImageButton startTimeButton;
    private TextView startTimeTV;

    private ImageButton endTimeButton;
    private TextView endTimeTV;

    private List<Train> trainList;

     private List<Train> positionList;

    private CreateMissionViewModel createMissionViewModel;

    private String startTime;

    private String endTime;

    private NavController navController;

    final String[] trainNo = {""};
    final String[] trainPlace = {""};


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateMissionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateMissionFragment newInstance(String param1, String param2) {
        CreateMissionFragment fragment = new CreateMissionFragment();
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
        navController = NavHostFragment.findNavController(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateMissionBinding.inflate(inflater, container, false);
        startTimeButton = binding.selectStartTime;
        startTimeButton.setTag(START_TIME_TAG);
        startTimeTV = binding.textStartTime;
        endTimeButton = binding.selectFinishedTime;
        endTimeButton.setTag(END_TIME_TAG);
        endTimeTV = binding.textFinishedTime;
        binding.trainNoAdd.setOnClickListener(this);
        binding.trainNoAdd.setTag(ACTION_TRAIN_ADD);
        binding.trainNoSub.setTag(ACTION_TRAIN_SUB);
        binding.trainNoSub.setOnClickListener(this);

        binding.finishedCreation.setTag(ACTION_FINISHED);
        binding.finishedCreation.setOnClickListener(this);
        if (Objects.isNull(createMissionViewModel)) {
            createMissionViewModel = new CreateMissionViewModel(getContext());
        }
        createMissionViewModel.getCreation().observe(getViewLifecycleOwner(), new Observer<CreateMission>() {
            @Override
            public void onChanged(CreateMission createMission) {
                binding.setCreation(createMission);
            }
        });
        createMissionViewModel.getTrains().observe(getViewLifecycleOwner(), new Observer<List<Train>>() {
            @Override
            public void onChanged(List<Train> trains) {
                trainList = trains;
            }
        });

        createMissionViewModel.getPositions().observe(getViewLifecycleOwner(), new Observer<List<Train>>() {
            @Override
            public void onChanged(List<Train> trains) {
                positionList = trains;
            }
        });

        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CardDatePickerDialog.Builder(getContext())
                        .setTitle("请选择开始时间")
                        .setLabelText("年","月","日","时","分", "秒")
                        .setDisplayType(Arrays.asList(
                                DateTimeConfig.YEAR,//显示年
                                DateTimeConfig.MONTH,//显示月
                                DateTimeConfig.DAY,//显示日
                                DateTimeConfig.HOUR,//显示时
                                DateTimeConfig.MIN))//显示分
                        .setOnChoose("选择", aLong -> {
                            //aLong  = millisecond
                            Date start = new Date(aLong);
                            startTime = TimeUtil.getDateStringFromMs(aLong);
                            startTimeTV.setText(startTime);
                            startTimeTV.setVisibility(View.VISIBLE);
                            return null;
                        })
                        .setOnCancel("取消", () -> {
                            return null;
                        })
                        .build().show();
            }
        });


        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CardDatePickerDialog.Builder(getContext())
                        .setTitle("请选择结束时间")
                        .setLabelText("年","月","日","时","分", "秒")
                        .setDisplayType(Arrays.asList(
                                DateTimeConfig.YEAR,//显示年
                                DateTimeConfig.MONTH,//显示月
                                DateTimeConfig.DAY,//显示日
                                DateTimeConfig.HOUR,//显示时
                                DateTimeConfig.MIN))//显示分
                        .setOnChoose("选择", aLong -> {
                            //aLong  = millisecond
                            Date start = new Date(aLong);
                            endTime = TimeUtil.getDateStringFromMs(aLong);
                            endTimeTV.setText(endTime);
                            endTimeTV.setVisibility(View.VISIBLE);
                            return null;
                        })
                        .setOnCancel("取消", () -> {
                            return null;
                        })
                        .build().show();
            }
        });

        /* return inflater.inflate(R.layout.fragment_create_mission, container, false);*/
        return binding.getRoot();
    }


    public void onItemClicked(String text) {
        CreateMission createMission =  binding.getCreation();
        createMission.addOneTrain(text);
        trainListDialogFragment.dismiss();
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();

        if (ACTION_TRAIN_ADD.equals(tag)) {
            /*trainListDialogFragment = TrainListDialogFragment.newInstance(trainList);
            trainListDialogFragment.setCreateMissionFragment(this);
            trainListDialogFragment.show(getFragmentManager(), "select_train");
            if (StringUtils.isNotBlank(binding.trainNoText.getText().toString())) {
                binding.trainNoSub.setVisibility(View.VISIBLE);
            } else {
                binding.trainNoSub.setVisibility(View.INVISIBLE);
            }*/

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.fragment_train_place, null);
            FragmentTrainPlaceBinding trainPlaceBinding = FragmentTrainPlaceBinding.bind(view);


            //车位置
            SpinnerAdapter spinnerPlaceAdapter = new SpinnerAdapter(CollectionUtil.covertTrainToSpinnerObj(positionList), getContext());
            trainPlaceBinding.trainPlace.setAdapter(spinnerPlaceAdapter);
            //trainPlaceBinding.trainPlace.setSelection();

            trainPlaceBinding.trainPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    trainPlace[0] = String.valueOf(positionList.get(position).getValue());
                    //调用接口 获取车列位

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //车号
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(CollectionUtil.covertTrainToSpinnerObj(trainList), getContext());
            trainPlaceBinding.trainNoSp.setAdapter(spinnerAdapter);
            trainPlaceBinding.trainNoSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    trainNo[0] = trainList.get(position).getText();
                    //调用接口 获取车列位
                    createMissionViewModel.getPositionByTrainNo(trainNo[0]).observe(getViewLifecycleOwner(), new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            if (Objects.nonNull(integer) && integer > 0) {
                                for (int i = 0; i < positionList.size(); i++) {
                                    if (positionList.get(i).getValue() == integer) {
                                        trainPlaceBinding.trainPlace.setSelection(i);
                                        trainPlace[0] = String.valueOf(positionList.get(i).getValue());
                                        break;
                                    }
                                }
                            }
                        }
                    });

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            CreateMission createMission =  binding.getCreation();
            createMission.addTrain(new TrainPlace(trainList.get(0).getText(), null, positionList.get(0).getValue()));
            binding.trainDetailLayout.addView(view);



        }
        if (ACTION_TRAIN_SUB.equals(tag)) {
            CreateMission createMission =  binding.getCreation();
            int count = binding.trainDetailLayout.getChildCount();
            createMission.subTrain(count - 1);
            binding.trainDetailLayout.removeViewAt(count - 1);

        }

        if (ACTION_FINISHED.equals(tag)) {
            CreateMission createMission =  binding.getCreation();
            createMission.covertTrainNoStr();
            if (StringUtils.isBlank(createMission.getName())) {
                binding.missionName.setError("任务名称不能为空");
                return;
            } else {
                binding.missionName.setError(null);
            }
            if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
                Toast.makeText(getContext(), "请选择时间", Toast.LENGTH_SHORT).show();
                return;
            }
            if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
                long start = TimeUtil.getMillionByYYYYMMMDDString(startTime);
                long end = TimeUtil.getMillionByYYYYMMMDDString(endTime);
                if (start > end) {
                    Toast.makeText(getContext(), "开始时间不能晚于结束时间", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            if (CollectionUtil.isEmpty(createMission.getTrains())) {
                Toast.makeText(getContext(), "请选择作业车辆或者作业位置", Toast.LENGTH_SHORT).show();
                return;
            }



            createMission.setPlanStartTime(startTime);
            createMission.setPlanEndTime(endTime);
            createMissionViewModel.createMission(createMission).observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    Mission mission = new Mission();
                    mission.setId("-1");
                    mission.setTaskId(s);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(ListViewAdapter.MISSION_KEY, mission);
                    navController.navigate(R.id.to_deliver_mission, bundle);
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
        Log.i("xiaweihu", "onOptionsItemSelected: ========================" + item.getItemId());
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}