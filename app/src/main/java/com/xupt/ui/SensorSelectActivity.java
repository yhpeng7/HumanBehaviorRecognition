package com.xupt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.xupt.R;
import com.xupt.adapter.Node;
import com.xupt.adapter.SimpleTreeAdapter;
import com.xupt.adapter.TreeListViewAdapter;
import com.xupt.base.BaseSensorTreeActivity;
import com.xupt.constant.Constant;
import com.xupt.event.SensorChangeEvent;
import com.xupt.mvp.MainActivity;
import com.xupt.utils.Preference;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

public class SensorSelectActivity extends BaseSensorTreeActivity implements View.OnClickListener{

    private Button commitButton;
    private TreeListViewAdapter adapter;
    List<String> sensorNames = new ArrayList<>();
    private List<Node> allNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_select);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        commitButton = findViewById(R.id.sensor_select_btn_commit);
        ListView sensorTreeList = (ListView) findViewById(R.id.sensor_select_list_view);

        adapter = new SimpleTreeAdapter(sensorTreeList, this,
                mData, 0,R.drawable.ic_arrow_down,R.drawable.ic_arrow_right);
        sensorTreeList.setAdapter(adapter);

        setListViewHeightBasedOnChildren(sensorTreeList);

        commitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sensor_select_btn_commit) {
            allNodes = adapter.getAllNodes();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < allNodes.size(); i++) {
                if (allNodes.get(i).isChecked()){
                    sensorNames.add(allNodes.get(i).getName());
                }
            }
            Preference.Companion.putSharedPreferences(Constant.SENSOR_NAMES, sensorNames);
            EventBus.getDefault().post(new SensorChangeEvent(sensorNames));

            if (sensorNames.size() == 0) {
                Toast.makeText(this, "请至少选择一个传感器", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!"MainActivity".equals(getIntent().getStringExtra("MainActivity"))) {
                startActivity(new Intent(this,MainActivity.class));
            }
            finish();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = 0;
            listView.setLayoutParams(params);
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
                + listView.getPaddingBottom() + listView.getPaddingTop();
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}