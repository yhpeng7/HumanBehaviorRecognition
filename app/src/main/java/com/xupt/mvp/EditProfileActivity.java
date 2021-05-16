
package com.xupt.mvp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.xupt.R;
import com.xupt.base.BaseMvpSwipeBackActivity;
import com.xupt.bean.User;
import com.xupt.constant.Constant;
import com.xupt.event.UpdateProfileSuccessEvent;
import com.xupt.utils.CalendarUtil;
import com.xupt.utils.CustomDialog;
import com.xupt.utils.GlideEngine;
import com.xupt.utils.Preference;
import com.xupt.utils.Random;
import com.xupt.service.SendEmailServer;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends BaseMvpSwipeBackActivity<EditProfileContract.View, EditProfileContract.Present<EditProfileContract.View>>
        implements EditProfileContract.View, View.OnClickListener, RadioGroup.OnCheckedChangeListener, View.OnFocusChangeListener {

    CircleImageView avatarImageView;
    EditText usernameEditText;
    EditText emailEditText;
    TextView verifyEmailTextView;
    EditText authCodeEditText;
    EditText ageEditText;
    RadioGroup genderRadioGroup;
    EditText regionEditText;
    Button commitButton;
    ImageView backImageView;

    CustomDialog loadingDialog;
    DatePickerDialog datePickerDialog;
    CityPickerView cityPicker;

    User user;

    String avatarPath;
    String avatar;
    String email;
    String birthday;
    int gender = -1;
    String region;

    boolean hasHint = false;

    int index;
    String[] contents;

    String authCode;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.edit_profile_avatar) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .selectionMode(PictureConfig.SINGLE)
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .enableCrop(true)
                    .circleDimmedLayer(true)
                    .theme(R.style.picture_WeChat_style)
                    .setCircleStrokeWidth(7)
                    .freeStyleCropEnabled(true)
                    .isGif(false)
                    .isDragFrame(true)
                    .showCropFrame(false)
                    .showCropGrid(false)
                    .rotateEnabled(false)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        } else if (v.getId() == R.id.edit_profile_et_age) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                datePickerDialog.show();
            }
        } else if (v.getId() == R.id.edit_profile_et_region) {
            cityPicker.showCityPicker();
        } else if (v.getId() == R.id.edit_profile_tv_verify_email) {
            sendAuthCode();
        } else if (v.getId() == R.id.edit_profile_btn_reset) {
            if (checkInput()) {
                if (!TextUtils.isEmpty(avatar)) {
                    user.setAvatar(avatar);
                }
                String username = usernameEditText.getText().toString();
                if (!TextUtils.isEmpty(username) && !username.equals(user.getEmail())) {
                    user.setUsername(username);
                }
                if (!TextUtils.isEmpty(email) && !user.getEmail().equals(email)) {
                    user.setEmail(email);
                }
                if (!TextUtils.isEmpty(birthday) && !birthday.equals(user.getBirthday())) {
                    user.setBirthday(birthday);
                }
                user.setGender(gender);
                if (!TextUtils.isEmpty(region) && !region.equals(user.getRegion())) {
                    user.setRegion(region);
                }
                User oldUser = Preference.Companion.getSharedPreferences(Constant.USER, null);
                if (!isEquals(user,oldUser ,false)) {
                    loadingDialog.show();
                    getMPresenter().updateProfile(user.toString(), avatarPath, avatar, isEquals(user, oldUser, true));
                } else {
                    Toast.makeText(this, "未修改资料请勿提交", Toast.LENGTH_SHORT).show();
                } 
            }
        } else if (v.getId() == R.id.edit_profile_iv_back) {
            finish();
        }
    }

    public boolean isEquals(User user1, User user2,boolean eliminateAvatar) {
        boolean equals;
        if (user1 == null && user2 == null) {
            equals = true;
        } else if (user1 == null || user2 == null) {
            equals = false;
        } else {
            equals = (user1.getBirthday() == null ? user2.getBirthday() == null : user1.getBirthday().equals(user2.getBirthday()))
                    && (user1.getGender() == user2.getGender())
                    && (user1.getRegion() == null ? user2.getRegion() == null : user1.getRegion().equals(user2.getRegion()))
                    && (user1.getUsername() == null ? user2.getUsername() == null : user1.getUsername().equals(user2.getUsername()))
                    && (user1.getEmail() == null ? user2.getEmail() == null : user1.getEmail().equals(user2.getEmail()));
            if (!eliminateAvatar) {
                equals = equals && TextUtils.isEmpty(avatar);
            }
        }
        return equals;
    }
    
    @Override
    public void updateSuccess() {
        Toast.makeText(this, "修改资料成功", Toast.LENGTH_SHORT).show();
        Preference.Companion.putSharedPreferences(Constant.USER, user);
        EventBus.getDefault().post(new UpdateProfileSuccessEvent(user));
        finish();
    }

    @Override
    public void updateFailure(String type) {
        if ("failure".equals(type)) {
            Toast.makeText(this, "修改资料失败", Toast.LENGTH_SHORT).show();
        } else if ("avatarFailure".equals(type)) {
            Toast.makeText(this, "修改头像失败", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkInput() {
        String authCodeInput = authCodeEditText.getText().toString();
        if (!TextUtils.isEmpty(email) && !user.getEmail().equals(email)) {
            if (TextUtils.isEmpty(authCodeInput)) {
                Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!authCodeInput.equals(authCode)) {
                Toast.makeText(this, "验证码输入错误，请重新输入", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void sendAuthCode() {
        String emailInput = emailEditText.getText().toString();
        if (TextUtils.isEmpty(emailInput)) {
            Toast.makeText(this, "请输入正确的邮箱地址", Toast.LENGTH_SHORT).show();
        } else if (user.getEmail().equals(emailInput)) {
            Toast.makeText(this, "此邮箱已验证，无需重复验证", Toast.LENGTH_SHORT).show();
        } else {
            email = emailInput;
            authCode = Random.randomVerifyCode();
            new SendEmailServer().execute(emailInput, "验证码", authCode);
            Toast.makeText(this, "邮件已发送，请注意查收", Toast.LENGTH_SHORT).show();
            countDown();
        }
    }

    public void countDown() {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                avatarPath = PictureSelector.obtainMultipleResult(data).get(0).getCutPath();
                String avatarFileName = PictureSelector.obtainMultipleResult(data).get(0).getFileName();
                avatar = user.getId() + avatarFileName.substring(avatarFileName.lastIndexOf("."));
                GlideEngine.createGlideEngine().loadFolderImage(this, avatarPath, avatarImageView);
            }
        }
    }

    @Override
    public void initData() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        avatarImageView = findViewById(R.id.edit_profile_avatar);
        usernameEditText = findViewById(R.id.edit_profile_et_username);
        emailEditText = findViewById(R.id.edit_profile_et_email);
        verifyEmailTextView = findViewById(R.id.edit_profile_tv_verify_email);
        authCodeEditText = findViewById(R.id.edit_profile_et_vc);
        ageEditText = findViewById(R.id.edit_profile_et_age);
        genderRadioGroup = findViewById(R.id.edit_profile_rg_gender);
        regionEditText = findViewById(R.id.edit_profile_et_region);
        commitButton = findViewById(R.id.edit_profile_btn_reset);
        backImageView = findViewById(R.id.edit_profile_iv_back);

        contents = new String[60];
        for (int i = 0; i < 60; i++) {
            contents[i] = (60 - i) + "s";
        }
        index = 0;

        backImageView.setOnClickListener(this);
        verifyEmailTextView.setOnClickListener(this);
        avatarImageView.setOnClickListener(this);
        ageEditText.setOnClickListener(this);
        commitButton.setOnClickListener(this);
        regionEditText.setOnClickListener(this);
        genderRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void start() {
        cityPicker = new CityPickerView();
        cityPicker.init(this);
        loadingDialog = new CustomDialog(this);
        user = Preference.Companion.getSharedPreferences(Constant.USER, null);
        if (user != null) {
            usernameEditText.setText(user.getUsername());
            emailEditText.setText(user.getEmail());
            if (user.getBirthday() != null) {
                ageEditText.setText(String.valueOf(CalendarUtil.getAgeByBirth(user.getBirthday())));
            }
            if (user.getGender() == 1) {
                genderRadioGroup.check(R.id.edit_profile_rb_male);
            } else if (user.getGender() == 0) {
                genderRadioGroup.check(R.id.edit_profile_rb_female);
            }
            if (user.getRegion() != null) {
                regionEditText.setText(user.getRegion());
            }
            if (user.getAvatar() != null) {
                GlideEngine.createGlideEngine().loadImage(this, Constant.BASE_URL + "/avatar/" + user.getAvatar(), avatarImageView);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialog);
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    birthday = year + "-" + month + "-" + dayOfMonth;
                    ageEditText.setText(String.valueOf(CalendarUtil.getAgeByBirth(birthday)));
                }
            });
        } else {
            ageEditText.setFocusableInTouchMode(true);
            ageEditText.setFocusable(true);
            ageEditText.requestFocus();
            ageEditText.setOnFocusChangeListener(this);
        }

        CityConfig cityConfig = new CityConfig.Builder()
                .titleBackgroundColor("#879ED0")
                .confirTextColor("#D81B60")
                .titleTextColor("#FEEA3B")
                .cancelTextColor("#FFFFFF")
                .showBackground(true)
                .build();
        cityPicker.setConfig(cityConfig);
        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                region = province.getName() + "-" + city.getName() + "-" + district.getName();
                regionEditText.setText(region);
            }

            @Override
            public void onCancel() {
            }
        });
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.edit_profile_rb_male) {
            gender = 1;
        } else if (checkedId == R.id.edit_profile_rb_female) {
            gender = 0;
        }
    }

    @NotNull
    @Override
    protected EditProfileContract.Present<EditProfileContract.View> createPresenter() {
        return new EditProfilePresenter<>();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_edit_profile;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.edit_profile_et_age) {
            if (!hasHint && hasFocus) {
                Toast.makeText(this, "格式:2012-3-29，月份从0开始", Toast.LENGTH_LONG).show();
                hasHint = true;
            }
        }
    }
}