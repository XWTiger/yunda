package com.tiger.yunda.ui.resource;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.barteksc.pdfviewer.PDFView;
import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.databinding.FragmentPdfBinding;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PDFFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PDFFragment extends Fragment {


    public static final String PDF_URL = "pdf_url";



    private String pdfUrl;


    private FragmentPdfBinding fragmentPdfBinding;

    private NavController navController;

    private PDFView pdfView;

    public PDFFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PDFFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PDFFragment newInstance(String param1, String param2) {
        PDFFragment fragment = new PDFFragment();
        Bundle args = new Bundle();
        args.putString(PDF_URL, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pdfUrl = getArguments().getString(PDF_URL);
        }
        //自定义header
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) { //自定义应用栏
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true); // 可选，如果不需要显示默认标题
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPdfBinding = FragmentPdfBinding.inflate(inflater, container, false);


        if (Objects.isNull(pdfView)) {
            pdfView = fragmentPdfBinding.pdfView;
        }
        getPdf(pdfUrl);
        /*.fromUri(uri)
                .swipeHorizontal(true)
                .pageSnap(true)
                .autoSpacing(true)
                .pageFling(true);*/
        // Inflate the layout for this fragment
        return fragmentPdfBinding.getRoot();
    }
    @SuppressLint("StaticFieldLeak")
    private void getPdf(String url) {
        final InputStream[] input = new InputStream[1];

        MainActivity.threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    input[0] = new URL(url).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pdfView.fromStream(input[0])
                        .enableAnnotationRendering(true) //可以显示电子章
                        .load();
            }
        });
      /*  new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    input[0] = new URL(url).openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pdfView.fromStream(input[0])
                        .enableAnnotationRendering(true) //可以显示电子章
                        .load();
            }
        }.execute();*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        navController = NavHostFragment.findNavController(this);
        navController.popBackStack();
        Log.i("xiaweihu", "onOptionsItemSelected: ========================" + item.getItemId());
        return super.onOptionsItemSelected(item);
    }
}