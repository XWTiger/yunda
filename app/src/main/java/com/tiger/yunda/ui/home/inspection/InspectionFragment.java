package com.tiger.yunda.ui.home.inspection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.tiger.yunda.MainActivity;

import com.tiger.yunda.R;
import com.tiger.yunda.data.BreakDownInfo;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.PageResult;
import com.tiger.yunda.databinding.FragmentInspectionBinding;
import com.tiger.yunda.service.InspectionService;
import com.tiger.yunda.service.MissionService;
import com.tiger.yunda.service.params.PauseParams;
import com.tiger.yunda.ui.common.BreakDownListDialogFragment;
import com.tiger.yunda.ui.common.CameraContentBean;
import com.tiger.yunda.ui.home.ListViewAdapter;
import com.tiger.yunda.ui.home.Mission;
import com.tiger.yunda.ui.home.inspection.placeholder.PlaceholderContent;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.DownLoadUtil;
import com.tiger.yunda.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 */
public class InspectionFragment extends Fragment implements View.OnClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int REQUEST_CODE_PERMISSIONS = 7;
    private String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    // TODO: Customize parameters
    private static Mission mission;

    public static BreakDownInfo breakDownInfo;

    private InspectionService inspectionService;

    private static String locationId;

    private NavController navController;

    private final String TV_PAUSE_VALUE = "暂停";

    private final String TV_CONTINUE_VALUE = "继续";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InspectionFragment() {
    }

    private BreakDownListDialogFragment breakDownListDialogFragment;
    com.tiger.yunda.databinding.FragmentInspectionBinding binding;
    LayoutInflater inflater;
    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static InspectionFragment newInstance(int columnCount) {
        InspectionFragment fragment = new InspectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Objects.isNull(inspectionService)) {
            inspectionService = MainActivity.retrofitClient.create(InspectionService.class);
        }
        if (Objects.isNull(breakDownInfo)) {
            breakDownInfo = new BreakDownInfo();
        }
        navController = NavHostFragment.findNavController(this);

        //查询任务
        if (getArguments() != null) {
            Mission tmeMission = (Mission) getArguments().getSerializable(ListViewAdapter.MISSION_KEY);
            if (Objects.nonNull(tmeMission)) {
                mission = tmeMission;
            }
            queryMissionDetail(mission.getId());
        }

        setHasOptionsMenu(true);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navController = NavHostFragment.findNavController(this);
        //navController.popBackStack();
        navController.navigate(R.id.to_navigation_mission);
        Log.i("xiaweihu", "onOptionsItemSelected: ========================" + item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        if (Objects.isNull(binding)) {
            binding = com.tiger.yunda.databinding.FragmentInspectionBinding.inflate(inflater, container, false);
            binding.inspectionFinished.setOnClickListener(this);
        } else {
            binding.inspectionAction.setText(null);
        }
        PlaceholderContent.addInspectionPositionData(mission);
        binding.inspTitle.setText("巡检对象:     " + mission.getInspectionUnit());
        ViewHolder viewHolder =  new ViewHolder(binding, PlaceholderContent.ITEMS);
       // NavController navController = NavHostFragment.findNavController(this);


        Bundle bundle = getArguments();
        if (Objects.nonNull(bundle)) {
            List<CameraContentBean> files = new ArrayList<>();
            List<CameraContentBean> handleFiles = new ArrayList<>();
            List<CameraContentBean> contentBeans = (List<CameraContentBean>) bundle.getSerializable("contents");
            if (Objects.isNull(breakDownListDialogFragment)) {
                breakDownListDialogFragment = BreakDownListDialogFragment.newInstance(2, breakDownInfo);
                breakDownInfo.setSubtaskId(mission.getId());
            }
            if (Objects.nonNull(contentBeans) && contentBeans.size() > 0) {
                breakDownInfo.setTypePosition(contentBeans.get(0).getBreakTypePosition());
                contentBeans.forEach(cameraContentBean -> {
                    if (cameraContentBean.isProblem()) {
                        files.add(cameraContentBean);
                    } else {
                        handleFiles.add(cameraContentBean);
                    }
                });
                if (contentBeans.get(0).isProblem()) {
                    if (Objects.nonNull(breakDownInfo) && !CollectionUtil.isEmpty(breakDownInfo.getFiles())) {
                        breakDownInfo.getFiles().addAll(files);
                    } else {
                        breakDownInfo.setFiles(files);
                    }
                } else {
                    if (Objects.nonNull(breakDownInfo) && !CollectionUtil.isEmpty(breakDownInfo.getHandleFiles())) {
                        breakDownInfo.getHandleFiles().addAll(handleFiles);
                    } else {
                        breakDownInfo.setHandleFiles(handleFiles);
                    }
                }
                breakDownInfo.setSubtaskId(mission.getId());
                breakDownInfo.setTrainLocationId(locationId);
                //TODO 确认逻辑是否正确
                breakDownListDialogFragment.show(getParentFragmentManager(), "breakdownReport");
            }

        }

        return binding.getRoot();
    }



    public static String getFileNameFromUri(Uri fileUri) {
        // 获取文件的完整路径
        String filePath = fileUri.getPath();
        // 获取文件名
        int lastSlashIndex = filePath.lastIndexOf('/');
        return lastSlashIndex != -1 ? filePath.substring(lastSlashIndex + 1) : filePath;
    }

    @Override
    public void onClick(View v) {
        //巡检任务完成

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("注意")
                .setMessage("已完成巡检，是否消除该防区门禁权限")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Call<ResponseBody> responseBodyCall = inspectionService.finishedMission(mission.getId());
                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "完成任务", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    navController.navigate(R.id.to_navigation_mission);
                                } else {
                                    try {
                                        String errStr = response.errorBody().string();
                                        ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                                        Log.e("xiaweihu", "完成任务失败: ===========>" + errStr);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

                            }
                        });

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    public class ViewHolder  implements View.OnClickListener {
        //状态，1:初始、2:已开始、3:已暂停、4:已恢复、5:已结束
        private int TRAIN_LOCATION_STATE_INIT = 1;
        private int TRAIN_LOCATION_STATE_STARTED = 2;
        private int TRAIN_LOCATION_STATE_PAUSED = 3;
        private int TRAIN_LOCATION_STATE_COVERED = 4;
        private int TRAIN_LOCATION_STATE_FINISHED = 5;
        @ColorInt
        public static final int GREEN       = 0xFF0B9D32;

        public LinearLayout linearLayout;
        public LinearLayout listchild;

        private FragmentInspectionBinding binding;
        public List<PlaceholderContent.PlaceholderItem> mItems;
        private Map<String, PlaceholderContent.PlaceholderItem> mapper = new HashMap<>();
        private Map<String, List<Button>> buttonMapper = new HashMap<>();



        public ViewHolder(com.tiger.yunda.databinding.FragmentInspectionBinding binding, List<PlaceholderContent.PlaceholderItem> mItems) {
            this.mItems = mItems;
            this.binding = binding;
            if (Objects.isNull(breakDownInfo)) {
                breakDownInfo = new BreakDownInfo();
            }


            if (Objects.isNull(this.linearLayout)) {

                LinearLayout linearLayout = binding.inspectionList;
                linearLayout.removeAllViews();
               // listchild = bindingList.inspectionList2;

                if (mItems.size() > 0) {
                    Log.i("tiger", "ViewHolder: ================ ");

                    for (int i = 0, mItemsSize = mItems.size(); i < mItemsSize; i++) {
                        PlaceholderContent.PlaceholderItem item = mItems.get(i);
                        mapper.put(item.id, item);

                        listchild = (LinearLayout) inflater.inflate(R.layout.fragment_inspection_list, linearLayout, false);
                        //inflater.inflate(R.id.item_train_position, listchild, false);btnItem.setEnabled(false);
                        //                ColorStateList colorStateList = ColorStateList.valueOf(Color.GRAY);
                        //                btnItem.setBackgroundTintList(colorStateList);

                        TextView view = (TextView) listchild.getChildAt(0);
                        Button startBtn = (Button) listchild.getChildAt(1);
                        Button pauseBtn = (Button) listchild.getChildAt(2);
                        Button finishBtn = (Button) listchild.getChildAt(3);
                        List<Button> buttons = new ArrayList<>();
                        buttons.add(startBtn);
                        buttons.add(pauseBtn);
                        buttons.add(finishBtn);
                        buttonMapper.put(item.id, buttons);
                        checkButtonsStateAndControl(item.state, startBtn, pauseBtn, finishBtn);

                        view.setText(item.content);
                        startBtn.setTag(item.id);
                        pauseBtn.setTag(item.id);
                        finishBtn.setTag(item.id);
                        startBtn.setOnClickListener(this);
                        pauseBtn.setOnClickListener(this);
                        finishBtn.setOnClickListener(this);
                        linearLayout.addView(listchild);
                    }


                }

            }


        }

        //状态，1:初始、2:已开始、3:已暂停、4:已恢复、5:已结束
        public void checkButtonsStateAndControl(int state,Button startBtn,Button pauseBtn, Button finishBtn) {
            ColorStateList colorStateList = ColorStateList.valueOf(Color.GRAY);
            ColorStateList green = ColorStateList.valueOf(GREEN);
            if (state == TRAIN_LOCATION_STATE_INIT) {
                //初始化
                pauseBtn.setEnabled(false);
                pauseBtn.setBackgroundTintList(colorStateList);
                finishBtn.setEnabled(false);
                finishBtn.setBackgroundTintList(colorStateList);
            }
            if (state == TRAIN_LOCATION_STATE_STARTED) {
                //已开始
                startBtn.setEnabled(false);
                startBtn.setBackgroundTintList(colorStateList);
                pauseBtn.setEnabled(true);
                pauseBtn.setBackgroundTintList(green);
                finishBtn.setEnabled(true);
                finishBtn.setBackgroundTintList(green);
            }

            if (state == TRAIN_LOCATION_STATE_PAUSED) {
                //已暂停
                startBtn.setEnabled(false);
                startBtn.setBackgroundTintList(colorStateList);
                pauseBtn.setEnabled(true);
                pauseBtn.setBackgroundTintList(green);
                pauseBtn.setText("继续");
                finishBtn.setEnabled(false);
                finishBtn.setBackgroundTintList(colorStateList);
            }

            if (state == TRAIN_LOCATION_STATE_COVERED) {
                //已恢复
                startBtn.setEnabled(false);
                startBtn.setBackgroundTintList(colorStateList);
                pauseBtn.setEnabled(true);
                pauseBtn.setBackgroundTintList(green);
                pauseBtn.setText("暂停");
                finishBtn.setEnabled(true);
                finishBtn.setBackgroundTintList(green);
            }

            if (state == TRAIN_LOCATION_STATE_FINISHED) {
                //已结束
                startBtn.setEnabled(false);
                startBtn.setBackgroundTintList(colorStateList);
                pauseBtn.setEnabled(false);
                pauseBtn.setBackgroundTintList(colorStateList);
                pauseBtn.setText("暂停");
                finishBtn.setEnabled(false);
                finishBtn.setBackgroundTintList(colorStateList);
            }
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            PlaceholderContent.PlaceholderItem item = mapper.get(v.getTag());
            breakDownInfo.setTrainLocationId(item.id);
            if (id == R.id.button2) {
                //开始
                Log.i("tiger", "onClick: button2 -----------> " + v.getTag());

                Call<ResponseBody>  responseBodyCall = inspectionService.startInspection(item.id);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == MissionService.HTTP_OK) {
                            binding.inspectionAction.setText("开始巡检: " + item.content);
                            item.state = TRAIN_LOCATION_STATE_STARTED;
                            List<Button> buttons = buttonMapper.get(v.getTag());
                            checkButtonsStateAndControl(TRAIN_LOCATION_STATE_STARTED, buttons.get(0), buttons.get(1), buttons.get(2));
                        } else {
                            try {
                                String errStr = response.errorBody().string();
                                ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                                Log.e("xiaweihu", "开始巡检失败: ===========>" + errStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        if (StringUtils.isNotBlank(throwable.getMessage())) {
                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            if (id == R.id.button3) {
                //暂停
                Log.i("tiger", "onClick: button3 -----------> " + v.getTag());
                Button pauseBtn = (Button) v;
                String content = pauseBtn.getText().toString();
                PauseParams params = PauseParams.builder()
                        .id((String) v.getTag())
                        .action(0).build();
                PlaceholderContent.PlaceholderItem placeholderItem =  mapper.get((String) v.getTag());
                if (TRAIN_LOCATION_STATE_PAUSED == placeholderItem.state) {//已暂停
                    params.setAction(2);
                } else {//已恢复
                    params.setAction(1);
                }
                locationId = placeholderItem.id;
                Call<ResponseBody>  responseBodyCall = inspectionService.pauseOrResumeInspection(params);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == MissionService.HTTP_OK) {
                            if (TRAIN_LOCATION_STATE_PAUSED == placeholderItem.state) {
                                binding.inspectionAction.setText("开始巡检: " + item.content);
                                item.state = TRAIN_LOCATION_STATE_COVERED;
                                List<Button> buttons = buttonMapper.get(v.getTag());
                                checkButtonsStateAndControl(TRAIN_LOCATION_STATE_COVERED, buttons.get(0), buttons.get(1), buttons.get(2));
                            } else {
                                binding.inspectionAction.setText("暂停巡检: " + item.content);
                                item.state = TRAIN_LOCATION_STATE_PAUSED;
                                List<Button> buttons = buttonMapper.get(v.getTag());
                                checkButtonsStateAndControl(TRAIN_LOCATION_STATE_PAUSED, buttons.get(0), buttons.get(1), buttons.get(2));
                                breakDownInfo.setTrainLocationId(placeholderItem.id);
                                breakDownInfo.setSubtaskId(mission.getId());
                                breakDownListDialogFragment = BreakDownListDialogFragment.newInstance(2, breakDownInfo);
                                breakDownListDialogFragment.show(getParentFragmentManager(), "breakdownReport");
                            }
                        } else {

                            try {
                                String errStr = response.errorBody().string();
                                ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                                Log.e("xiaweihu", "暂停巡检失败: ===========>" + errStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        if (StringUtils.isNotBlank(throwable.getMessage())) {
                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
            if (id == R.id.button4) {
                //结束
                Log.i("tiger", "onClick: button4 -----------> " + v.getTag());
                Call<ResponseBody>  responseBodyCall = inspectionService.finishedInspection(item.id);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == MissionService.HTTP_OK) {
                            binding.inspectionAction.setText(null);
                            item.state = TRAIN_LOCATION_STATE_FINISHED;
                            List<Button> buttons = buttonMapper.get(v.getTag());
                            checkButtonsStateAndControl(TRAIN_LOCATION_STATE_FINISHED, buttons.get(0), buttons.get(1), buttons.get(2));
                            Toast.makeText(getContext(),"结束成功", Toast.LENGTH_SHORT).show();
                        } else {

                            try {
                                String errStr = response.errorBody().string();
                                ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                                Log.e("xiaweihu", "暂停巡检失败: ===========>" + errStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        if (StringUtils.isNotBlank(throwable.getMessage())) {
                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }



        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public void setLinearLayout(LinearLayout linearLayout) {
            this.linearLayout = linearLayout;
        }

        public List<PlaceholderContent.PlaceholderItem> getmItems() {
            return mItems;
        }

        public void setmItems(List<PlaceholderContent.PlaceholderItem> mItems) {
            this.mItems = mItems;
        }
    }

    private void queryMissionDetail(String missionId) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Call<Mission> missionCall = inspectionService.querySubtaskDetail(missionId);
        MainActivity.threadPoolExecutor.execute(() -> {
            try {
                Response<Mission> response = missionCall.execute();
                if (response.isSuccessful()) {
                    mission = response.body();
                } else {
                    String errStr = null;
                    try {
                        errStr = response.errorBody().string();
                        ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                        Log.e("xiaweihu", "查询子任务详情: ===========>" + errStr);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                countDownLatch.countDown();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}