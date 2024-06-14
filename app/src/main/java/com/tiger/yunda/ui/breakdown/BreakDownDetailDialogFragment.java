package com.tiger.yunda.ui.breakdown;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;
import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.Attachments;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.databinding.FragmentBreakDownDetailDialogBinding;
import com.tiger.yunda.enums.CameraFileType;
import com.tiger.yunda.service.BreakDownService;
import com.tiger.yunda.ui.common.SpinnerAdapter;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.FileUtil;
import com.tiger.yunda.utils.JsonUtil;
import com.tiger.yunda.utils.OpenFileUtil;
import com.tiger.yunda.utils.TimeUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
 *     BreakDownDetailDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class BreakDownDetailDialogFragment  extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // TODO: Customize parameter argument names
    public static final String ARG_ITEM_DETAIL = "wroker_detail";
    private static final int BREAK_DEAL_UNDO = 1; //未处理
    private static final int BREAK_DEAL_FINISHED = 2; //已处理
    private FragmentBreakDownDetailDialogBinding binding;

    private BreakDownViewModel breakDownViewModel;

    private NavController navHostController;

    private List<User> userList;

    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

    private User selectedUser;

    private Date startDateTime;
    private Boolean finisehd = true;

    private BreakRecord breakRecordGlobal;

    private ChipGroup chipGroup;

    private List<Uri> handleFiles;
    private BreakDownService breakDownService;



    private static volatile BreakDownDetailDialogFragment breakDownListDialogFragment ;


    // TODO: Customize parameters
    public static BreakDownDetailDialogFragment newInstance(BreakRecord breakRecord) {
        breakDownListDialogFragment  = new BreakDownDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_DETAIL, breakRecord);
        breakDownListDialogFragment.setArguments(args);
        return breakDownListDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentBreakDownDetailDialogBinding.inflate(inflater, container, false);
        if (Objects.isNull(breakDownViewModel)) {
            breakDownViewModel = new BreakDownViewModel(getContext());
        }
        BreakRecord breakRecord = (BreakRecord) getArguments().getSerializable(ARG_ITEM_DETAIL);
        breakRecordGlobal = breakDownViewModel.getBreakRecordById(breakRecord.getId());
        if (Objects.nonNull(breakRecordGlobal)) {
            breakRecord = breakRecordGlobal;
        } else {
            breakRecordGlobal = breakRecord;
        }
        navHostController = NavHostFragment.findNavController(this);
        //加载图片
        List<Attachments> attachments = breakRecord.getAttachments();
        if (!CollectionUtil.isEmpty(attachments)) {

            AtomicInteger index = new AtomicInteger();
            attachments.forEach(attachment -> {
                Uri uri = Uri.parse(attachment.getUrl());
                ImageView imageView = new ImageView(getContext());

                ViewGroup.LayoutParams params  = new ViewGroup.LayoutParams(300, 300);
                imageView.setLayoutParams(params);

                imageView.setOnClickListener(this);
                binding.contentLayout.addView(imageView);
                if (".mp4".equals(attachment.getExt())) {
                    imageView.setTag("video_" + index.get() );
                    imageView.setImageResource(R.drawable.ic_video_p2);
                } else {
                    imageView.setTag("img_" + index.get());
                    Glide.with(this).load(uri).into(imageView);
                }



                index.getAndIncrement();
            });


        }
        breakDownViewModel.getUsers(MainActivity.loggedInUser.getDeptId()).observe(getViewLifecycleOwner(), users -> {
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(CollectionUtil.covertUserToSpinnerObj(users), getContext());
            binding.spinnerPerson.setAdapter(spinnerAdapter);
            binding.spinnerPerson.setOnItemSelectedListener(this);
            userList = users;
        });

        if (Objects.isNull(breakDownService)) {
            breakDownService = MainActivity.retrofitClient.create(BreakDownService.class);
        }

        binding.setDetail(breakRecord);
        /*binding.addDealtime.setTag("check_time");
        binding.addDealtime.setOnClickListener(this);*/
        binding.btnEdit.setTag("edit");
        binding.btnEdit.setOnClickListener(this);
        binding.btnCancle.setTag("cancel");
        binding.btnCancle.setOnClickListener(this);
        binding.btnYes.setTag("ok");
        binding.btnYes.setOnClickListener(this);
        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
        if (breakRecord.getState() == BREAK_DEAL_FINISHED) {
            binding.btnEdit.setVisibility(View.INVISIBLE);
        } else {
            finisehd = false;
        }

        chipGroup = binding.fileGroup;

        pickMultipleMedia =
                registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(4), uris -> {
                    // Callback is invoked after the user selects media items or closes the
                    // photo picker.
                    if (!uris.isEmpty()) {
                        Log.d("PhotoPicker", "Number of items selected: " + uris.size());
                        AtomicInteger index;
                        if (Objects.isNull(handleFiles)) {
                            handleFiles = new ArrayList<>();
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
                                    handleFiles.remove(ind);
                                    chipGroup.removeView(v);
                                }
                            });
                            handleFiles.add(uri);
                            chipGroup.addView(chip);
                            index.getAndIncrement();
                        });
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
        setHasOptionsMenu(true);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

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
        String cation = (String) v.getTag();
        String[] array = null;
        if (cation.startsWith("img")) {
           array = cation.split("_");
            cation = "img";
        }
        if (cation.startsWith("video")) {
            array = cation.split("_");
            cation = "video";
        }
        switch (cation) {

            case "check_time":
                new CardDatePickerDialog.Builder(v.getContext())
                        .setTitle("请选择处理时间时间")
                        .setLabelText("年", "月", "日", "时", "分", "秒")
                        .setOnChoose("选择", aLong -> {
                            //aLong  = millisecond
                            Date start = new Date(aLong);
                            startDateTime = start;
                           // binding.dealTime.setText(TimeUtil.getSTrFromMs(start));
                            return null;
                        })
                        .setOnCancel("取消", () -> {
                            return null;
                        })
                        .build().show();
                break;
            case "edit":
                //binding.addDealtime.setVisibility(View.VISIBLE);
                binding.dealPerson.setVisibility(View.GONE);
                binding.addDesc.setVisibility(View.VISIBLE);
                binding.addResolveFiles.setVisibility(View.VISIBLE);
                binding.spinnerPerson.setVisibility(View.VISIBLE);
                break;
            case "cancel":
                navHostController.popBackStack();
                break;
            case "ok":
                if (finisehd) {
                    navHostController.popBackStack();
                } else {
                    //添加未处理逻辑 提交编辑的信息
                    //判断处理时间
                    // 故障处理人
                    String describe = binding.desc.getText().toString();
                    if (StringUtils.isBlank(describe)) {
                        Toast.makeText(getContext(), "描述不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int pindex = binding.spinnerPerson.getSelectedItemPosition();
                    User user = userList.get(pindex);
                    Map<String, RequestBody> params = new HashMap<>();
                    RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), breakRecordGlobal.getId());
                    params.put("id", requestBody);
                    RequestBody desc = RequestBody.create(MediaType.parse("text/plain"), describe);
                    params.put("HandleDesc", desc);
                    params.put("HandleUserId", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(user.getValue())));

                    if (!CollectionUtil.isEmpty(handleFiles)) {
                        handleFiles.forEach( ele -> {
                          /*  String path = ele.getEncodedPath();
                            //File file = FileUtil.uri2File( ele, getContext(), false);
                            path = Uri.decode(path);*/
                            File file = FileUtil.getFileFromUri(ele, getContext());
                            String filename = FileUtil.getFileStr(ele, getContext());
                            RequestBody requestFile;


                           /* if (cameraContentBean.getType() == CameraFileType.IMAGE) {
                                File file = new File(cameraContentBean.getUri());
                                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
                                //注意：file就是与服务器对应的key,后面filename是服务器得到的文件名
                                bodyMap.put("HandleFiles\"; filename=\"" + cameraContentBean.getFilename() , requestFile);
                            }
                            if (cameraContentBean.getType() == CameraFileType.VIDEO) {
                                File file = new File(cameraContentBean.getUri());
                                RequestBody requestFile = RequestBody.create(MediaType.parse("video/mp4"), file);
                                //注意：file就是与服务器对应的key,后面filename是服务器得到的文件名
                                bodyMap.put("HandleFiles\"; filename=\"" + cameraContentBean.getFilename() , requestFile);
                            }*/

                            if (OpenFileUtil.isVideo(filename)) {
                                requestFile = RequestBody.create(MediaType.parse("video/" + OpenFileUtil.getFileExtension(filename)), file);
                            } else {
                                requestFile = RequestBody.create(MediaType.parse("image/" + OpenFileUtil.getFileExtension(filename)), file);
                            }

                            //注意：file就是与服务器对应的key,后面filename是服务器得到的文件名
                            params.put("handleFiles\"; filename=\"" + filename, requestFile);
                        });

                    }
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnYes.setEnabled(false);
                    Call<ResponseBody> call = breakDownService.handleProblem(params);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "提交成功", Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);
                                navHostController.popBackStack();
                            } else {
                                try {
                                    String errStr = response.errorBody().string();
                                    ErrorResult errorResult = JsonUtil.getObject(errStr, getContext());
                                    Log.e("xiaweihu", "处理故障失败: ===========>" + errStr);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                            Log.e("xiaweihu", "处理故障失败: ===========>", throwable);
                        }
                    });
                }
                break;
            case "video":
                Bundle vbundle = new Bundle();
                vbundle.putString(VideoFragment.VIDEO_URL_FLAG, breakRecordGlobal.getAttachments().get(Integer.parseInt(array[1])).getUrl());
                vbundle.putString(VideoFragment.VIDEO_TITLE, "故障视频");
                navHostController.navigate(R.id.to_video_view, vbundle);
                break;
            case "img":
                Bundle bundle = new Bundle();
                bundle.putString(ImageFragment.IMAGE_URL, breakRecordGlobal.getAttachments().get(Integer.parseInt(array[1])).getUrl());
                navHostController.navigate(R.id.to_img_view,bundle);
                break;
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedUser = userList.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getContext(), "需要选择一个处理人", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
        return super.onOptionsItemSelected(item);
    }
}