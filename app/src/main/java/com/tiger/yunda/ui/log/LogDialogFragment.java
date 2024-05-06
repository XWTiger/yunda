package com.tiger.yunda.ui.log;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.data.model.Faults;
import com.tiger.yunda.data.model.WorkLog;
import com.tiger.yunda.databinding.FragmentDetailLogDialogItemBinding;
import com.tiger.yunda.databinding.FragmentDetailLogDialogBinding;
import com.tiger.yunda.enums.RoleType;
import com.tiger.yunda.ui.home.TrainLocations;
import com.tiger.yunda.utils.CollectionUtil;

import java.util.List;
import java.util.Objects;

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
        dismiss();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(FragmentDetailLogDialogItemBinding binding) {
            super(binding.getRoot());
            text = binding.text;
        }

    }


}