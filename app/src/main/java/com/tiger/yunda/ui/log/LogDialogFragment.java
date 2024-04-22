package com.tiger.yunda.ui.log;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tiger.yunda.R;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.data.model.WorkLog;
import com.tiger.yunda.databinding.FragmentDetailLogDialogItemBinding;
import com.tiger.yunda.databinding.FragmentDetailLogDialogBinding;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     LogDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class LogDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_DETAIL = "wroker_detail";
    private FragmentDetailLogDialogBinding binding;

    // TODO: Customize parameters
    public static LogDialogFragment newInstance(WorkLog workLog) {
        final LogDialogFragment fragment = new LogDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_DETAIL, workLog);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDetailLogDialogBinding.inflate(inflater, container, false);
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

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(FragmentDetailLogDialogItemBinding binding) {
            super(binding.getRoot());
            text = binding.text;
        }

    }


}