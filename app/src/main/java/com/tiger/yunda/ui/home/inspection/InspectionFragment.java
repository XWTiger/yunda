package com.tiger.yunda.ui.home.inspection;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.tiger.yunda.R;
import com.tiger.yunda.data.BreakDownInfo;
import com.tiger.yunda.ui.common.BreakDownListDialogFragment;
import com.tiger.yunda.ui.common.CameraContentBean;
import com.tiger.yunda.ui.home.ListViewAdapter;
import com.tiger.yunda.ui.home.Mission;
import com.tiger.yunda.ui.home.inspection.placeholder.PlaceholderContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a list of Items.
 */
public class InspectionFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private Mission mission;

    private BreakDownInfo breakDownInfo;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InspectionFragment() {
    }

    private BreakDownListDialogFragment breakDownListDialogFragment;
    com.tiger.yunda.databinding.FragmentInspectionBinding binding;
    LayoutInflater inflater;
    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static InspectionFragment newInstance(int columnCount) {
        InspectionFragment fragment = new InspectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Objects.isNull(breakDownInfo)) {
            breakDownInfo = new BreakDownInfo();
        }
        if (getArguments() != null) {
            mission = (Mission) getArguments().getSerializable(ListViewAdapter.MISSION_KEY);
        }
        setHasOptionsMenu(true);


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
        Log.i("xiaweihu", "onOptionsItemSelected: ========================" + item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        binding = com.tiger.yunda.databinding.FragmentInspectionBinding.inflate(inflater, container, false);
        ViewHolder viewHolder =  new ViewHolder(binding, PlaceholderContent.ITEMS);
       // NavController navController = NavHostFragment.findNavController(this);


        Bundle bundle = getArguments();
        if (Objects.nonNull(bundle)) {
            List<CameraContentBean> files = new ArrayList<>();
            List<CameraContentBean> handleFiles = new ArrayList<>();
            List<CameraContentBean> contentBeans = (List<CameraContentBean>) bundle.getSerializable("contents");
            if (Objects.nonNull(contentBeans) && contentBeans.size() > 0) {
                contentBeans.forEach(cameraContentBean -> {
                    if (cameraContentBean.isProblem()) {
                        files.add(cameraContentBean);
                    } else {
                        handleFiles.add(cameraContentBean);
                    }
                });
                breakDownInfo.setFiles(files);
                breakDownInfo.setHandleFiles(handleFiles);

            }
            if (Objects.isNull(breakDownListDialogFragment)) {
                breakDownListDialogFragment = BreakDownListDialogFragment.newInstance(2, breakDownInfo);
                breakDownListDialogFragment.show(getParentFragmentManager(), "breakdownReport");
            }
        }

        return binding.getRoot();
    }



    public static String getFileNameFromUri(Uri fileUri) {
        // 获取文件的完整路径
        String filePath = fileUri.getPath();
        // 获取文件名
        int lastSlashIndex = filePath.lastIndexOf('/');
        return lastSlashIndex != -1 ? filePath.substring(lastSlashIndex + 1) : filePath;
    }

    public class ViewHolder  implements View.OnClickListener {

        public LinearLayout linearLayout;
        public LinearLayout listchild;

        public List<PlaceholderContent.PlaceholderItem> mItems;



        public ViewHolder(com.tiger.yunda.databinding.FragmentInspectionBinding binding, List<PlaceholderContent.PlaceholderItem> mItems) {
            this.mItems = mItems;
            if (Objects.isNull(breakDownInfo)) {
                breakDownInfo = new BreakDownInfo();
            }
            if (Objects.isNull(this.linearLayout)) {

                LinearLayout linearLayout = binding.inspectionList;
                linearLayout.removeAllViews();
               // listchild = bindingList.inspectionList2;

                if (mItems.size() > 0) {
                    Log.i("tiger", "ViewHolder: ================ ");

                    for (PlaceholderContent.PlaceholderItem item : mItems) {

                         listchild = (LinearLayout) inflater.inflate(R.layout.fragment_inspection_list, linearLayout, false);
                        //inflater.inflate(R.id.item_train_position, listchild, false);

                        TextView view = (TextView) listchild.getChildAt(0);
                        Button startBtn = (Button) listchild.getChildAt(1);
                        Button pauseBtn = (Button) listchild.getChildAt(2);
                        Button finishBtn = (Button) listchild.getChildAt(3);

                        view.setText(item.content);
                        startBtn.setTag(item.id);
                        pauseBtn.setTag(item.id);
                        finishBtn.setTag(item.id);
                        startBtn.setOnClickListener(this);
                        pauseBtn.setOnClickListener(this);
                        finishBtn.setOnClickListener(this);
                        linearLayout.addView(listchild);
                    }


                }

            }


        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.button2) {
                //开始
                Log.i("tiger", "onClick: button2 -----------> " + v.getTag());

            }
            if (id == R.id.button3) {
                //暂停
                Log.i("tiger", "onClick: button3 -----------> " + v.getTag());
                breakDownListDialogFragment = BreakDownListDialogFragment.newInstance(2, breakDownInfo);
                breakDownListDialogFragment.show(getParentFragmentManager(), "breakdownReport");


            }
            if (id == R.id.button4) {
                //结束
                Log.i("tiger", "onClick: button4 -----------> " + v.getTag());

            }

        }



        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public void setLinearLayout(LinearLayout linearLayout) {
            this.linearLayout = linearLayout;
        }

        public List<PlaceholderContent.PlaceholderItem> getmItems() {
            return mItems;
        }

        public void setmItems(List<PlaceholderContent.PlaceholderItem> mItems) {
            this.mItems = mItems;
        }
    }
}