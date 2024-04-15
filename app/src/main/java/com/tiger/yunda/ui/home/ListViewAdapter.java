package com.tiger.yunda.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.DiffUtil;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.enums.RoleType;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Mission> {

    private Context context;
    private NavController navController;
    private FragmentManager fragmentManager;

    private Activity activity;

    public ListViewAdapter(@NonNull Context context, int resource,@NonNull List<Mission> objects, Activity activity) {
        super(context, resource,objects);
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null ) {

            convertView = inflater.inflate(R.layout.layout_mission, parent, false);


            TextView tvItem = convertView.findViewById(R.id.editTextTextMultiLine);
            Button btnItem = convertView.findViewById(R.id.button_accept);
            Button btnReject = convertView.findViewById(R.id.button_reject);
            CheckBox checkBox = convertView.findViewById(R.id.checkBox);

            btnItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 接受事件
                    Log.d("tiger", "onClick: ======================= ");
                    AppCompatActivity ac = (AppCompatActivity) activity;
                    ActionBar actionBar = ac.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setDisplayShowCustomEnabled(false);
                        actionBar.setDisplayShowTitleEnabled(true);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("系统提示")
                            .setMessage("确认要领取该任务吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“OK”按钮后的操作
                                    Toast.makeText(context, "点击确定", Toast.LENGTH_SHORT).show();
                                    if (MainActivity.loggedInUser.getRole() == RoleType.WORKER) {
                                        //巡检员
                                        getNavController().navigate(R.id.to_inspection_mission);
                                    }
                                    //to_accept_mission
                                    if (MainActivity.loggedInUser.getRole() == RoleType.WORKER_LEADER) {
                                        //工班长
                                        getNavController().navigate(R.id.to_accept_mission);
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“Cancel”按钮后的操作
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });

            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 拒绝事件
                    Log.d("tiger", "onClick: ======================= ");
                    // 例如：Toast.makeText(context, "Button clicked for " + item, Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("系统提示")
                            .setMessage("确认要拒绝该任务吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“OK”按钮后的操作
                                    Toast.makeText(context, "点击确定", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 点击“Cancel”按钮后的操作
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }
            });
             viewHolder = new ViewHolder(tvItem, btnItem, btnReject, checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TextView textView = viewHolder.getTvItem();
        final Mission item = getItem(position);
        textView.setText(item.getInspectionUnit());

        return convertView;
    }

    private static final DiffUtil.ItemCallback<Mission> diffCallback = new DiffUtil.ItemCallback<Mission>() {
        @Override
        public boolean areItemsTheSame(@NonNull Mission oldItem, @NonNull Mission newItem) {
            //判断数据是否为同一项，一般用名称判断
            //当return值为true时执行下面areContentsTheSame方法
            return oldItem.getTaskId().equals(newItem.getTaskId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Mission oldItem, @NonNull Mission newItem) {
            //判断数据内容是否相同，根据需求更改
            if (oldItem.getTaskId() != newItem.getTaskId()) return false;
            if (!oldItem.getInspectionUnit().equals(newItem.getInspectionUnit())) return false;
            return true;
        }
    };

    public NavController getNavController() {
        return navController;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public static class ViewHolder  {
        TextView tvItem;
        Button btnItem ;
        Button btnReject ;

        public ViewHolder(TextView tvItem, Button btnItem, Button btnReject, CheckBox checkBox) {
            this.tvItem = tvItem;
            this.btnItem = btnItem;
            this.btnReject = btnReject;
            this.checkBox = checkBox;
        }

        CheckBox checkBox ;

        public TextView getTvItem() {
            return tvItem;
        }

        public void setTvItem(TextView tvItem) {
            this.tvItem = tvItem;
        }
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
}

