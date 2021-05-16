package com.xupt.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.xupt.R;
import com.xupt.event.DataTypeChangeEvent;
import com.xupt.event.IsCollectEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class DataCollectionFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    TextView collectStatusTextView;

    Switch walkSwitch;
    Switch runSwitch;
    Switch jumpSwitch;
    Switch upstairsSwitch;
    Switch downstairsSwitch;
    Switch lastSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_collection, container, false);
        collectStatusTextView = view.findViewById(R.id.data_collection_tv_status);
        walkSwitch = view.findViewById(R.id.data_collection_switch_walk);
        runSwitch = view.findViewById(R.id.data_collection_switch_run);
        jumpSwitch = view.findViewById(R.id.data_collection_switch_jump);
        upstairsSwitch = view.findViewById(R.id.data_collection_switch_upstairs);
        downstairsSwitch = view.findViewById(R.id.data_collection_switch_downstairs);
        lastSwitch = walkSwitch;

        walkSwitch.setOnCheckedChangeListener(this);
        runSwitch.setOnCheckedChangeListener(this);
        jumpSwitch.setOnCheckedChangeListener(this);
        upstairsSwitch.setOnCheckedChangeListener(this);
        downstairsSwitch.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void isCollect(IsCollectEvent event) {
        if (event.isMovement() == 0) {
            collectStatusTextView.setText("正在收集数据...");
        }  else if (event.isMovement() == 2) {
            collectStatusTextView.setText("请选择运动类型");
        }
    }

    public DataCollectionFragment() {
    }

    public static DataCollectionFragment newInstance(String param1, String param2) {
        DataCollectionFragment fragment = new DataCollectionFragment();
        return fragment;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getId() == R.id.data_collection_switch_walk) {
                EventBus.getDefault().post(new DataTypeChangeEvent(0));
                runSwitch.setChecked(false);
                jumpSwitch.setChecked(false);
                upstairsSwitch.setChecked(false);
                downstairsSwitch.setChecked(false);
            } else if (buttonView.getId() == R.id.data_collection_switch_run) {
                EventBus.getDefault().post(new DataTypeChangeEvent(1));
                walkSwitch.setChecked(false);
                jumpSwitch.setChecked(false);
                upstairsSwitch.setChecked(false);
                downstairsSwitch.setChecked(false);
            } else if (buttonView.getId() == R.id.data_collection_switch_jump) {
                EventBus.getDefault().post(new DataTypeChangeEvent(2));
                walkSwitch.setChecked(false);
                runSwitch.setChecked(false);
                upstairsSwitch.setChecked(false);
                downstairsSwitch.setChecked(false);
            } else if (buttonView.getId() == R.id.data_collection_switch_upstairs) {
                EventBus.getDefault().post(new DataTypeChangeEvent(3));
                walkSwitch.setChecked(false);
                runSwitch.setChecked(false);
                jumpSwitch.setChecked(false);
                downstairsSwitch.setChecked(false);
            } else if (buttonView.getId() == R.id.data_collection_switch_downstairs) {
                EventBus.getDefault().post(new DataTypeChangeEvent(4));
                walkSwitch.setChecked(false);
                runSwitch.setChecked(false);
                jumpSwitch.setChecked(false);
                upstairsSwitch.setChecked(false);
            }
        }
        if (!walkSwitch.isChecked() && !runSwitch.isChecked() && !jumpSwitch.isChecked() && !upstairsSwitch.isChecked() && !downstairsSwitch.isChecked()) {
            EventBus.getDefault().post(new DataTypeChangeEvent(-1));
            collectStatusTextView.setText("请选择运动类型");
        }
    }
}