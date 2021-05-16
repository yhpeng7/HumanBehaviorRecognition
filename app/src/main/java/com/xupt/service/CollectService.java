package com.xupt.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.xupt.bean.SensorDataBean;
import com.xupt.bean.User;
import com.xupt.constant.Constant;
import com.xupt.event.DataTypeChangeEvent;
import com.xupt.event.IsCollectEvent;
import com.xupt.event.RequestTypeChangeEvent;
import com.xupt.event.SensorChangeEvent;
import com.xupt.event.ServerChangeEvent;
import com.xupt.event.UpdateProfileSuccessEvent;
import com.xupt.utils.Calculate;
import com.xupt.utils.CalendarUtil;
import com.xupt.utils.Preference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE;
import static android.hardware.Sensor.TYPE_GRAVITY;
import static android.hardware.Sensor.TYPE_GYROSCOPE;
import static android.hardware.Sensor.TYPE_LIGHT;
import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
import static android.hardware.Sensor.TYPE_MAGNETIC_FIELD;
import static android.hardware.Sensor.TYPE_ORIENTATION;
import static android.hardware.Sensor.TYPE_PRESSURE;

public class CollectService extends Service implements SensorEventListener {
    List<String> sensorNames = new ArrayList<>();

    //0 Web服务器   1 email服务器   默认Web服务器
    int serverType;
    //0 数据存储   1 行为识别   2 运动解锁
    int requestType = 0;
    //0 走   1 跑   2 跳   3 上楼   4 下楼   -1 未知状态
    int dataType = -1;

    String emailDataReceiver;
    User user;
    SensorDataBean sensorData;

    String userId;

    StringBuffer sb = new StringBuffer();
    StringBuffer sb1 = new StringBuffer();
    StringBuffer sb2 = new StringBuffer();
    StringBuffer sb3 = new StringBuffer();
    StringBuffer sb4 = new StringBuffer();
    StringBuffer sb5 = new StringBuffer();
    StringBuffer sb6 = new StringBuffer();
    StringBuffer sb7 = new StringBuffer();
    StringBuffer sb8 = new StringBuffer();
    StringBuffer sb9 = new StringBuffer();

    private int nu = 200;
    int count = 0;//计步数
    int emailNum = 0;//发送邮件数
    int num1 = 0, num2 = 0, num3 = 0, num4 = 0, num5 = 0, num6 = 0, num7 = 0, num8 = 0, num9 = 0;
    int sb_length;
    //存放三轴数据
    double[] oriValues = new double[3];
    SensorManager sensorManager;

    @Subscribe
    public void onSensorChange(SensorChangeEvent event) {
        sensorNames = event.getSensorName();
    }

    @Subscribe
    public void onServerChange(ServerChangeEvent event) {
        serverType = event.getServerType();
        if (serverType == 1) {
            emailDataReceiver = event.getReceiverEmailAddress();
        }
    }

    @Subscribe
    public void onRequestChange(RequestTypeChangeEvent event) {
        requestType = event.getRequestChangeEvent();
    }

    @Subscribe
    public void onDataTypeChange(DataTypeChangeEvent event) {
        dataType = event.getDataType();
    }

