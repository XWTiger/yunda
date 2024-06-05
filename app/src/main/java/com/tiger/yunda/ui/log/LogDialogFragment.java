package com.tiger.yunda.ui.log;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.Faults;
import com.tiger.yunda.data.model.WorkLog;
import com.tiger.yunda.databinding.FragmentDetailLogDialogItemBinding;
import com.tiger.yunda.databinding.FragmentDetailLogDialogBinding;
import com.tiger.yunda.enums.RoleType;
import com.tiger.yunda.service.WorkLogService;
import com.tiger.yunda.ui.common.SpinnerAdapter;
import com.tiger.yunda.ui.home.TrainLocations;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.JsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     LogDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class LogDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_DETAIL = "wroker_detail";
    private FragmentDetailLogDialogBinding binding;
    private static WorkLog workLog;
    private WorkLogService workLogService;

    // TODO: Customize parameters
    public static LogDialogFragment newInstance(WorkLog workLog) {
        final LogDialogFragment fragment = new LogDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_DETAIL, workLog);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (Objects.nonNull(bundle)) {
            WorkLog logs = (WorkLog) bundle.getSerializable(ARG_ITEM_DETAIL);
            workLog = logs;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDetailLogDialogBinding.inflate(inflater, container, false);
        if (Objects.isNull(workLogService)) {
            workLogService = MainActivity.retrofitClient.create(WorkLogService.class);
        }
        if (Objects.nonNull(workLog)) {
            binding.setLog(workLog);
            if (CollectionUtil.isEmpty(workLog.getTrainLocations())) {
                binding.dealStatus.setText("否");
            } else {
                boolean deal = false;
                for (TrainLocations trainLocations : workLog.getTrainLocations()) {
                    if (trainLocations.getState() == 2) {// 已处理
                        deal = true;
                    } else {
                        deal = false;
                    }
                }
                if (deal) {
                    binding.dealStatus.setText("是");
                } else {
                    binding.dealStatus.setText("否");
                }
            }
            if (!CollectionUtil.isEmpty(workLog.getFaults())) {
                List<Faults> faults = workLog.getFaults();

                faults.forEach(fault -> {
                    if (!CollectionUtil.isEmpty(fault.getAttachments())) {
                        fault.getAttachments().forEach(attachments -> {
                            Uri uri = Uri.parse(attachments.getUrl());
                            ImageView imageView = new ImageView(getContext());
                            ViewGroup.LayoutParams params  = new ViewGroup.LayoutParams(300, 300);

                            imageView.setLayoutParams(params);
                            binding.problemDetail.addView(imageView);
                            Glide.with(this).load(uri).into(imageView);
                        });
                    }

                });
            }
        }
        binding.btnEdit.setTag("edit");
        binding.btnYes.setTag("yes");
        binding.btnReject.setTag("reject");
        binding.btnCancle.setTag("cancel");
        binding.btnYes.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.btnReject.setOnClickListener(this);
        binding.btnCancle.setOnClickListener(this);
        if (MainActivity.loggedInUser.getRole() == RoleType.WORKER_LEADER) {
            binding.btnReject.setVisibility(View.GONE);
        } else {
            binding.btnYes.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.GONE);
        }
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      /*  final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new detailAdapter((BreakRecord) getArguments().getSerializable(ARG_ITEM_DETAIL)));*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        String tag = v.getTag().toString();
        if ("edit".equals(tag)) {

        }
        if ("yes".equals(tag)) {
            dismiss();
        }

        if ("reject".equals(tag)) {
            // 创建一个Dialog
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_reject_layout); // 设置自定义布局
            dialog.setTitle("申述");

            // 找到布局中的EditText
            EditText input = dialog.findViewById(R.id.reject_reason);
            Spinner spinner = dialog.findViewById(R.id.reject_location);
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(workLog.getSpinnerTypes(),getContext());
            spinner.setAdapter(spinnerAdapter);
            int position = spinner.getSelectedItemPosition();
            // 创建按钮来确认输入
            Button confirmButton = dialog.findViewById(R.id.reject_finished);
            Button cancel = dialog.findViewById(R.id.reject_cancel);
            dialog.show(); // 显示对话框
            confirmButton.setOnClickListener(ele -> {
                String inputText = input.getText().toString();
                // 处理输入的文本
                Map<String, Object> params = new HashMap<>();
                params.put("trainLocationId", workLog.getTrainLocations().get(position).getId());
                params.put("reason", inputText);
                workLogService.reject(params).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText( getContext(), "提交成功", Toast.LENGTH_SHORT).show();
                            // 关闭对话框
                            dialog.dismiss();
                        } else {
                            try {
                                String errStr = response.errorBody().string();
                                ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                                Log.e("xiaweihu", "查询作业详情失败: ===========>" + errStr);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        Log.e("xiaweihu", "提交申诉失败详情失败: ===========>", throwable);
                    }
                });


            });

            cancel.setOnClickListener( ele -> {
                dialog.dismiss();
            });


        }
        if ("cancel".equals(tag)) {
            dismiss();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(FragmentDetailLogDialogItemBinding binding) {
            super(binding.getRoot());
            text = binding.text;
        }

    }


}