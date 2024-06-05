package com.tiger.yunda.ui.breakdown;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mx.video.beans.MXPlaySource;
import com.tiger.yunda.databinding.FragmentVideoBinding;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String VIDEO_URL_FLAG = "video_url";
    public static final String VIDEO_TITLE = "video_title";

    private FragmentVideoBinding fragmentVideoBinding;

    // TODO: Rename and change types of parameters
    private String url;
    private String title;

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(VIDEO_URL_FLAG, param1);
        args.putString(VIDEO_TITLE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(VIDEO_URL_FLAG);
            title = getArguments().getString(VIDEO_TITLE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentVideoBinding = FragmentVideoBinding.inflate(inflater, container, false);
        //fragmentVideoBinding.mxVideoStd;
        MXPlaySource mxp = new MXPlaySource(Uri.parse(url), title, new HashMap<>(), false, true, false);
        fragmentVideoBinding.mxVideoStd.setSource( mxp,  0);
        fragmentVideoBinding.mxVideoStd.startPlay();
        return fragmentVideoBinding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
        Log.i("xiaweihu", "onOptionsItemSelected: ========================" + item.getItemId());
        return super.onOptionsItemSelected(item);
    }
}