package com.tiger.yunda.ui.home.inspection;





import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiger.yunda.R;
import com.tiger.yunda.ui.home.inspection.placeholder.PlaceholderContent.PlaceholderItem;
import com.tiger.yunda.databinding.FragmentInspectionBinding;

import java.util.List;
import java.util.Objects;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyInspectionRecyclerViewAdapter extends RecyclerView.Adapter<MyInspectionRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    private Context context;

    public MyInspectionRecyclerViewAdapter(List<PlaceholderItem> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentInspectionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
/*
        holder.mItem = mValues.get(position);

        holder.mIdView.setText(mValues.get(position).content);*/
        Log.i("tiger", "onBindViewHolder: =========================");
        holder.mItems = mValues;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public  TextView mIdView;
        public  Button start;
        public  Button pause;
        public   Button stop;
        public LinearLayout linearLayout;
        public List<PlaceholderItem> mItems;

        public ViewHolder(FragmentInspectionBinding binding) {
            super(binding.getRoot());
         /*   if (Objects.nonNull(this.mIdView)) {
                LinearLayout linearLayout = binding.inspectionList;
                mIdView = binding.itemTrainPosition;
                start = binding.button2;
                pause = binding.button3;
                stop = binding.button4;
                if (mItems.size() > 0) {

                    mItems.stream().forEach(item -> {
                        LinearLayout line = new LinearLayout(context);
                        mIdView.setText(item.content);
                        line.addView(mIdView);
                        start.setTag(item.id);
                        pause.setTag(item.id);
                        stop.setTag(item.id);
                        line.addView(start);
                        line.addView(pause);
                        line.addView(stop);
                        linearLayout.addView(line);
                    });
                    pause.setOnClickListener(this);
                    start.setOnClickListener(this);
                    pause.setOnClickListener(this);
                    stop.setOnClickListener(this);
                }

            } else {
                if (mItems.size() > 0) {
                    linearLayout.removeAllViews();
                    mItems.stream().forEach(item -> {
                        LinearLayout line = new LinearLayout(context);
                        mIdView.setText(item.content);
                        line.addView(mIdView);
                        start.setTag(item.id);
                        pause.setTag(item.id);
                        stop.setTag(item.id);
                        line.addView(start);
                        line.addView(pause);
                        line.addView(stop);
                        linearLayout.addView(line);
                    });
                }
            }
*/

        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.button2) {
                //开始
                Log.i("tiger", "onClick: button2 -----------> " + v.getTag());

            }
            if (id == R.id.button3) {
                //开始
                Log.i("tiger", "onClick: button3 -----------> " + v.getTag());

            }
            if (id == R.id.button4) {
                //开始
                Log.i("tiger", "onClick: button4 -----------> " + v.getTag());

            }

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }
}