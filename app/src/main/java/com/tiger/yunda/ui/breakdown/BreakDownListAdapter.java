package com.tiger.yunda.ui.breakdown;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tiger.yunda.R;
import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.databinding.BreakdownRecordeListBinding;

import java.util.List;
import java.util.Objects;

public class BreakDownListAdapter extends ArrayAdapter<BreakRecord> {


    private BreakdownRecordeListBinding breakdownRecordeListBinding;

    private List<BreakRecord> breakRecords;

    private FragmentManager manager;

    private NavController navController;

    public BreakDownListAdapter(@NonNull Context context, int resource, @NonNull List<BreakRecord> objects, FragmentManager fragmentManager, NavController nav) {
        super(context, resource, objects);
        this.breakRecords = objects;
        this.navController = nav;
        manager = fragmentManager;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (Objects.isNull(convertView)) {
            breakdownRecordeListBinding = BreakdownRecordeListBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            breakdownRecordeListBinding.setRecord(breakRecords.get(position));
            convertView = breakdownRecordeListBinding.getRoot();
            ViewHolder viewHolder = new ViewHolder(convertView, breakRecords.get(position), manager, navController);
            viewHolder.setDetailBtn(breakdownRecordeListBinding.problemDetailButton);
            breakdownRecordeListBinding.problemDetailButton.setTag(breakRecords.get(position).getId());

            //breakdownRecordeListBinding.problemDetailButton.setBackground(new GreenBorderDrawable());

        }

        return convertView;

    }



    @Override
    public int getCount() {
        return super.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button detailBtn;
        private BreakRecord breakRecord;
        private FragmentManager manager;
        private NavController navController;
        public ViewHolder(@NonNull View itemView, BreakRecord breakRecord, FragmentManager manager, NavController navController) {
            super(itemView);
            this.breakRecord = breakRecord;
            this.manager = manager;
            this.navController = navController;
        }

        public Button getDetailBtn() {
            return detailBtn;
        }

        public void setDetailBtn(Button detailBtn) {
            this.detailBtn = detailBtn;
            detailBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Bundle args = new Bundle();
            args.putSerializable(BreakDownDetailDialogFragment.ARG_ITEM_DETAIL, breakRecord);
            navController.navigate(R.id.break_record_dia, args);
           // BreakDownDetailDialogFragment.newInstance(breakRecord).show(manager, "breakdown2");
        }
    }

    public List<BreakRecord> getBreakRecords() {
        return breakRecords;
    }

    public void setBreakRecords(List<BreakRecord> breakRecords) {
        this.breakRecords = breakRecords;
    }
}
