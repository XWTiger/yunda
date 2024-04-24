package com.tiger.yunda.ui.common;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tiger.yunda.data.model.Train;
import com.tiger.yunda.databinding.FragmentTrainDialogItemBinding;
import com.tiger.yunda.databinding.FragmentTrainDialogListDialogBinding;
import com.tiger.yunda.ui.home.CreateMissionFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     TrainListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class TrainListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private FragmentTrainDialogListDialogBinding binding;
    private CreateMissionFragment createMissionFragment;

    // TODO: Customize parameters
    public static TrainListDialogFragment newInstance(List<Train> itemCount) {
        final TrainListDialogFragment fragment = new TrainListDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM_COUNT, (Serializable) itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentTrainDialogListDialogBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Train> trains = new ArrayList<>();
        if (getArguments() != null) {
            trains = (List<Train>) getArguments().getSerializable(ARG_ITEM_COUNT);
        }
        recyclerView.setAdapter(new TrainAdapter(trains));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        ViewHolder(FragmentTrainDialogItemBinding binding) {
            super(binding.getRoot());
            text = binding.text;
        }

    }

    private class TrainAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener {

        private  List<Train> mItemCount;

        TrainAdapter(List<Train> itemCount) {
            mItemCount = itemCount;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(FragmentTrainDialogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(mItemCount.get(position).getText());
            holder.text.setTag(mItemCount.get(position).getText());
            holder.text.setOnClickListener(this);
        }

        @Override
        public int getItemCount() {
            if (Objects.isNull(mItemCount)) {
                return 0;
            }
            return mItemCount.size();
        }


        @Override
        public void onClick(View v) {
            createMissionFragment.onItemClicked((String) v.getTag());
        }
    }

    public CreateMissionFragment getCreateMissionFragment() {
        return createMissionFragment;
    }

    public void setCreateMissionFragment(CreateMissionFragment createMissionFragment) {
        this.createMissionFragment = createMissionFragment;
    }
}