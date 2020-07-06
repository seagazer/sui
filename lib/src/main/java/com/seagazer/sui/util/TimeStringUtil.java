package com.seagazer.sui.util;

import java.util.Locale;

public class TimeStringUtil {

    /**
     * 时长转字符串(9->"09",11->"11")
     *
     * @param duration 时长
     * @return
     */
    public static String timeToString(long duration) {
        int minute = (int) (duration / 1000 / 60);
        int second = (int) (duration / 1000 % 60);
        int hour = 0;
        String hourStr = "00";
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
            hourStr = hour < 10 ? String.format(Locale.CHINA, "0%d", hour) : String.valueOf(hour);
        }
        String minuteStr = minute < 10 ? String.format(Locale.CHINA, "0%d", minute) : String.valueOf(minute);
        String secondStr = second < 10 ? String.format(Locale.CHINA, "0%d", second) : String.valueOf(second);
        return hour > 0 ? hourStr + ":" + minuteStr + ":" + secondStr :
                minuteStr + ":" + secondStr;
    }

    /**
     * 数字转字符串(9->"09",11->"11")
     *
     * @param number 数字
     * @return
     */
    public static String numberToString(int number) {
        if (number > 9) {
            return String.valueOf(number);
        } else {
            return String.format(Locale.CHINA, "0%d", number);
        }
    }

}
