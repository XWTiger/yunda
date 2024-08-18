package com.tiger.yunda.ui.log;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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
import com.tiger.yunda.utils.FileUtil;
import com.tiger.yunda.utils.JsonUtil;
import com.tiger.yunda.utils.OpenFileUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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

    private List<Uri> handleFiles;

    private ChipGroup chipGroup;

    private List<Integer> deleteList;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

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

                            ViewGroup.MarginLayoutParams params  = new ViewGroup.MarginLayoutParams(300, 300);
                            params.bottomMargin = 8;// 你想要的margin值
                            params.leftMargin = 8;
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

        pickMultipleMedia =
                registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(4), uris -> {
                    // Callback is invoked after the user selects media items or closes the
                    // photo picker.
                    if (!uris.isEmpty()) {
                        Log.d("PhotoPicker", "Number of items selected: " + uris.size());
                        AtomicInteger index;
                        if (Objects.isNull(handleFiles)) {
                            handleFiles = new ArrayList<>();
                            deleteList = new ArrayList<>();
                            index  = new AtomicInteger();
                        } else {
                            index = new AtomicInteger(handleFiles.size());
                        }

                        uris.forEach(uri -> {
                            Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.chip_file, null);
                            chip.setText("恢复_" + index);
                            chip.setTag(index.get());
                            chip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int ind = (int) v.getTag();
                                    //handleFiles.remove(ind);
                                    deleteList.set(ind, 0);
                                    chipGroup.removeView(v);

                                }
                            });
                            handleFiles.add(uri);
                            deleteList.add(1);
                            chipGroup.addView(chip);
                            index.getAndIncrement();
                        });
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
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

    public void openFilePicker() {
        // For this example, launch the photo picker and let the user choose images
        // and videos. If you want the user to select a specific type of media file,
        // use the overloaded versions of launch(), as shown in the section about how
        // to select a single media item.
        pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    public void onClick(View v) {
        String tag = v.getTag().toString();
        if ("edit".equals(tag)) {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_change_score_layout); // 设置自定义布局
            dialog.setTitle("修改评分");

            // 找到布局中的EditText
            EditText input = dialog.findViewById(R.id.change_num);
            Button confirmButton = dialog.findViewById(R.id.change_finished);
            Button cancel = dialog.findViewById(R.id.change_cancel);
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String num = input.getText().toString();
                    Map<String, Object> params = new HashMap<>();
                    if (StringUtils.isBlank(num)) {
                        input.setError("分数不能为空");
                        Toast.makeText(getContext(), "分数不能为空" ,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Double.parseDouble(num) < 0 || Double.parseDouble(num) > 10) {
                        input.setError("分数在0-10之间");
                        Toast.makeText(getContext(), "分数在0-10之间" ,Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        input.setError(null);
                    }
                    params.put("id",  workLog.getId());
                    params.put("score", num);
                    workLogService.changeScore(params).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText( getContext(), "提交成功", Toast.LENGTH_SHORT).show();
                                // 关闭对话框
                                dialog.dismiss();
                                dismiss();
                            } else {
                                try {
                                    String errStr = response.errorBody().string();
                                    ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                                    Log.e("xiaweihu", "修改分数失败: ===========>" + errStr);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                            Log.e("xiaweihu", "修改分数失败: ===========>", throwable);
                        }
                    });
                }
            });

            cancel.setOnClickListener( ele -> {
                dialog.dismiss();
            });
            dialog.show();
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
            chipGroup = dialog.findViewById(R.id.file_group);
            ProgressBar bar = dialog.findViewById(R.id.progressBar);
            ImageButton imageButton = dialog.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFilePicker();
                }
            });
           /* Spinner spinner = dialog.findViewById(R.id.reject_location);
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(workLog.getSpinnerTypes(),getContext());
            spinner.setAdapter(spinnerAdapter);
            int position = spinner.getSelectedItemPosition();*/
            // 创建按钮来确认输入
            Button confirmButton = dialog.findViewById(R.id.reject_finished);
            Button cancel = dialog.findViewById(R.id.reject_cancel);
            dialog.show(); // 显示对话框
            confirmButton.setOnClickListener(ele -> {
                String inputText = input.getText().toString();
                if (StringUtils.isBlank(inputText)) {
                    input.setError("原因不能为空");
                    Toast.makeText(getContext(), "原因不能为空" ,Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    input.setError(null);
                }
                // 处理输入的文本
                Map<String, RequestBody> params = new HashMap<>();
                RequestBody id = RequestBody.create(MediaType.parse("text/plain"), workLog.getId());

                RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), inputText);


                params.put("SubtaskId", id);
                params.put("reason", desc);
                if (!CollectionUtil.isEmpty(handleFiles)) {
                    for (int i = 0; i < deleteList.size(); i++) {
                        if (deleteList.get(i) == 1) {
                            File file = FileUtil.getFileFromUri(handleFiles.get(i), getContext());
                            String filename = FileUtil.getFileStr(handleFiles.get(i), getContext());
                            RequestBody requestFile;
                            if (OpenFileUtil.isVideo(filename)) {
                                requestFile = RequestBody.create(MediaType.parse("video/" + OpenFileUtil.getFileExtension(filename)), file);
                            } else {
                                requestFile = RequestBody.create(MediaType.parse("image/" + OpenFileUtil.getFileExtension(filename)), file);
                            }

                            //注意：file就是与服务器对应的key,后面filename是服务器得到的文件名
                            params.put("Files\"; filename=\"" + filename, requestFile);
                        }
                    }

                }
                bar.setVisibility(View.VISIBLE);
                confirmButton.setEnabled(false);
                workLogService.reject(params).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText( getContext(), "提交成功", Toast.LENGTH_SHORT).show();
                            // 关闭对话框
                            confirmButton.setEnabled(true);
                            bar.setVisibility(View.GONE);
                            dialog.dismiss();
                            dismiss();
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