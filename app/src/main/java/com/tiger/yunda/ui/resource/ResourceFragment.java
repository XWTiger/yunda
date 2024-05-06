package com.tiger.yunda.ui.resource;

import android.app.DownloadManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;


import com.tiger.yunda.R;
import com.tiger.yunda.data.model.OperationResource;
import com.tiger.yunda.databinding.FragmentResourceBinding;

import java.util.List;
import java.util.Objects;

public class ResourceFragment extends Fragment implements  SearchView.OnQueryTextListener {

    private FragmentResourceBinding binding;
    private SearchView searchView;

    private View customView;

    private ResourceViewModel resourceViewModel;

    private ResourceAdapter resourceAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        ActionBar actionBar = activity.getSupportActionBar();

       if (actionBar != null) { //自定义应用栏
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // 可选，如果不需要显示默认标题

            // 创建自定义视图
           customView = LayoutInflater.from(getContext()).inflate(R.layout.resource_header_bar, null);
           searchView = customView.findViewById(R.id.searchView);
           searchView.setIconifiedByDefault(false);
           searchView.setOnQueryTextListener(this);
            // 设置自定义视图
            actionBar.setCustomView(customView);

        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (Objects.isNull(resourceViewModel)) {
            resourceViewModel = new ResourceViewModel(getContext());
        }

        binding = FragmentResourceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //自定义header
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) { //自定义应用栏
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false); // 可选，如果不需要显示默认标题
            if (Objects.nonNull(customView)) {
                // 设置自定义视图
                actionBar.setCustomView(customView);
            }
        }


        resourceViewModel.queryData(1, 20, "").observe(getViewLifecycleOwner(), new Observer<List<OperationResource>>() {
            @Override
            public void onChanged(List<OperationResource> operationResources) {

                resourceAdapter = new ResourceAdapter(getContext(), binding.list.getId(), operationResources);
                binding.list.setAdapter(resourceAdapter);
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }





    @Override
    public boolean onQueryTextSubmit(String query) {
        resourceViewModel.queryData(1, 20, query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


}