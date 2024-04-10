package com.tiger.yunda.ui.common;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FallbackStrategy;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.tiger.yunda.R;
import com.tiger.yunda.databinding.FragmentCameraBinding;
import com.tiger.yunda.enums.CameraFileType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private List<CameraContentBean> contentBeans;

    private FragmentCameraBinding fragmentCameraBinding;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private ExecutorService cameraExecutor;

    private ImageCapture imageCapture;

    private Recording recording = null;

    private VideoCapture<Recorder> videoCapture = null;
    ;

    private Viewholder viewholder;

    private Camera camera;



    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    public CameraFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        this.contentBeans = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false);
        if (Objects.isNull(viewholder)) {
            this.viewholder = new Viewholder(this);
        }
        if (allPermissionsGranted()) {
            //校验权限
            startCamera();
        }
        cameraExecutor = Executors.newFixedThreadPool(2);
        return fragmentCameraBinding.getRoot();
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }





    private void startCamera() {
        if (cameraProviderFuture == null) {
            cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        }

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    // Preview
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(fragmentCameraBinding.previewView.getSurfaceProvider());

                    //默认后置摄像头
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;


                    //解绑前面所有的
                    cameraProvider.unbindAll();

                    imageCapture = new ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                            .build();


                    List<Quality> qualities = new ArrayList<>();
                    //qualities.add(Quality.UHD);
                    //qualities.add(Quality.FHD);
                    qualities.add(Quality.HD);
                    qualities.add(Quality.SD);
                    QualitySelector qualitySelector = QualitySelector.fromOrderedList(qualities, FallbackStrategy.lowerQualityThan(Quality.SD));


                    Recorder recorder = new Recorder.Builder()
                            .setQualitySelector(qualitySelector)
                            .build();

                    videoCapture = VideoCapture.withOutput(recorder);
                    MediaStoreOutputOptions options = new MediaStoreOutputOptions
                            .Builder(getContext().getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                            .setContentValues(getVideoContentValues())
                            .build();

                    videoCapture
                            .getOutput()
                            .prepareRecording(getContext(), options);


                    camera = cameraProvider.bindToLifecycle(
                            getViewLifecycleOwner(), cameraSelector, preview, imageCapture, videoCapture );
                    Log.i("xiaweihu", "======初始化照相 ===========");


                } catch (ExecutionException | InterruptedException exc) {
                    Log.e("tiger", "绑定预览失败", exc);
                }
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void startCameraVideo() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = this.cameraProviderFuture;

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();


                    // Preview
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(fragmentCameraBinding.previewView.getSurfaceProvider());

                    //默认后置摄像头
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                    //解绑前面所有的
                    cameraProvider.unbindAll();

                    List<Quality> qualities = new ArrayList<>();
                    //qualities.add(Quality.UHD);
                    qualities.add(Quality.FHD);
                    qualities.add(Quality.HD);
                    qualities.add(Quality.SD);
                    QualitySelector qualitySelector = QualitySelector.fromOrderedList(qualities, FallbackStrategy.lowerQualityThan(Quality.SD));


                    Recorder recorder = new Recorder.Builder()
                            .setQualitySelector(qualitySelector)
                            .build();
                    videoCapture = VideoCapture.withOutput(recorder);
                    MediaStoreOutputOptions options = new MediaStoreOutputOptions
                            .Builder(getContext().getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                            .setContentValues(getVideoContentValues())
                            .build();

                    videoCapture
                            .getOutput()
                            .prepareRecording(getContext(), options);
                        camera = cameraProvider.bindToLifecycle(
                                getViewLifecycleOwner(), cameraSelector,videoCapture);
                        Log.i("xiaweihu", "======初始化录像 ===========");


                } catch (ExecutionException | InterruptedException exc) {
                    Log.e("tiger", "绑定预览失败", exc);
                }
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    @NonNull
    private static ContentValues getContentValues() {
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Yunda-Image");
        }
        return contentValues;
    }

    private static ContentValues getVideoContentValues() {
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "video_" + name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Movies/Yunda-Video");
        }
        return contentValues;
    }



    //拍一张照片
    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }

        ContentValues contentValues = getContentValues();

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(
                getContext().getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
                .build();

        //拍照逻辑
        imageCapture.takePicture(
                ContextCompat.getMainExecutor(getContext()),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(ImageProxy image) {
                        super.onCaptureSuccess(image);
                        //拍照成功
                        Log.e("tiger", "拍照成功");
                        image.close();
                    }

                    @Override
                    public void onError(ImageCaptureException exception) {
                        Log.e("tiger", "拍照失败 : " + exception.getMessage(), exception);
                    }
                });


        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(getContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e("tiger", "图片保存失败 : " + exc.getMessage(), exc);
                    }

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        String msg = "图片保存成功: " + output.getSavedUri();
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.d("tiger", msg);
                        CameraContentBean cameraContentBean = new CameraContentBean(CameraFileType.IMAGE, output.getSavedUri());
                        contentBeans.add(cameraContentBean);
                    }
                }
        );
    }

    //开始录像
    private void captureVideo() {
        if (videoCapture == null) {
            Log.w("tiger", "captureVideo: videoCapture is null");
            Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT).show();
            return;
        }

        MediaStoreOutputOptions options =new MediaStoreOutputOptions
                .Builder( getContext().getContentResolver(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(getVideoContentValues())
                .build();
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        recording = videoCapture
                .getOutput()
                .prepareRecording(getContext(), options)
                .start(ContextCompat.getMainExecutor(getContext()), videoRecordEvent -> {
                    if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                        Log.i("tiger", "captureVideo: video start ===================");
                    } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                        if (!((VideoRecordEvent.Finalize) videoRecordEvent).hasError()) {
                            String msg = "Video capture succeeded: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri();
                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            CameraContentBean cameraContentBean = new CameraContentBean(CameraFileType.VIDEO, ((VideoRecordEvent.Finalize) videoRecordEvent).getOutputResults().getOutputUri());
                            this.contentBeans.add(cameraContentBean);
                        } else {
                            recording.close();
                            recording = null;
                            String msg = "Error: " + ((VideoRecordEvent.Finalize) videoRecordEvent).getError();
                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }
              });

        Log.i("tiger", "录像开启完成 ===================");
    }

    //开启闪光
    private void toggleFlash() {
        Camera camera = this.camera;
        if (camera.getCameraInfo().hasFlashUnit()) {
            if (camera.getCameraInfo().getTorchState().getValue() == 0) {
                camera.getCameraControl().enableTorch(true);

            } else {
                camera.getCameraControl().enableTorch(false);

            }
        } else {
           Toast.makeText(getContext(), "Flash is not available currently", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageCapture = null;
        videoCapture = null;
        cameraExecutor.shutdown();
    }

    public List<CameraContentBean> getContentBeans() {
        return contentBeans;
    }

    public void setContentBeans(List<CameraContentBean> contentBeans) {
        this.contentBeans = contentBeans;
    }

    private class Viewholder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        ImageButton captureButton;
        CameraFragment cameraFragment;
        //是否录制音频 闪光
        CheckBox checkBoxAudio;

        //录像
        ImageButton cameraButton;

        //停止录像
        ImageButton stopButton;
        ImageButton exitButton;




        public Viewholder(CameraFragment cameraFragment) {
            captureButton = fragmentCameraBinding.captureButton;
            checkBoxAudio = fragmentCameraBinding.audioSelection;
            cameraButton = fragmentCameraBinding.cameraButton;
            stopButton = fragmentCameraBinding.stopButton;
            exitButton = fragmentCameraBinding.cameraOut;
            captureButton.setOnClickListener(this);
            captureButton.setTag("capture");
            cameraButton.setOnClickListener(this);
            cameraButton.setTag("camera");
            stopButton.setTag("stop");
            stopButton.setOnClickListener(this);
            checkBoxAudio.setOnCheckedChangeListener(this);
            exitButton.setTag("exit");
            exitButton.setOnClickListener(this);
            this.cameraFragment = cameraFragment;
        }

        @Override
        public void onClick(View v) {
            String type = (String) v.getTag();
            if (type.equals("capture")) {
                cameraFragment.takePhoto();
                stopButton.setVisibility(View.INVISIBLE);

            }
            if (type.equals("camera")) {
                Toast.makeText(getContext(), "开始录制", Toast.LENGTH_SHORT).show();
                Log.i("tiger", "onClick:  camera, audio:" + checkBoxAudio.isChecked());
                stopButton.setVisibility(View.VISIBLE);
                captureButton.setVisibility(View.INVISIBLE);
                cameraButton.setVisibility(View.INVISIBLE);

                cameraFragment.captureVideo();
            }
            if (type.equals("stop")) {
                Toast.makeText(getContext(), "结束录制", Toast.LENGTH_SHORT).show();
                stopButton.setVisibility(View.INVISIBLE);
                captureButton.setVisibility(View.VISIBLE);
                cameraButton.setVisibility(View.VISIBLE);
                recording.stop();
            }

            if (type.equals("exit")) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("contents", (Serializable) getContentBeans());
                NavHostFragment.findNavController(cameraFragment).navigate(R.id.to_inspection_mission, bundle);
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.i("tiger", "onCheckedChanged:  toggleFlash================");
            toggleFlash();
        }
    }

}