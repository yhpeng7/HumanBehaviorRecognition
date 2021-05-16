package com.xupt.mvp;

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
import com.xupt.constant.Constant;
import com.xupt.utils.CustomDialog;
import com.xupt.utils.Preference;
import com.xupt.utils.Random;
import com.xupt.service.SendEmailServer;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ResetPasswordActivity extends BaseMvpSwipeBackActivity<ResetPasswordContract.View, ResetPasswordContract.Present<ResetPasswordContract.View>>
        implements ResetPasswordContract.View, View.OnClickListener, View.OnFocusChangeListener {

    ImageView backButton;
    Button resetPasswordButton;
    EditText emailEditText;
    EditText authCodeEditText;
    EditText newPasswordEditText;
    TextView verifyEmailTextView;
    CustomDialog loadingDialog;
    boolean hasHint = false;
    String[] contents;
    int index;
    String email;
    String authCode;
    String newPassword;
    //清除重置成功时登录界面缓存的数据
    boolean resetSuccess = false;

    @Override
    public void initData() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        backButton = findViewById(R.id.reset_password_iv_back);
        resetPasswordButton = findViewById(R.id.reset_password_btn_reset);
        emailEditText = findViewById(R.id.reset_password_et_email);
        authCodeEditText = findViewById(R.id.reset_password_et_vc);
        newPasswordEditText = findViewById(R.id.reset_password_et_password);
        verifyEmailTextView = findViewById(R.id.reset_password_tv_verify_email);

        contents = new String[60];
        for (int i = 0; i < 60; i++) {
            contents[i] = (60 - i) + "s";
        }
        index = 0;

        backButton.setOnClickListener(this);
        verifyEmailTextView.setOnClickListener(this);
        resetPasswordButton.setOnClickListener(this);
        newPasswordEditText.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reset_password_tv_verify_email) {
            sendAuthCode();
        } else if (v.getId() == R.id.reset_password_btn_reset) {
            if(checkInput()){
                loadingDialog.show();
                Objects.requireNonNull(getMPresenter()).resetPassword(email, newPassword);
            }
        } else if (v.getId() == R.id.reset_password_iv_back) {
            finish();
        }
    }

    private boolean checkInput() {
        email = emailEditText.getText().toString();
        newPassword = newPasswordEditText.getText().toString();
        String authCodeInput = authCodeEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(authCodeInput)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!authCodeInput.equals(authCode)) {
            Toast.makeText(this, "验证码输入错误，请重新输入", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void resetSuccess() {
        Toast.makeText(this, "重置成功，请继续登录", Toast.LENGTH_SHORT).show();
        resetSuccess = true;
        finish();
    }

    @Override
    public void wrongEmail() {
        emailEditText.setText("");
        email = null;
        authCodeEditText.setText("");
        authCode = null;
        Toast.makeText(this, "重置失败，当前邮箱未绑定账号", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void resetFailure() {
        Toast.makeText(this, "重置失败", Toast.LENGTH_SHORT).show();
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
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.reset_password_et_password) {
            if (!hasHint && hasFocus) {
                Toast.makeText(this, "密码格式:不少于6位的数字与字母的组合，字母区分大小写", Toast.LENGTH_LONG).show();
                hasHint = true;
            }
        }
    }

    @Override
    public void start() {
        loadingDialog = new CustomDialog(this);
        emailEditText.setText((String) Preference.Companion.getSharedPreferences(Constant.RESET_PASSWORD_EMAIL, ""));
        newPasswordEditText.setText((String) Preference.Companion.getSharedPreferences(Constant.RESET_PASSWORD_NEW_PASSWORD, ""));
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
        if (!resetSuccess) {
            Preference.Companion.putSharedPreferences(Constant.RESET_PASSWORD_EMAIL, emailEditText.getText().toString());
            Preference.Companion.putSharedPreferences(Constant.RESET_PASSWORD_NEW_PASSWORD, newPasswordEditText.getText().toString());
        } else {
            Preference.Companion.putSharedPreferences(Constant.RESET_PASSWORD_EMAIL, "");
            Preference.Companion.putSharedPreferences(Constant.RESET_PASSWORD_NEW_PASSWORD, "");
        }
    }

    @NotNull
    @Override
    protected ResetPasswordContract.Present<ResetPasswordContract.View> createPresenter() {
        return new ResetPasswordPresenter<>();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_reset_password;
    }
}
