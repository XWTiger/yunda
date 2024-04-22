package com.tiger.yunda.ui.breakdown;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.databinding.FragmentBreakDownDetailDialogBinding;
import com.tiger.yunda.utils.TimeUtil;

import java.util.Date;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     BreakDownDetailDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class BreakDownDetailDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_DETAIL = "wroker_detail";
    private FragmentBreakDownDetailDialogBinding binding;
    private Date startDateTime;


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
        BreakRecord breakRecord = (BreakRecord) getArguments().getSerializable(ARG_ITEM_DETAIL);
        binding.setDetail(breakRecord);
        binding.addDealtime.setOnClickListener(this);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        /*final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(new ItemAdapter(getArguments().getInt(ARG_ITEM_COUNT)));*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onClick(View v) {
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
    }
}