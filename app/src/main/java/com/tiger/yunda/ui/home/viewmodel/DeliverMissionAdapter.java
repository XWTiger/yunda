package com.tiger.yunda.ui.home.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.tiger.yunda.R;
import com.tiger.yunda.data.BreakDownType;
import com.tiger.yunda.data.model.DeliverMssion;
import com.tiger.yunda.data.model.User;
import com.tiger.yunda.databinding.DeliverMissionBinding;
import com.tiger.yunda.ui.common.SpinnerAdapter;
import com.tiger.yunda.ui.common.SpinnerCallBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DeliverMissionAdapter extends ArrayAdapter<DeliverMssion> implements SpinnerCallBack, AdapterView.OnItemSelectedListener {
    List<DeliverMssion> deliverMissions;

    List<User> users;


    public DeliverMissionAdapter(@NonNull Context context, int resource, @NonNull List<DeliverMssion> objects, List<User> users) {
        super(context, resource, objects);
        this.deliverMissions = objects;
        this.users = users;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DeliverMissionBinding deliverMissionBinding = null;
        if (convertView == null) {
            deliverMissionBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.deliver_mission, parent, false);;
            convertView = deliverMissionBinding.getRoot();
            deliverMissionBinding.setDeliver(deliverMissions.get(position));
        } else {
            deliverMissionBinding = DataBindingUtil.getBinding(convertView);
        }
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(covertUserToSpinnerObj(users), getContext());
        deliverMissionBinding.spinnerPerson.setAdapter(spinnerAdapter);
        deliverMissionBinding.spinnerPerson.setTag(position);
        deliverMissionBinding.spinnerPerson.setOnItemSelectedListener(this);
        setCheck(deliverMissionBinding.spinnerPerson, deliverMissions.get(position));

        return convertView;

    }


    private List<BreakDownType> covertUserToSpinnerObj(List<User> users) {
        if (Objects.isNull(users) || users.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        List<BreakDownType> types = new ArrayList<>();
        users.forEach(user ->  {
            BreakDownType breakDownType = new BreakDownType();
            breakDownType.setName(user.getText());
            breakDownType.setType(user.getValue());
            types.add(breakDownType);
        });
        return types;
    }

    private void setCheck(Spinner spinner, DeliverMssion deliverMssion) {
        if (Objects.isNull(spinner) || users.isEmpty()) {
            return;
        }
        if (deliverMssion.getInspectorId().get() <= 0) {
            return;
        }
        for (int i = 0; i < users.size(); i++) {
            if (deliverMssion.getInspectorId().get() == users.get(i).getValue()) {
                spinner.setSelection(i);
                return;
            }
        }
        ;

    }

    public void spinnerChecked(int deliverMissionIndex, int userIndex) {
        DeliverMssion deliverMssion  = deliverMissions.get(deliverMissionIndex);
        deliverMssion.getInspector().set(users.get(userIndex).getText());
        deliverMssion.getInspectorId().set(users.get(userIndex).getValue());
    }

    public List<DeliverMssion> getDeliverMissions() {
        return deliverMissions;
    }

    public void setDeliverMissions(List<DeliverMssion> deliverMissions) {
        this.deliverMissions = deliverMissions;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int mindex = (int) parent.getTag();
        spinnerChecked(mindex, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
