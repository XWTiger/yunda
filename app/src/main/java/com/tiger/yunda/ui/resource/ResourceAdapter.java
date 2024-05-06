package com.tiger.yunda.ui.resource;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tiger.yunda.MainActivity;
import com.tiger.yunda.R;
import com.tiger.yunda.dao.ResourceLocationDao;
import com.tiger.yunda.data.model.OperationResource;
import com.tiger.yunda.databinding.BooksListBinding;
import com.tiger.yunda.entity.ResourceLocationEntity;
import com.tiger.yunda.utils.DownLoadUtil;
import com.tiger.yunda.utils.OpenFileUtil;

import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.util.List;
import java.util.Objects;

public class ResourceAdapter extends ArrayAdapter<OperationResource> implements View.OnClickListener {

    private  List<OperationResource> operationResources;

    private ResourceLocationDao resourceLocationDao;
    private Context context;

    public ResourceAdapter(@NonNull Context context, int resource,  @NonNull List<OperationResource> objects) {
        super(context, resource, objects);
        operationResources = objects;
        this.context = context;
        resourceLocationDao = MainActivity.appDatabase.resourceLocationDao();
    }

    public List<OperationResource> getOperationResources() {
        return operationResources;
    }

    public void setOperationResources(List<OperationResource> operationResources) {
        this.operationResources = operationResources;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {




        if (Objects.isNull(convertView)) {
            BooksListBinding  binding = BooksListBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            binding.contentLayout.setOnClickListener(this);
            binding.contentLayout.setTag(position);
            convertView = binding.getRoot();
            binding.setResource(operationResources.get(position));
        }

        return convertView;
    }


    @Override
    public void onClick(View v) {
        Integer position = (Integer) v.getTag();
        OperationResource operationResource = operationResources.get(position);
        ResourceLocationEntity rle = resourceLocationDao.getById(operationResource.getId());
        if (Objects.isNull(rle)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("系统提示")
                    .setMessage("本地没有相关文档确定要下载吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Long refId = DownLoadUtil.downLoad(operationResource.getFilePath(), context, operationResource.getFileName() + operationResource.getExt());
                            if (refId >= 0) {
                                String url = DownLoadUtil.getFileUriString(refId, context);
                                if (StringUtils.isNotBlank(url)) {
                                    ResourceLocationEntity resourceLocationEntity = ResourceLocationEntity.builder()
                                            .id(operationResource.getId())
                                            .ext(operationResource.getExt())
                                            .contentType(operationResource.getContentType())
                                            .fileName(operationResource.getFileName())
                                            .filePath(operationResource.getFilePath())
                                            .location(url).build();
                                    resourceLocationDao.insert(resourceLocationEntity);
                                }
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
        } else {

            Intent intent1 = OpenFileUtil.openFile(context, URLDecoder.decode(rle.getLocation()));;
            if (Objects.nonNull(intent1)) {
                context.startActivity(intent1);
            }
        }

    }


}