    @Subscribe
    public void onUpdateProfile(UpdateProfileSuccessEvent event) {
        user = event.getUser();
        userId = String.valueOf(user.getId());
        sensorData.setAge(CalendarUtil.getAgeByBirth(user.getBirthday()));
        sensorData.setGender(user.getGender());
        sensorData.setRegion(user.getRegion());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Data(sensorEvent);//收集其他传感器的数据
        boolean collect = (!sensorNames.contains("加速度传感器") || (sensorNames.contains("加速度传感器") && num1 >= nu))
                && (!sensorNames.contains("陀螺仪传感器") || (sensorNames.contains("陀螺仪传感器") && num2 >= nu))
                && (!sensorNames.contains("重力传感器") || (sensorNames.contains("重力传感器") && num3 >= nu))
                && (!sensorNames.contains("线性加速度传感器") || (sensorNames.contains("线性加速度传感器") && num4 >= nu))
                && (!sensorNames.contains("磁场传感器") || (sensorNames.contains("磁场传感器") && num6 >= nu))
                && (!sensorNames.contains("方向传感器") || (sensorNames.contains("方向传感器") && num8 >= nu))
                || num9 >= nu;
        if (collect) {
            for (int i = 0; i < sensorNames.size(); i++) {
                if (sensorNames.get(i).equals("加速度传感器")) {
                    sb = sb.append("TYPE_ACCELEROMETER:").append(sb1).append("\n");
                }
                if (sensorNames.get(i).equals("陀螺仪传感器")) {
                    sb = sb.append("TYPE_GYROSCOPE:").append(sb2).append("\n");
                }
                if (sensorNames.get(i).equals("重力传感器")) {
                    sb = sb.append("TYPE_GRAVITY:").append(sb3).append("\n");
                }
                if (sensorNames.get(i).equals("线性加速度传感器")) {
                    sb = sb.append("TYPE_LINEAR_ACCELERATION:").append(sb4).append("\n");
                }
                if (sensorNames.get(i).equals("光线传感器")) {
                    sb = sb.append("TYPE_LIGHT:").append(sb5).append("\n");
                }
                if (sensorNames.get(i).equals("磁场传感器")) {
                    sb = sb.append("TYPE_MAGNETIC_FIELD:").append(sb6).append("\n");
                }
                if (sensorNames.get(i).equals("温度传感器")) {
                    sb = sb.append("TYPE_AMBIENT_TEMPERATURE:").append(sb7).append("\n");
                }
                if (sensorNames.get(i).equals("方向传感器")) {
                    sb = sb.append("TYPE_ORIENTATION:").append(sb8).append("\n");
                }
                if (sensorNames.get(i).equals("压力传感器")) {
                    sb = sb.append("TYPE_PRESSURE:").append(sb9).append("\n");
                }
            }
            sensorData.setBehaviorType(dataType);
            sensorData.setSensorData(sb.toString());
            if (requestType == 0) {
                //已选择运动状态
                if (dataType != -1) {
                    EventBus.getDefault().post(new IsCollectEvent(0));
                    //Web服务器
                    if (serverType == 0) {
                        new SendWebDataServer().execute(userId, sensorData.toString());
                    } else if (serverType == 1) {
                        //邮件服务器
                        new SendEmailServer().execute(emailDataReceiver, "userId:" + user.getId() + ",email:" + user.getEmail(), sensorData.toString());
                    }
                } else {
                    EventBus.getDefault().post(new IsCollectEvent(2));
                }
            } else if (requestType == 1) {
                if (serverType != 1) {
                    new SendWebRecognitionServer().execute(userId,sensorData.toString());
                }
            } else if (requestType == 2) {
                if (serverType != 1) {
                    new SendWebUnlockServer().execute(userId,sensorData.toString());
                }
            }
            num1 = 0;
            num2 = 0;
            num3 = 0;
            num4 = 0;
            num5 = 0;
            num6 = 0;
            deleteStringBuffer(sb);
            deleteStringBuffer(sb1);
            deleteStringBuffer(sb2);
            deleteStringBuffer(sb3);
            deleteStringBuffer(sb4);
            deleteStringBuffer(sb5);
            deleteStringBuffer(sb6);
            deleteStringBuffer(sb7);
            deleteStringBuffer(sb8);
            deleteStringBuffer(sb9);
        }
    }

    private void deleteStringBuffer(StringBuffer sb) {
        sb_length = sb.length();// 取得字符串的长度
        sb.delete(0, sb_length);    //删除字符串从0~sb_length-1处的内容 (这个方法就是用来清除StringBuffer中的内容的)
    }

    @Override
    public void onCreate() {
        //获取系统的传感器管理服务
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        super.onCreate();
        EventBus.getDefault().register(this);

        serverType = Preference.Companion.getSharedPreferences(Constant.SERVER_TYPE, 0);
        sensorNames = Preference.Companion.getSharedPreferences(Constant.SENSOR_NAMES, new ArrayList<>());
        emailDataReceiver = Preference.Companion.getSharedPreferences(Constant.EMAIL_DATA_RECEIVER, "2373705751@qq.com");
        user = Preference.Companion.getSharedPreferences(Constant.USER, new User());

        sensorData = new SensorDataBean();
        sensorData.setAge(CalendarUtil.getAgeByBirth(user.getBirthday()));
        sensorData.setGender(user.getGender());
        sensorData.setRegion(user.getRegion());
        userId = String.valueOf(user.getId());
    }

