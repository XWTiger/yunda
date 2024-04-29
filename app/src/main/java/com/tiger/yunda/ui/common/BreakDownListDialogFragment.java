package com.tiger.yunda.ui.common;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.BreakDownInfo;
import com.tiger.yunda.data.BreakDownType;
import com.tiger.yunda.data.model.ErrorResult;
import com.tiger.yunda.databinding.ChipFileBinding;
import com.tiger.yunda.databinding.FragmentBreakdownListDialogItemBinding;
import com.tiger.yunda.enums.CameraFileType;
import com.tiger.yunda.service.InspectionService;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     BreakDownListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class BreakDownListDialogFragment extends BottomSheetDialogFragment implements SpinnerCallBack {


    private static final String ARG_ITEM_COUNT = "item_count";
    private FragmentBreakdownListDialogItemBinding binding;


    private int CHOOSE_CODE = 3;
    private Context context;
    private static volatile BreakDownListDialogFragment breakDownListDialogFragment ;


    private BreakDownListViewModel viewModel;

    private BreakDownInfo breakDownInfo = null;
    private SpinnerAdapter spinnerAdapter;
    ActivityResultLauncher<PickVisualMediaRequest>  pickMultipleMedia;
    private InspectionService inspectionService;



    public static   BreakDownListDialogFragment newInstance(int itemCount, BreakDownInfo info) {


        if  (Objects.isNull(breakDownListDialogFragment)) {
            synchronized (ARG_ITEM_COUNT) {
                 breakDownListDialogFragment = new BreakDownListDialogFragment();
                /* Bundle args = new Bundle();
                args.putInt(ARG_ITEM_COUNT, itemCount);
                breakDownListDialogFragment.setArguments(args);*/
                breakDownListDialogFragment.breakDownInfo = info;
            }
        }
        breakDownListDialogFragment.breakDownInfo = info;
        return breakDownListDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Objects.isNull(inspectionService)) {
            inspectionService =  MainActivity.retrofitClient.create(InspectionService.class);
        }


        viewModel = new ViewModelProvider(this).get(BreakDownListViewModel.class);
        viewModel.setInspectionService(inspectionService);
        List<BreakDownType> breakDownTypes = new ArrayList<>();
        context = getContext();
        spinnerAdapter = new SpinnerAdapter(breakDownTypes, context, this, -1);



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_breakdown_list_dialog_item, container);
        binding = FragmentBreakdownListDialogItemBinding.inflate(inflater, container, false);
        binding.problemCatalogSelect.setAdapter(spinnerAdapter);
        if (Objects.nonNull(viewModel)) {
            viewModel.getTypes().observe(this, new Observer<List<BreakDownType>>() {
                @Override
                public void onChanged(List<BreakDownType> breakDownTypes) {
                    spinnerAdapter.setData(breakDownTypes);
                    spinnerAdapter.notifyDataSetChanged();
                }
            });

        }
        ViewHolder viewHolder = new ViewHolder(this);
        pickMultipleMedia =
                registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(4), uris -> {
                    // Callback is invoked after the user selects media items or closes the
                    // photo picker.
                    if (!uris.isEmpty()) {
                        Log.d("PhotoPicker", "Number of items selected: " + uris.size());
                        viewHolder.handleFileUri(uris);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void spinnerChecked(int deliverMissionIndex, int userIndex) {

    }

    private class ViewHolder implements View.OnClickListener, Switch.OnCheckedChangeListener, AdapterView.OnItemSelectedListener{

        private  ImageButton button;

        private Spinner type;

        private EditText problemDesc;

        private LinearLayout addProblemVedioLayout;

        private ChipGroup chipGroup;

        private Switch aSwitch;

        private ImageButton addRecoverButton;
        private ChipGroup recoverChipGroup;

        private Button finished;

        private LinearLayout recoverLayout;

        private LinearLayout problemRecoverLayout;



        BreakDownListDialogFragment breakDownListDialogFragment;

        private List<CameraContentBean> removeProblem = new ArrayList<>();

        private List<CameraContentBean> removeRecover = new ArrayList<>();

        private boolean way;



        public ViewHolder(BreakDownListDialogFragment breakDownListDialogFragment) {
            button = binding.imageButton; //添加故障图片或者视频
            button.setTag("problem_video");
            button.setOnClickListener(this);
            problemDesc = binding.problemDesc;
            addProblemVedioLayout = binding.addProblemVedio;
            chipGroup = binding.fileGroup;
            aSwitch = binding.switch1;
            aSwitch.setOnCheckedChangeListener(this);
            recoverChipGroup = binding.recoverFileGroup;
            finished = binding.inspectionFinished;
            finished.setTag("finished");
            finished.setOnClickListener(this);
            addRecoverButton = binding.imageButton2; //添加恢复视频或照片
            addRecoverButton.setTag("recover_problem");
            addRecoverButton.setOnClickListener(this);

            this.breakDownListDialogFragment = breakDownListDialogFragment;
            recoverLayout = binding.recoverLayout;
            problemRecoverLayout = binding.problemRecoverVedio;
            type = binding.problemCatalogSelect;
            type.setOnItemSelectedListener(this);
            if (Objects.isNull(breakDownInfo)) {
                Log.w("xiaweihu", "breakDownInfo:================");
                breakDownInfo = new BreakDownInfo();
            } else {
                //回显样式
                if (Objects.nonNull(breakDownInfo.getTypePosition()) ) {
                    spinnerAdapter.notifyDataSetChanged();
                    type.setSelection(breakDownInfo.getTypePosition(), true);
                }
                if (Objects.nonNull(breakDownInfo.getDesc()) && breakDownInfo.getDesc() != "") {
                    problemDesc.setText(breakDownInfo.getDesc());
                }

                if (Objects.nonNull(breakDownInfo.getFiles()) && !breakDownInfo.getFiles().isEmpty()) {
                    chipGroup.removeAllViews();

                    AtomicInteger position = new AtomicInteger();
                    breakDownInfo.getFiles().forEach(s -> {
                        Chip chip =  (Chip) LayoutInflater.from(context).inflate(R.layout.chip_file, null);;
                        chip.setText("故障." + (s.getType().equals(CameraFileType.IMAGE)?"jpg":"mp4"));
                        chip.setTag("problem_" + position.get());
                        chipGroup.addView(chip);
                        position.getAndIncrement();
                    });

                }
                if (Objects.nonNull(breakDownInfo.getDiscretion()) && breakDownInfo.getDiscretion()) {
                    aSwitch.setChecked(breakDownInfo.getDiscretion());
                    if (Objects.nonNull(breakDownInfo.getHandleFiles()) && !breakDownInfo.getHandleFiles().isEmpty()) {
                        recoverChipGroup.removeAllViews();

                        AtomicInteger position = new AtomicInteger();
                        breakDownInfo.getFiles().forEach(s -> {
                            Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.chip_file, null);;
                            chip.setText("恢复." + (s.getType().equals(CameraFileType.IMAGE)?"jpg":"mp4"));
                            chip.setTag("recover_" + position.get());
                            recoverChipGroup.addView(chip);
                            position.getAndIncrement();
                        });
                    }

                }


            }
        }



        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                this.recoverLayout.setVisibility(View.VISIBLE);
                this.problemRecoverLayout.setVisibility(View.VISIBLE);
            } else {
                this.recoverLayout.setVisibility(View.INVISIBLE);
                this.problemRecoverLayout.setVisibility(View.INVISIBLE);
                this.recoverChipGroup.removeAllViews();
            }
            breakDownInfo.setDiscretion(isChecked);
        }

        @Override

        public void onClick(View v) {

            String tag = (String) v.getTag();
            saveInfo();
            if (StringUtils.isNotBlank(tag)) {
                switch (tag) {
                    case "problem_video":
                        removeProblem.clear();
                        if (Objects.nonNull(breakDownInfo.getFiles())) {
                            breakDownInfo.getFiles().clear();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("故障资料")
                                .setMessage("请选择文件或者拍照")
                                .setPositiveButton("已有图像", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“OK”按钮后的操作
                                        Log.i("xiaweihu", "图像:  =========================>");
                                        way = true;
                                        openFilePicker();
                                        //dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("拍摄", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“Cancel”按钮后的操作
                                        Toast.makeText(context, "拍摄", Toast.LENGTH_SHORT).show();
                                        //getNavController().navigate(R.id.to_accept_mission);
                                        Log.i("xiaweihu", "拍摄:  =========================>");
                                        if (breakDownListDialogFragment != null && breakDownListDialogFragment.getDialog() != null) {
                                            // View view = breakDownListDialogFragment.getDialog().getWindow().getDecorView();
                                            //view.setVisibility(View.GONE); // 设置对话框的视图不可见
                                            breakDownListDialogFragment.dismiss();
                                        }
                                        Bundle bundle = new Bundle();
                                        bundle.putString("type", "1");
                                        NavHostFragment.findNavController(breakDownListDialogFragment).navigate(R.id.dialog_to_camera, bundle);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        break;
                    case "recover_problem":
                        removeRecover.clear();
                        if (Objects.nonNull(breakDownInfo.getHandleFiles())) {
                            breakDownInfo.getHandleFiles().clear();
                        }
                        AlertDialog.Builder builderRecover = new AlertDialog.Builder(context);
                        builderRecover.setTitle("故障资料")
                                .setMessage("请选择文件或者拍照")
                                .setPositiveButton("已有图像", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“OK”按钮后的操作
                                        Log.i("xiaweihu", "图像:  =========================>");
                                        way = false;
                                        openFilePicker();
                                        //dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("拍摄", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 点击“Cancel”按钮后的操作
                                        Toast.makeText(context, "拍摄", Toast.LENGTH_SHORT).show();
                                        //getNavController().navigate(R.id.to_accept_mission);
                                        Log.i("xiaweihu", "拍摄:  =========================>");
                                        if (breakDownListDialogFragment != null && breakDownListDialogFragment.getDialog() != null) {
                                            // View view = breakDownListDialogFragment.getDialog().getWindow().getDecorView();
                                            //view.setVisibility(View.GONE); // 设置对话框的视图不可见
                                            breakDownListDialogFragment.dismiss();
                                        }
                                        Bundle bundle = new Bundle();
                                        bundle.putString("type", "0");
                                        NavHostFragment.findNavController(breakDownListDialogFragment).navigate(R.id.dialog_to_camera, bundle);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        break;
                    case "finished":
                        //提交故障

                        if (StringUtils.isBlank(breakDownInfo.getDesc())) {
                            problemDesc.setError("故障描述不能为空");
                            return;
                        } else {
                            problemDesc.setError(null);
                        }
                        if (breakDownInfo.getType() <= 0) {
                            Toast.makeText(getContext(), "故障类型不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (breakDownInfo.getBreakFilesUri().isEmpty()) {
                            Toast.makeText(getContext(), "故障文件不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Call<Map<String, String>> result = inspectionService.createBreakDownRecord(breakDownInfo.getSubtaskId(), breakDownInfo.getTrainLocationId(), breakDownInfo.getType(), breakDownInfo.getDesc(), breakDownInfo.getDiscretion(), breakDownInfo.getBreakFilesUri(), breakDownInfo.getHandleDesc(), breakDownInfo.getHandleFilesUri());
                        result.enqueue(new Callback<Map<String, String>>() {
                            @Override
                            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                                if (response.isSuccessful()) {
                                   dismiss();
                                } else {
                                    try {
                                        String errStr = response.errorBody().string();
                                        ErrorResult errorResult = JsonUtil.getObject(errStr, context);
                                        Log.e("xiaweihu", "创建故障失败: ===========>" + errStr);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Map<String, String>> call, Throwable throwable) {

                            }
                        });

                        break;
                    default: //文件chip 选择
                        if (tag.startsWith("problem_")) { //删除故障图片或视频
                            String position = tag.split("_")[1];
                            chipGroup.removeView(v);
                            removeProblem.add(breakDownInfo.getFiles().get(Integer.parseInt(position)));

                        }
                        if (tag.startsWith("recover_")) { //删除恢复图片或视频
                            String position = tag.split("_")[1];
                            recoverChipGroup.removeView(v);
                            removeRecover.add(breakDownInfo.getHandleFiles().get(Integer.parseInt(position)));
                        }

                }

            }


        }



        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (Objects.nonNull(view)) {
                Log.i("xiaweihu", "onItemSelected:  ==========>" + (Integer) view.getTag() + "=====" +position);
                LinearLayout linearLayout = (LinearLayout) view;
                Integer type = (Integer) linearLayout.getChildAt(0).getTag();
                breakDownInfo.setType(type);
                breakDownInfo.setTypePosition(position);

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.i("xiaweihu", "onNothingSelected: ============================");
        }

        private void saveInfo() {
            breakDownInfo.setDesc(this.problemDesc.getText().toString());
            breakDownInfo.setType((Integer) this.type.getTag());
        }

        public String getMimeType(ContentResolver contentResolver, Uri fileUri) {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(fileUri));
            return extension;
        }

        public void handleFileUri(List<Uri> uris) {

                if (way) {
                    if (Objects.isNull(breakDownInfo.getFiles())) {
                        breakDownInfo.setFiles(new ArrayList<>());
                    }
                    List<CameraContentBean> ccbs = new ArrayList<>();

                    uris.forEach(uri -> {
                        String type = getMimeType(context.getContentResolver(), uri);
                        CameraContentBean cameraContentBean = null;
                        if (type.equals("jpg")) {
                          cameraContentBean = new CameraContentBean(CameraFileType.IMAGE, uri.toString(), way);
                        } else {
                            cameraContentBean = new CameraContentBean(CameraFileType.VIDEO, uri.toString(), way);
                        }
                        ccbs.add(cameraContentBean);
                    });
                    breakDownInfo.setFiles(ccbs);
                    chipGroup.removeAllViews();
                    AtomicInteger position = new AtomicInteger();
                    breakDownInfo.getFiles().forEach(s -> {

                        Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.chip_file, null);
                        chip.setText("故障_" + position.get() + "." + (s.getType().equals(CameraFileType.IMAGE)?"jpg":"mp4"));
                        chip.setTag("problem_" + position.get());
                        chip.setOnClickListener(this);
                        chipGroup.addView(chip);
                        position.getAndIncrement();
                    });
                } else {
                    if (Objects.isNull(breakDownInfo.getHandleFiles())) {
                        breakDownInfo.setHandleFiles(new ArrayList<>());
                    }
                    recoverChipGroup.removeAllViews();
                    List<CameraContentBean> ccbs = new ArrayList<>();

                    uris.forEach(uri -> {
                        String type = getMimeType(context.getContentResolver(), uri);
                        CameraContentBean cameraContentBean = null;
                        if (type.equals("jpg")) {
                            cameraContentBean = new CameraContentBean(CameraFileType.IMAGE, uri.toString(), way);
                        } else {
                            cameraContentBean = new CameraContentBean(CameraFileType.VIDEO, uri.toString(), way);
                        }
                        ccbs.add(cameraContentBean);
                    });
                    breakDownInfo.setHandleFiles(ccbs);
                    AtomicInteger position = new AtomicInteger();
                    breakDownInfo.getFiles().forEach(s -> {
                        Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.chip_file, null);
                        chip.setText("恢复_" + position.get() + "." + (s.getType().equals(CameraFileType.IMAGE)?"jpg":"mp4"));
                        chip.setTag("recover_" + position.get());
                        chip.setOnClickListener(this);
                        recoverChipGroup.addView(chip);
                        position.getAndIncrement();
                    });
                }



        }
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




}