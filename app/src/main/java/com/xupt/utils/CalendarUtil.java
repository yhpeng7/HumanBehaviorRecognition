package com.xupt.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {
    public static Integer getAgeByBirth(String birthday) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date birth = df.parse(birthday);
            Calendar cal = Calendar.getInstance();
            Calendar bir = Calendar.getInstance();
            bir.setTime(birth);
            if(cal.before(bir)){
                return 0;
            }
            int yearNow = cal.get(Calendar.YEAR);
            int monthNow = cal.get(Calendar.MONTH);
            int dayNow = cal.get(Calendar.DAY_OF_MONTH);
            int yearBirth = bir.get(Calendar.YEAR);
            int monthBirth = bir.get(Calendar.MONTH);
            int dayBirth = bir.get(Calendar.DAY_OF_MONTH);
            int age = yearNow - yearBirth;
            if(monthNow < monthBirth || (monthNow == monthBirth && dayNow < dayBirth)){
                age--;
            }
            return age;
        } catch (Exception e) {
            return 0;
        }
    }
}