    private void Data(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {

            case TYPE_ACCELEROMETER://加速度传感器
                for (int i = 0; i < 3; i++) {
                    oriValues[i] = sensorEvent.values[i];//把三维数据取出来放入自定义的数组中
                }
                sb1.append(oriValues[0]).append(",");
                sb1.append(oriValues[1]).append(",");
                sb1.append(oriValues[2]).append(";");
                num1++;//收集一条，num加1
                break;
            case TYPE_GYROSCOPE://陀螺仪传感器
                for (int i = 0; i < 3; i++) {
                    oriValues[i] = sensorEvent.values[i];//把三维数据取出来放入自定义的数组中
                }
                sb2.append(oriValues[0]).append(",");
                sb2.append(oriValues[1]).append(",");
                sb2.append(oriValues[2]).append(";");
                num2++;//收集一条，num加1
                break;
            case TYPE_GRAVITY://重力传感器
                for (int i = 0; i < 3; i++) {
                    oriValues[i] = sensorEvent.values[i];//把三维数据取出来放入自定义的数组中
                }
                sb3.append(oriValues[0]).append(",");
                sb3.append(oriValues[1]).append(",");
                sb3.append(oriValues[2]).append(";");
                num3++;//收集一条，num加1
                break;
            case TYPE_LINEAR_ACCELERATION://线性加速度传感器
                for (int i = 0; i < 3; i++) {
                    oriValues[i] = sensorEvent.values[i];//把三维数据取出来放入自定义的数组中
                }
                sb4.append(oriValues[0]).append(",");
                sb4.append(oriValues[1]).append(",");
                sb4.append(oriValues[2]).append(";");
                num4++;//收集一条，num加1
                break;
            case TYPE_LIGHT://光线传感线
                sb5.append(sensorEvent.values[0]).append(";");
//                num5++;//收集一条，num加1
                break;
            case TYPE_MAGNETIC_FIELD://磁场传感器
                for (int i = 0; i < 3; i++) {
                    oriValues[i] = sensorEvent.values[i];//把三维数据取出来放入自定义的数组中
                }
                sb6.append(oriValues[0]).append(",");
                sb6.append(oriValues[1]).append(",");
                sb6.append(oriValues[2]).append(";");
                num6++;//收集一条，num加1
                break;
            case TYPE_AMBIENT_TEMPERATURE://温度传感器
                oriValues[0] = sensorEvent.values[0];//把三维数据取出来放入自定义的数组中
                sb7.append(oriValues[0]).append(";");
                num7++;
                break;
            case TYPE_ORIENTATION://方向传感器
                for (int i = 0; i < 3; i++) {
                    oriValues[i] = sensorEvent.values[i];//把三维数据取出来放入自定义的数组中
                }
                sb8.append(oriValues[0]).append(",");
                sb8.append(oriValues[1]).append(",");
                sb8.append(oriValues[2]).append(";");
                num8++;//收集一条，num加1
                break;
            case TYPE_PRESSURE://压力传感器
                for (int i = 0; i < 3; i++) {
                    oriValues[i] = sensorEvent.values[i];//把三维数据取出来放入自定义的数组中
                }
                sb9.append(oriValues[0]).append(",");
                sb9.append(oriValues[1]).append(",");
                sb9.append(oriValues[2]).append(";");
                num9++;//收集一条，num加1
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class MyBinder extends Binder {
        public CollectService getService() {
            return CollectService.this;
        }
    }

    //service被启动时回调该方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //为系统的线性加速度传感器注册监听器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
        // 为加速度传感器注册监听器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        // 为重力加速度传感器注册监听器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);
        // 为温度传感器注册监听器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_GAME);
        // 为陀螺仪传感器注册监听器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
        // 为光线传感器注册监听器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(TYPE_LIGHT), SensorManager.SENSOR_DELAY_GAME);
        // 为磁场传感器注册监听器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        // 为方向传感器注册监听器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        // 为压力传感器注册监听器
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(TYPE_PRESSURE), SensorManager.SENSOR_DELAY_GAME);

        return START_STICKY;//统有足够多资源的时候，就会重新开启service，保证后台运行
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        emailNum = 0;//重置发送邮箱数
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}