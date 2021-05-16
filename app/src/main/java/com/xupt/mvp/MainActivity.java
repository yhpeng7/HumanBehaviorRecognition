package com.xupt.mvp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.xupt.R;
import com.xupt.adapter.ViewPagerAdapter;
import com.xupt.base.BaseMvpActivity;
import com.xupt.bean.User;
import com.xupt.constant.Constant;
import com.xupt.event.RequestTypeChangeEvent;
import com.xupt.event.ServerChangeEvent;
import com.xupt.event.UpdateProfileSuccessEvent;
import com.xupt.service.CollectService;
import com.xupt.ui.SensorSelectActivity;
import com.xupt.ui.ServerSelectActivity;
import com.xupt.utils.CalendarUtil;
import com.xupt.utils.CustomDialog;
import com.xupt.utils.GlideEngine;
import com.xupt.utils.NetworkUtil;
import com.xupt.utils.Preference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends BaseMvpActivity<MainContract.View, MainContract.Present<MainContract.View>>
        implements MainContract.View, View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    FloatingActionButton faButton;
    NavigationView navigationView;
    ConstraintLayout contentView;

    ImageView mainAvatar;
    TextView mainUsername;
    TextView mainEmail;
    MenuItem mainAge;
    MenuItem mainGender;
    MenuItem mainRegion;

    ViewPager viewPager;

    CustomDialog loadingDialog;

    User user;

    int serverType;

    Intent startServiceIntent;
    CollectService service;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((CollectService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initData() {
        if (NetworkUtil.Companion.isNetworkConnect(this)) {
            if (!NetworkUtil.Companion.isWifi(this)) {
                    Toast.makeText(this, "非Wifi网络，请注意流量使用", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "请在有网环境下使用本软件", Toast.LENGTH_LONG).show();
            finish();
        }

        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("数据收集");
        setSupportActionBar(toolbar);
        contentView = findViewById(R.id.main_content_view);
        faButton = findViewById(R.id.main_fab);
        drawer = findViewById(R.id.main_drawer_layout);
        navigationView = findViewById(R.id.main_nav_view);

        View headView = navigationView.getHeaderView(0);
        mainAvatar = headView.findViewById(R.id.main_avatar);
        mainUsername = headView.findViewById(R.id.main_username);
        mainEmail = headView.findViewById(R.id.main_email);

        Menu navigationMenu = navigationView.getMenu();
        mainAge = navigationMenu.getItem(0);
        mainGender = navigationMenu.getItem(1);
        mainRegion = navigationMenu.getItem(2);

        loadingDialog = new CustomDialog(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.main_view_pager);
        viewPager.setAdapter(adapter);

        mainAvatar.setOnClickListener(this);
        faButton.setOnClickListener(this);
        drawer.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);
        viewPager.addOnPageChangeListener(this);

        toggle.syncState();
    }

    @Override
    public void start() {
        showInfo(null);
        startServiceIntent = new Intent(this, CollectService.class);

        startService(startServiceIntent);
        bindService(startServiceIntent, connection, Service.BIND_AUTO_CREATE);
    }

    @Subscribe
    public void showInfo(UpdateProfileSuccessEvent event) {
        if (event == null) {
            user = Preference.Companion.getSharedPreferences(Constant.USER, null);
        } else {
            user = event.getUser();
        }
        if (user.getAvatar() != null) {
            GlideEngine.createGlideEngine().loadImage(this, Constant.BASE_URL + "/avatar/" + user.getAvatar(), mainAvatar);
        } else {
            Glide.with(this).load(R.drawable.ic_avatar).into(mainAvatar);
        }
        mainUsername.setText(user.getUsername());
        mainEmail.setText(user.getEmail());
        mainAge.setTitle(String.valueOf(CalendarUtil.getAgeByBirth(user.getBirthday())));
        if (user.getGender() == 1) {
            mainGender.setTitle("男");
        } else if(user.getGender() == 0){
            mainGender.setTitle("女");
        } else if (user.getGender() == -1) {
            mainGender.setTitle("未知");
        }
        mainRegion.setTitle(user.getRegion());
        serverType = Preference.Companion.getSharedPreferences(Constant.SERVER_TYPE, 0);
    }

    @Subscribe
    public void onServerChange(ServerChangeEvent event) {
        serverType = event.getServerType();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_setting_profile) {
            startActivity(new Intent(this, EditProfileActivity.class));
        } else if (id == R.id.nav_choose_server) {
            startActivity(new Intent(this, ServerSelectActivity.class));
        } else if (id == R.id.nav_choose_sensor) {
            Intent intent = new Intent(this, SensorSelectActivity.class);
            intent.putExtra("MainActivity", "MainActivity");
            startActivity(intent);
        }
        return false;
    }

    void logout() {
        Preference.Companion.putSharedPreferences(Constant.USER, null);
        Preference.Companion.putSharedPreferences(Constant.HAS_LOGIN, false);
        Preference.Companion.putSharedPreferences(Constant.SERVER_TYPE, 0);
        Preference.Companion.putSharedPreferences(Constant.SENSOR_NAMES, new ArrayList<String>());
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void cancelSuccess() {
        Toast.makeText(this, "注销成功", Toast.LENGTH_SHORT).show();
        Preference.Companion.putSharedPreferences(Constant.USER, null);
        Preference.Companion.putSharedPreferences(Constant.HAS_LOGIN, false);
        Preference.Companion.putSharedPreferences(Constant.SERVER_TYPE, 0);
        Preference.Companion.putSharedPreferences(Constant.SENSOR_NAMES, new ArrayList<String>());
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    @Override
    public void cancelFailure() {
        Toast.makeText(this, "注销失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_fab) {
            if (serverType == 1) {
                Toast.makeText(this, "Email服务器下行为解锁功能不可用", Toast.LENGTH_LONG).show();
            } else {
                Snackbar.make(v, "锁屏后可由本人通过步行解锁", Snackbar.LENGTH_LONG)
                        .setAction("锁屏", null).show();
            }
        } else if (v.getId() == R.id.main_avatar) {
            startActivity(new Intent(this, EditProfileActivity.class));
        }
    }

    @NotNull
    @Override
    protected MainContract.Present<MainContract.View> createPresenter() {
        return new MainPresenter<>();
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_main;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Toast.makeText(this, "Hello World", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            logout();
        } else if (id == R.id.action_cancel_account) {
            loadingDialog.show();
            User user = (User) Preference.Companion.getSharedPreferences(Constant.USER, null);
            Objects.requireNonNull(getMPresenter()).cancelAccount(String.valueOf(user.getId()));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            toolbar.setTitle("数据收集");
            EventBus.getDefault().post(new RequestTypeChangeEvent(0));
        } else if (position == 1) {
            if (serverType == 1) {
                Toast.makeText(this, "Email服务器下行为识别功能不可用", Toast.LENGTH_LONG).show();
            } else {
                toolbar.setTitle("行为识别");
                EventBus.getDefault().post(new RequestTypeChangeEvent(1));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        stopService(startServiceIntent);
        unbindService(connection);
        super.onDestroy();
    }
}
