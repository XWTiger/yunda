package com.tiger.yunda.ui.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tiger.yunda.R;
import com.tiger.yunda.data.BreakDownInfo;
import com.tiger.yunda.data.BreakDownType;
import com.tiger.yunda.databinding.FragmentBreakdownListDialogItemBinding;
import com.tiger.yunda.databinding.FragmentBreakdowntDialogListDialogBinding;
import com.tiger.yunda.enums.CameraFileType;
import com.tiger.yunda.ui.login.LoginViewModel;
import com.tiger.yunda.ui.login.LoginViewModelFactory;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     BreakDownListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class BreakDownListDialogFragment extends BottomSheetDialogFragment {


    private static final String ARG_ITEM_COUNT = "item_count";
    private FragmentBreakdownListDialogItemBinding binding;
    private FragmentBreakdowntDialogListDialogBinding listDialogBinding;

    private int CHOOSE_CODE = 3;
    private Context context;
    private static volatile BreakDownListDialogFragment breakDownListDialogFragment ;


    private BreakDownListViewModel viewModel;

    private BreakDownInfo breakDownInfo = null;

    public static   BreakDownListDialogFragment newInstance(int itemCount, BreakDownInfo info) {
        if (Objects.isNull(breakDownListDialogFragment)) {
             breakDownListDialogFragment = new BreakDownListDialogFragment();
             Bundle args = new Bundle();
            args.putInt(ARG_ITEM_COUNT, itemCount);
            breakDownListDialogFragment.setArguments(args);
            breakDownListDialogFragment.breakDownInfo = info;
        }
        return breakDownListDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BreakDownListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_breakdown_list_dialog_item, container);
        binding = FragmentBreakdownListDialogItemBinding.inflate(inflater, container, false);
        listDialogBinding = FragmentBreakdowntDialogListDialogBinding.inflate(inflater, container, false);
        context = getContext();
        ViewHolder viewHolder = new ViewHolder(this);
        if (Objects.nonNull(viewModel)) {
            viewModel.getTypes().observe(this, new Observer<List<BreakDownType>>() {
                @Override
                public void onChanged(List<BreakDownType> breakDownTypes) {
                    SpinnerAdapter sa = new SpinnerAdapter(breakDownTypes, context);
                    binding.problemCatalogSelect.setAdapter(sa);
                }
            });

        }

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       /* final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new BreakDownAdapter(getArguments().getInt(ARG_ITEM_COUNT)));*/

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ViewHolder implements View.OnClickListener, Switch.OnCheckedChangeListener, AdapterView.OnItemSelectedListener{

        private  ImageButton button;

        private Spinner type;

        private EditText problemDesc;

        private LinearLayout addProblemVedioLayout;

        private ChipGroup chipGroup;

        private Chip chip;

        private Switch aSwitch;

        private ImageButton addRecoverButton;
        private ChipGroup recoverChipGroup;

        private Button finished;

        private LinearLayout recoverLayout;

        private LinearLayout problemRecoverLayout;



        BreakDownListDialogFragment breakDownListDialogFragment;



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
            chip = binding.chip2;
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
                if (Objects.nonNull(breakDownInfo.getType()) ) {
                    type.setSelection(breakDownInfo.getTypePosition());
                }
                if (Objects.nonNull(breakDownInfo.getDesc()) && breakDownInfo.getDesc() != "") {
                    problemDesc.setText(breakDownInfo.getDesc());
                }

                if (Objects.nonNull(breakDownInfo.getFiles()) && !breakDownInfo.getFiles().isEmpty()) {
                    chipGroup.removeAllViews();

                    int position = 0;
                    breakDownInfo.getFiles().forEach(s -> {
                        chip.setText("故障." + (s.getType().equals(CameraFileType.IMAGE)?"jpg":"mp4"));
                        chip.setTag(position);
                        chipGroup.addView(chip);
                    });

                }
                if (Objects.nonNull(breakDownInfo.getDiscretion()) && breakDownInfo.getDiscretion()) {
                    aSwitch.setChecked(breakDownInfo.getDiscretion());
                    if (Objects.nonNull(breakDownInfo.getHandleFiles()) && !breakDownInfo.getHandleFiles().isEmpty()) {
                        recoverChipGroup.removeAllViews();

                        int position = 0;
                        breakDownInfo.getFiles().forEach(s -> {
                            chip.setText("恢复." + (s.getType().equals(CameraFileType.IMAGE)?"jpg":"mp4"));
                            chip.setTag(position);
                            recoverChipGroup.addView(chip);
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
            }
            breakDownInfo.setDiscretion(isChecked);
        }

        @Override

        public void onClick(View v) {

            String tag = (String) v.getTag();
            switch (tag) {
                case "problem_video":
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("故障资料")
                            .setMessage("请选择文件或者拍照")
                            .setPositiveButton("已有图像", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“OK”按钮后的操作
                                    Log.i("xiaweihu", "图像:  =========================>");
                                    Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                    albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);   // 是否允许多选
                                    albumIntent.setType("*/*");
                                    startActivityForResult(albumIntent, CHOOSE_CODE); // 打开系统相册
                                    dialog.dismiss();
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
                    break;
                case "finished":
                    break;

            }


        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (Objects.nonNull(view)) {
                breakDownInfo.setType((Integer) view.getTag());
                breakDownInfo.setTypePosition(position);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.i("xiaweihu", "onNothingSelected: ============================");
        }

        private void saveInfo() {
            breakDownInfo.setDesc(this.problemDesc.getText().toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("xiaweihu", "onActivityResult:  ==========>" + data.getData().toString() + "==========>" +  data.getClipData().getItemCount());
        data.getClipData().getItemAt(1).getUri();
        if ( this.getDialog() != null) {
            View view = this.getDialog().getWindow().getDecorView();
            view.setVisibility(View.VISIBLE); // 设置对话框的视图不可见
        }
    }





}