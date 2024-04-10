package com.tiger.yunda.ui.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.tiger.yunda.R;
import com.tiger.yunda.databinding.FragmentBreakdownListDialogItemBinding;
import com.tiger.yunda.databinding.FragmentBreakdowntDialogListDialogBinding;

import java.util.ArrayList;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     BreakDownListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class BreakDownListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private FragmentBreakdownListDialogItemBinding binding;

    private int CHOOSE_CODE = 3;
    private Context context;

    // TODO: Customize parameters
    public static BreakDownListDialogFragment newInstance(int itemCount) {
        final BreakDownListDialogFragment fragment = new BreakDownListDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_breakdown_list_dialog_item, container);
        binding = FragmentBreakdownListDialogItemBinding.inflate(inflater, container, false);
        context = getContext();
        ViewHolder viewHolder = new ViewHolder(this);
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

    private class ViewHolder implements View.OnClickListener {

        ImageButton button;

        BreakDownListDialogFragment breakDownListDialogFragment;

        public ViewHolder(BreakDownListDialogFragment breakDownListDialogFragment) {
            button = binding.imageButton;
            button.setTag("problem_video");
            button.setOnClickListener(this);
            this.breakDownListDialogFragment = breakDownListDialogFragment;
        }

        @Override

        public void onClick(View v) {

            //breakDownListDialogFragment.dismiss();
            //NavHostFragment.findNavController(breakDownListDialogFragment).navigate(R.id.to_camera);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("故障资料")
                    .setMessage("请选择文件或者拍照")
                    .setPositiveButton("已有图像", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 点击“OK”按钮后的操作
                            Log.i("xiaweihu", "图像:  =========================>");
                            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);   // 是否允许多选
                           /* ArrayList<String> mimeTypes = new ArrayList<String>();
                            mimeTypes.add("image/*");
                            mimeTypes.add("video/*");
                            albumIntent.putStringArrayListExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);*/
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
                            NavHostFragment.findNavController(breakDownListDialogFragment).navigate(R.id.dialog_to_camera);
                            dialog.dismiss();
                        }
                    })
                    .show();
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