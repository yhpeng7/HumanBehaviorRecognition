package com.xupt.ui;

import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.xupt.R;
import com.xupt.base.BaseSwipeBackActivity;
import com.xupt.constant.Constant;
import com.xupt.event.ServerChangeEvent;
import com.xupt.utils.Preference;

import org.greenrobot.eventbus.EventBus;

public class ServerSelectActivity extends BaseSwipeBackActivity
        implements RadioGroup.OnCheckedChangeListener,View.OnClickListener{

    private EditText receiverEmailAddressEditText;
    //0 Web   1 Email
    int serverType;
    String emailInput = null;
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_server_select;
    }

    @Override
    public void initData() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        RadioGroup serverSelectRadioGroup = findViewById(R.id.server_select_rg_server);
        receiverEmailAddressEditText = findViewById(R.id.server_select_et_email_address);
        Button commitButton = findViewById(R.id.server_select_btn_commit);

        serverType = Preference.Companion.getSharedPreferences(Constant.SERVER_TYPE, 0);
        emailInput = Preference.Companion.getSharedPreferences(Constant.EMAIL_DATA_RECEIVER, "");

        if (serverType == 0) {
            RadioButton webButton = findViewById(R.id.server_select_rb_web);
            webButton.setChecked(true);
            receiverEmailAddressEditText.setVisibility(View.GONE);
        } else if (serverType == 1) {
            RadioButton emailButton = findViewById(R.id.server_select_rb_email);
            emailButton.setChecked(true);
            receiverEmailAddressEditText.setVisibility(View.VISIBLE);
            receiverEmailAddressEditText.setText(emailInput);
        }

        serverSelectRadioGroup.setOnCheckedChangeListener(this);
        commitButton.setOnClickListener(this);
    }

    @Override
    public void initView() {
    }

    @Override
    public void start() {
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.server_select_rb_web) {
            serverType = 0;
            receiverEmailAddressEditText.setVisibility(View.GONE);
        } else if (checkedId == R.id.server_select_rb_email) {
            serverType = 1;
            receiverEmailAddressEditText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Preference.Companion.putSharedPreferences(Constant.SERVER_TYPE, serverType);
        ServerChangeEvent event = new ServerChangeEvent(serverType);
        if (serverType == 1) {
            emailInput = receiverEmailAddressEditText.getText().toString();
            if (TextUtils.isEmpty(emailInput)) {
                Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
            } else {
                event.setReceiverEmailAddress(emailInput);
                Preference.Companion.putSharedPreferences(Constant.EMAIL_DATA_RECEIVER,emailInput);
            }
        }
        EventBus.getDefault().post(event);
        if (!(serverType == 1 && TextUtils.isEmpty(emailInput))) {
            finish();
        }
    }
}