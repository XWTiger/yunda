package com.tiger.yunda.ui.breakdown;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.tiger.yunda.data.model.BreakRecord;
import com.tiger.yunda.databinding.BreakdownRecordeListBinding;

import java.util.List;
import java.util.Objects;

public class BreakDownListAdapter extends ArrayAdapter<BreakRecord> {


    private BreakdownRecordeListBinding breakdownRecordeListBinding;

    private List<BreakRecord> breakRecords;

    public BreakDownListAdapter(@NonNull Context context, int resource, @NonNull List<BreakRecord> objects, BreakdownRecordeListBinding breakdownRecordeListBinding) {
        super(context, resource, objects);
        this.breakRecords = objects;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (Objects.isNull(convertView)) {
            ViewHolder viewHolder = new ViewHolder(convertView);
            breakdownRecordeListBinding = BreakdownRecordeListBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            breakdownRecordeListBinding.setRecord(breakRecords.get(position));
            viewHolder.setDetailBtn(breakdownRecordeListBinding.problemDetailButton);
            breakdownRecordeListBinding.problemDetailButton.setTag(breakRecords.get(position).getId());

            //breakdownRecordeListBinding.problemDetailButton.setBackground(new GreenBorderDrawable());
            convertView = breakdownRecordeListBinding.getRoot();

        }

        return convertView;

    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button detailBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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

        }
    }

}
