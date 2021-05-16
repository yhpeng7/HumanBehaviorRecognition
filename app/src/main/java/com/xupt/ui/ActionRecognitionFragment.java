package com.xupt.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xupt.R;
import com.xupt.event.IsCollectEvent;
import com.xupt.event.RecognitionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ActionRecognitionFragment extends Fragment {
    private TextView recognitionStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action_recognition, container, false);
        recognitionStatus = view.findViewById(R.id.action_recognition_tv_status);
        return view;
    }

    @Subscribe
    public void isMovement(IsCollectEvent event){
        if (event.isMovement() == 1 || event.isMovement() == 3) {
            recognitionStatus.setText("请保持运动状态");
        }
    }

    @Subscribe
    public void recognitionStatus(RecognitionEvent event) {
        if (event.getMovementType() == 0) {
            recognitionStatus.setText("你正在:走");
        } else if (event.getMovementType() == 1) {
            recognitionStatus.setText("你正在:跑");
        } else if (event.getMovementType() == 2) {
            recognitionStatus.setText("你正在:跳");
        } else if (event.getMovementType() == 3) {
            recognitionStatus.setText("你正在:上楼");
        } else if (event.getMovementType() == 4) {
            recognitionStatus.setText("你正在:下楼");
        } else if (event.getMovementType() == 5) {
            recognitionStatus.setText("未知状态");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public ActionRecognitionFragment() {
    }

    public static ActionRecognitionFragment newInstance(String param1, String param2) {
        ActionRecognitionFragment fragment = new ActionRecognitionFragment();
        return fragment;
    }
}