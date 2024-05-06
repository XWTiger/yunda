package com.tiger.yunda.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.DiffUtil;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.databinding.FragmentHomeBinding;
import com.tiger.yunda.databinding.LayoutMissionBinding;
import com.tiger.yunda.enums.RoleType;
import com.tiger.yunda.service.MissionService;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.JsonUtil;
import com.tiger.yunda.utils.TimeUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListViewAdapter extends ArrayAdapter<Mission> implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    public static String MISSION_KEY = "mission";
    private static final int MISSION_STATE_DELIVERING = 1;//下发中
    private static final int MISSION_STATE_DELIVERED = 2; //已下发
    private static final int MISSION_STATE_INSPECTION = 3; //巡检中
    private static final int MISSION_STATE_FINISHED = 4; //完成
    private static final int MISSION_STATE_ACCEPTED = 5; //已接受
    private Context context;
    private NavController navController;
    private FragmentManager fragmentManager;

    private Activity activity;

    private List<Mission> objects;

    private Button acceptAll;
    private MissionService missionService;

    private MissionViewModel missionViewModel;
    private MissionFragment missionFragment;



    private int[]  checkedArr;

    public ListViewAdapter(@NonNull Context context, int resource,@NonNull List<Mission> objects, Activity activity, MissionViewModel missionViewModel) {
        super(context, resource, objects);
        this.context = context;
        this.activity = activity;
        this.objects = objects;
        checkedArr = new int[objects.size()];
        this.missionViewModel = missionViewModel;
        missionService = MainActivity.retrofitClient.create(MissionService.class);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null ) {

            LayoutMissionBinding binding = LayoutMissionBinding.inflate(inflater, parent, false);

            convertView = binding.getRoot();

            TextView tvItem = binding.editTextTextMultiLine;
            Button btnItem = binding.buttonAccept;
            btnItem.setTag(position);
            Button btnReject = binding.buttonReject;
            btnReject.setTag(position);
            CheckBox checkBox = binding.checkBox;
            checkBox.setTag(position);
            checkBox.setOnCheckedChangeListener(this);
            Button inspectionBtn = binding.buttonInspection;
            inspectionBtn.setTag(position);
            Mission mission =  objects.get(position);
            if (Objects.nonNull(mission.getMasterMission()) && mission.getMasterMission()) {
                checkBox.setEnabled(false);
            }

            int state = objects.get(position).getState();
            if ((state == MISSION_STATE_ACCEPTED || MISSION_STATE_INSPECTION == state) && StringUtils.isNotBlank(objects.get(position).getId())) {
                btnReject.setVisibility(View.GONE);
                btnItem.setEnabled(false);
                ColorStateList colorStateList = ColorStateList.valueOf(Color.GRAY);
                btnItem.setBackgroundTintList(colorStateList);
                inspectionBtn.setVisibility(View.VISIBLE);
                inspectionBtn.setText("巡检");

            }
            if (state == MISSION_STATE_FINISHED && StringUtils.isNotBlank(objects.get(position).getId())) {
                btnItem.setEnabled(false);
                ColorStateList colorStateList = ColorStateList.valueOf(Color.GRAY);
                btnItem.setBackgroundTintList(colorStateList);
            }

            if (state == MISSION_STATE_DELIVERED && StringUtils.isBlank(objects.get(position).getId())) {
                /// 待下发 主任务
                btnItem.setText("派发");
            }

            if ((state >= MISSION_STATE_INSPECTION ) && StringUtils.isBlank(objects.get(position).getId())) {
                //已下发 巡检中  已完成 6 已作废
                btnItem.setEnabled(false);
                ColorStateList colorStateList = ColorStateList.valueOf(Color.GRAY);
                btnItem.setBackgroundTintList(colorStateList);
                if (state == MISSION_STATE_INSPECTION) {
                    btnItem.setText("已下发");
                }
                if (state == MISSION_STATE_FINISHED) {
                    btnItem.setText("巡检中");
                }
                if (state == MISSION_STATE_ACCEPTED) {
                    btnItem.setText("已完成");
                }
            }

            //点击巡检按钮
            inspectionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MISSION_KEY, objects.get(position));
                    Call<ResponseBody> responseBodyCall = missionService.inspection(objects.get(position).getId());
                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == MissionService.HTTP_OK) {
                                getNavController().navigate(R.id.to_inspection_mission, bundle);
                            } else {

                                try {
                                    String errStr = response.errorBody().string();
                                    ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                                    Log.e("xiaweihu", "绑定设备失败: ===========>" + errStr);
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
            });

            btnItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 接受事件
                    Log.d("tiger", "onClick: ======================= ");
                    if (Objects.isNull(MainActivity.loggedInUser) || MainActivity.loggedInUser.getRole() == RoleType.WORKER) {
                        AppCompatActivity ac = (AppCompatActivity) activity;
                        ActionBar actionBar = ac.getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setDisplayShowCustomEnabled(false);
                            actionBar.setDisplayShowTitleEnabled(true);
                        }
                    }

                    int position = (int) v.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MISSION_KEY, objects.get(position));
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("系统提示")
                            .setMessage("确认要领取该任务吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    // 点击“OK”按钮后的操作
                                    if (StringUtils.isNotBlank(objects.get(position).getId())) {
                                        Boolean result = missionViewModel.acceptMission(objects.get(position).getId());
                                        //巡检员
                                        if (result) {
                                            /*getNavController().navigate(R.id.to_inspection_mission, bundle);*/
                                            missionViewModel.getData(1, 30, null, null, false);
                                        } else {
                                            Toast.makeText(context, "接受任务失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //to_accept_mission
                                    if (StringUtils.isNotBlank(objects.get(position).getTaskId()) && StringUtils.isBlank(objects.get(position).getId())) {
                                        //工班长 的主任务
                                        getNavController().navigate(R.id.to_accept_mission, bundle);
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“Cancel”按钮后的操作
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });


            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 拒绝事件
                    int position = (int) v.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MISSION_KEY, objects.get(position));
                    Log.d("tiger", "onClick: ======================= ");
                    // 例如：Toast.makeText(context, "Button clicked for " + item, Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("系统提示")
                            .setMessage("确认要拒绝该任务吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“OK”按钮后的操作
                                    Toast.makeText(context, "点击确定", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“Cancel”按钮后的操作
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }
            });
             viewHolder = new ViewHolder(tvItem, btnItem, btnReject, checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TextView textView = viewHolder.getTvItem();
        final Mission item = getItem(position);
        textView.setText(item.getInspectionUnit());
        textView.setTooltipText(item.getInspectionUnit());
        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (int) buttonView.getTag();
        if (isChecked) {
            checkedArr[position] = 1;
        } else {
            checkedArr[position] = 0;
        }
        boolean allZero = TimeUtil.checkAllZero(checkedArr);
        if (!allZero && (acceptAll.getVisibility() == View.INVISIBLE || acceptAll.getVisibility() == View.GONE)) {
            acceptAll.setVisibility(View.VISIBLE);
        }
        if (allZero) {
            acceptAll.setVisibility(View.INVISIBLE);
        }
    }


    public Button getAcceptAll() {
        return acceptAll;
    }

    public void setAcceptAll(Button acceptAll) {
        this.acceptAll = acceptAll;
        acceptAll.setOnClickListener(this);
    }

    public NavController getNavController() {
        return navController;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }


    //一键接受子任务
    @Override
    public void onClick(View view) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < checkedArr.length; i++) {
            if (checkedArr[i] == 1) {
                if (StringUtils.isNotBlank(objects.get(i).getId())) {
                    ids.add(objects.get(i).getId());
                }
                checkedArr[i] = 0;
            }
        }
        LiveData<Boolean> result = missionViewModel.acceptMissions(ids);
        AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
        result.observe(appCompatActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(context, "接受任务成功", Toast.LENGTH_SHORT).show();
                    acceptAll.setVisibility(View.INVISIBLE);
                    missionViewModel.getData(1, 30, null, null, false);
                } else {
                    Toast.makeText(context, "接受任务失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class ViewHolder  {
        TextView tvItem;
        Button btnItem ;
        Button btnReject ;

        public ViewHolder(TextView tvItem, Button btnItem, Button btnReject, CheckBox checkBox) {
            this.tvItem = tvItem;
            this.btnItem = btnItem;
            this.btnReject = btnReject;
            this.checkBox = checkBox;
        }

        CheckBox checkBox ;

        public TextView getTvItem() {
            return tvItem;
        }

        public void setTvItem(TextView tvItem) {
            this.tvItem = tvItem;
        }
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public List<Mission> getObjects() {
        return objects;
    }

    public void setObjects(List<Mission> updateObjets) {
        if (!CollectionUtil.isEmpty(updateObjets)) {
            /*updateObjets.forEach(mission -> {
                if (!this.objects.contains(mission)) {
                    this.objects.add(mission);
                }
            });*/
            this.objects = updateObjets;
        }
        checkedArr = new int[objects.size()];
    }


}

