package com.tiger.yunda.ui.breakdown;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;
import com.tiger.yunda.MainActivity;
import com.tiger.yunda.data.model.Attachments;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.databinding.FragmentBreakDownDetailDialogBinding;
import com.tiger.yunda.service.MissionService;
import com.tiger.yunda.ui.common.SpinnerAdapter;
import com.tiger.yunda.utils.CollectionUtil;
import com.tiger.yunda.utils.TimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
public class BreakDownDetailDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_DETAIL = "wroker_detail";
    private static final int BREAK_DEAL_UNDO = 1; //未处理
    private static final int BREAK_DEAL_FINISHED = 2; //已处理
    private FragmentBreakDownDetailDialogBinding binding;

    private BreakDownViewModel breakDownViewModel;

    private List<User> userList;

    private User selectedUser;

    private Date startDateTime;
    private Boolean finisehd = true;


    // TODO: Customize parameters
    public static BreakDownDetailDialogFragment newInstance(BreakRecord breakRecord) {
        final BreakDownDetailDialogFragment fragment = new BreakDownDetailDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_DETAIL, breakRecord);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentBreakDownDetailDialogBinding.inflate(inflater, container, false);
        if (Objects.isNull(breakDownViewModel)) {
            breakDownViewModel = new ViewModelProvider(this).get(BreakDownViewModel.class);
            breakDownViewModel.setContext(getContext());
        }
        BreakRecord breakRecord = (BreakRecord) getArguments().getSerializable(ARG_ITEM_DETAIL);
        BreakRecord result = breakDownViewModel.getBreakRecordById(breakRecord.getId());
        if (Objects.nonNull(result)) {
            breakRecord = result;
        }
        //加载图片
        List<Attachments> attachments = breakRecord.getAttachments();
        if (!CollectionUtil.isEmpty(attachments)) {

            attachments.forEach(attachment -> {
                Uri uri = Uri.parse(attachment.getUrl());
                ImageView imageView = new ImageView(getContext());
                imageView.setMaxWidth(200);
                imageView.setMaxHeight(200);
                binding.contentLayout.addView(imageView);
                Glide.with(this).load(uri).into(imageView);
            });


        }
        breakDownViewModel.getUsers(MainActivity.loggedInUser.getDeptId()).observe(this, users -> {
            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(CollectionUtil.covertUserToSpinnerObj(users), getContext());
            binding.spinnerPerson.setAdapter(spinnerAdapter);
            binding.spinnerPerson.setOnItemSelectedListener(this);
            userList = users;
        });


        binding.setDetail(breakRecord);
        binding.addDealtime.setTag("check_time");
        binding.addDealtime.setOnClickListener(this);
        binding.btnEdit.setTag("edit");
        binding.btnEdit.setOnClickListener(this);
        binding.btnCancle.setTag("cancel");
        binding.btnCancle.setOnClickListener(this);
        binding.btnYes.setTag("ok");
        binding.btnYes.setOnClickListener(this);
        if (breakRecord.getState() == BREAK_DEAL_FINISHED) {
            binding.btnEdit.setVisibility(View.INVISIBLE);
        } else {
            finisehd = false;
        }
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


    @Override
    public void onClick(View v) {
        String cation = (String) v.getTag();
        switch (cation) {
            case "check_time":
                new CardDatePickerDialog.Builder(v.getContext())
                        .setTitle("请选择处理时间时间")
                        .setLabelText("年", "月", "日", "时", "分", "秒")
                        .setOnChoose("选择", aLong -> {
                            //aLong  = millisecond
                            Date start = new Date(aLong);
                            startDateTime = start;
                            binding.dealTime.setText(TimeUtil.getSTrFromMs(start));
                            return null;
                        })
                        .setOnCancel("取消", () -> {
                            return null;
                        })
                        .build().show();
                break;
            case "edit":
                binding.addDealtime.setVisibility(View.VISIBLE);
                binding.dealPerson.setVisibility(View.GONE);
                binding.spinnerPerson.setVisibility(View.VISIBLE);
                break;
            case "cancel":
                dismiss();
                break;
            case "ok":
                if (finisehd) {
                    dismiss();
                } else {

                }
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
}