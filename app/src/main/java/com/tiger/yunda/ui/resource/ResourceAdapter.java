package com.tiger.yunda.ui.resource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tiger.yunda.data.model.OperationResource;
import com.tiger.yunda.databinding.BooksListBinding;

import java.util.List;
import java.util.Objects;

public class ResourceAdapter extends ArrayAdapter<OperationResource> {

    private  List<OperationResource> operationResources;

    public ResourceAdapter(@NonNull Context context, int resource,  @NonNull List<OperationResource> objects) {
        super(context, resource, objects);
        operationResources = objects;
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
            convertView = binding.getRoot();
            binding.setResource(operationResources.get(position));
        }

        return convertView;
    }



}
