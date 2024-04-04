package com.tiger.yunda.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tiger.yunda.R;
import com.tiger.yunda.databinding.FragmentAcceptMissionBinding;
import com.tiger.yunda.databinding.FragmentHomeBinding;
import com.tiger.yunda.ui.breakdown.BreakDownFragment;


//接受任务界面
public class AcceptMissionFragment extends Fragment {

    private FragmentAcceptMissionBinding binding;

    public AcceptMissionFragment() {

    }
 /*   public AcceptMissionFragment(FragmentAcceptMissionBinding binding) {
        this.binding = binding;
    }

    public AcceptMissionFragment(int contentLayoutId, FragmentAcceptMissionBinding binding) {
        super(contentLayoutId);
        this.binding = binding;
    }*/

    public static AcceptMissionFragment newInstance(String param1, String param2) {
        AcceptMissionFragment fragment = new AcceptMissionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAcceptMissionBinding.inflate(inflater, container, false);
        //View root = binding.getRoot();
        return inflater.inflate(R.layout.fragment_accept_mission, container, false);
       // return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}