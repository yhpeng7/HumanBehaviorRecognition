package com.xupt.mvp;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xupt.R;
import com.xupt.base.BaseMvpSwipeBackActivity;
import com.xupt.bean.User;
import com.xupt.constant.Constant;
import com.xupt.event.RegisterSuccessEvent;
import com.xupt.ui.SensorSelectActivity;
import com.xupt.utils.Calculate;
import com.xupt.utils.CustomDialog;

import com.xupt.utils.Preference;
import com.xupt.utils.Random;
import com.xupt.service.SendEmailServer;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends BaseMvpSwipeBackActivity<RegisterContract.View,RegisterContract.Present<RegisterContract.View>>
        implements RegisterContract.View, View.OnClickListener , View.OnFocusChangeListener {

    Button registerButton;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText emailEditText;
    EditText authCodeEditText;
    TextView verifyEmailTextView;
    ImageView backButton;
    CustomDialog loadingDialog;
    String authCode;
    boolean hasHint = false;
    String[] contents;
    int index;
    String username;
    String password;
    String email;
    boolean registerSuccess = false;

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_register;
    }

    @Override
    public void initData() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        registerButton = findViewById(R.id.register_btn_reset);
        usernameEditText = findViewById(R.id.register_et_username);
        passwordEditText = findViewById(R.id.register_et_password);
        emailEditText = findViewById(R.id.register_et_email);
        authCodeEditText = findViewById(R.id.register_et_vc);
        verifyEmailTextView = findViewById(R.id.register_tv_verify_email);
        backButton = findViewById(R.id.register_iv_back);

        contents = new String[60];
        for (int i = 0; i < 60; i++) {
            contents[i] = (60 - i) + "s";
        }
        index = 0;

        registerButton.setOnClickListener(this);
        verifyEmailTextView.setOnClickListener(this);
        backButton.setOnClickListener(this);
        passwordEditText.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register_tv_verify_email){
            sendAuthCode();
        } else if (v.getId() == R.id.register_btn_reset) {
            if (!Calculate.isFastClick() && checkInput()) {
                showLoading();
                Objects.requireNonNull(getMPresenter()).register(username, password, email);
            }
        } else if (v.getId() == R.id.register_iv_back) {
            finish();
        }
    }

    @Override
    public void registerSuccess(User user) {
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        registerSuccess = true;
        Preference.Companion.putSharedPreferences(Constant.HAS_LOGIN,true);
        Preference.Companion.putSharedPreferences(Constant.USER, user);
        EventBus.getDefault().post(new RegisterSuccessEvent());
        startActivity(new Intent(this, SensorSelectActivity.class));
        finish();
    }

    @Override
    public void registerFailure() {
        Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void duplicateUsername() {
        usernameEditText.setText("");
        username = null;
        Toast.makeText(this, "用户名重复，请重新输入", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void duplicateEmail(String username) {
        authCodeEditText.setText("");
        authCode = null;
        Toast.makeText(this, "该邮箱已绑定账号:" + username + "，请重新输入", Toast.LENGTH_LONG).show();
    }

    private boolean checkInput() {
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        email = emailEditText.getText().toString();
        String authCodeInput = authCodeEditText.getText().toString();
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(this, "密码不能少于6位，请重新输入", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(authCodeInput)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!authCodeInput.equals(authCode)) {
            Toast.makeText(this, "验证码输入错误，请重新输入", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void sendAuthCode(){
        String emailAccount = emailEditText.getText().toString();
        if (!TextUtils.isEmpty(emailAccount)) {
            authCode = Random.randomVerifyCode();
            new SendEmailServer().execute(emailAccount, "验证码", authCode);
            Toast.makeText(this, "邮件已发送，请注意查收", Toast.LENGTH_SHORT).show();
            countDown();
        } else {
            Toast.makeText(this, "请输入正确的邮箱地址", Toast.LENGTH_SHORT).show();
        }
    }

    public void countDown(){
        final Timer timer = new Timer();
        verifyEmailTextView.setTextColor(Color.parseColor("#9E9E9E"));
        verifyEmailTextView.setClickable(false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (index == contents.length) {
                            index = 0;
                            verifyEmailTextView.setTextColor(Color.parseColor("#879ED0"));
                            verifyEmailTextView.setClickable(true);
                            verifyEmailTextView.setText("验证");
                            timer.cancel();
                        } else {
                            verifyEmailTextView.setText(contents[index]);
                            index++;
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void start() {
        loadingDialog = new CustomDialog(this);
        usernameEditText.setText((String)Preference.Companion.getSharedPreferences(Constant.REGISTER_USERNAME,""));
        passwordEditText.setText((String) Preference.Companion.getSharedPreferences(Constant.REGISTER_PASSWORD,""));
        emailEditText.setText((String) Preference.Companion.getSharedPreferences(Constant.REGISTER_EMAIL, ""));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.register_et_password) {
            if (!hasHint && hasFocus) {
                Toast.makeText(this, "密码格式:不少于6位的数字与字母的组合，字母区分大小写", Toast.LENGTH_LONG).show();
                hasHint = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!registerSuccess) {
            Preference.Companion.putSharedPreferences(Constant.REGISTER_USERNAME, usernameEditText.getText().toString());
            Preference.Companion.putSharedPreferences(Constant.REGISTER_PASSWORD, passwordEditText.getText().toString());
            Preference.Companion.putSharedPreferences(Constant.REGISTER_EMAIL, emailEditText.getText().toString());
        } else {
            Preference.Companion.putSharedPreferences(Constant.REGISTER_USERNAME, "");
            Preference.Companion.putSharedPreferences(Constant.REGISTER_PASSWORD,"");
            Preference.Companion.putSharedPreferences(Constant.REGISTER_EMAIL, "");
        }
    }

    @Override
    public void showLoading() {
        loadingDialog.show();
    }

    @Override
    public void hideLoading() {
        loadingDialog.dismiss();
    }

    @NotNull
    @Override
    protected RegisterContract.Present<RegisterContract.View> createPresenter() {
        return new RegisterPresenter<>();
    }
}
