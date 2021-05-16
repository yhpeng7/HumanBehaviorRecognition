package com.xupt.mvp;

import android.content.Intent;
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
import com.xupt.utils.CustomDialog;
import com.xupt.utils.Preference;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends BaseMvpSwipeBackActivity<LoginContract.View, LoginContract.Present<LoginContract.View>>
        implements LoginContract.View, View.OnClickListener {

    Button loginButton;
    Button registerButton;
    TextView resetPasswordTextView;
    ImageView backButton;
    EditText accountEditText;
    EditText passwordEditText;
    CustomDialog loadingDialog;
    String account;
    String password;
    boolean hasLogin;
    //清除注册成功时登录界面缓存的数据
    boolean registerSuccess = false;
    boolean loginSuccess = false;
    
    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_login;
    }

    @Override
    public void initData() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if ((hasLogin = Preference.Companion.getSharedPreferences(Constant.HAS_LOGIN, false))) {
            if (Preference.Companion.getSharedPreferences(Constant.SENSOR_NAMES, new ArrayList<String>()).size() != 0) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, SensorSelectActivity.class));
            }
            finish();
        }
        loginButton = findViewById(R.id.login_btn_login);
        registerButton = findViewById(R.id.login_btn_register);
        resetPasswordTextView = findViewById(R.id.login_tv_reset_password);
        backButton = findViewById(R.id.login_iv_back);
        accountEditText = findViewById(R.id.login_et_username);
        passwordEditText = findViewById(R.id.login_et_password);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        resetPasswordTextView.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void start() {
        loadingDialog = new CustomDialog(this);
        accountEditText.setText((String) Preference.Companion.getSharedPreferences(Constant.LOGIN_ACCOUNT, ""));
        passwordEditText.setText((String) Preference.Companion.getSharedPreferences(Constant.LOGIN_PASSWORD, ""));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_iv_back) {
            finish();
        } else if (v.getId() == R.id.login_btn_register) {
            startActivity(new Intent(this,RegisterActivity.class));
        } else if (v.getId() == R.id.login_btn_login) {
            account = accountEditText.getText().toString();
            password = passwordEditText.getText().toString();
            if (TextUtils.isEmpty(account)) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            } else {
                loadingDialog.show();
                Objects.requireNonNull(getMPresenter()).login(account,password);
            } 
        } else if (v.getId() == R.id.login_tv_reset_password) {
            startActivity(new Intent(this,ResetPasswordActivity.class));
        }
    }

    @Override
    public void loginSuccess(User user) {
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        loginSuccess = true;
        Preference.Companion.putSharedPreferences(Constant.HAS_LOGIN, true);
        Preference.Companion.putSharedPreferences(Constant.USER,user);
        startActivity(new Intent(this, SensorSelectActivity.class));
        finish();
    }

    @Override
    public void wrongAccountOrPassword() {
        passwordEditText.setText("");
        password = null;
        Toast.makeText(this, "用户名或密码错误，请重新输入", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginFailure() {
        Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        loadingDialog.show();
    }

    @Override
    public void hideLoading() {
        loadingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!registerSuccess && !loginSuccess) {
            Preference.Companion.putSharedPreferences(Constant.LOGIN_ACCOUNT, accountEditText.getText().toString());
            Preference.Companion.putSharedPreferences(Constant.LOGIN_PASSWORD, passwordEditText.getText().toString());
        } else {
            Preference.Companion.putSharedPreferences(Constant.LOGIN_ACCOUNT, "");
            Preference.Companion.putSharedPreferences(Constant.LOGIN_PASSWORD, "");
        }
    }

    @NotNull
    @Override
    protected LoginContract.Present<LoginContract.View> createPresenter() {
        return new LoginPresenter<>();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerSuccess(RegisterSuccessEvent event) {
        registerSuccess = true;
        finish();
    }
}
