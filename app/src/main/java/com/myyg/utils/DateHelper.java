package com.myyg.utils;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by JOHN on 2015/11/15.
 */
public class DateHelper {
    private final static String TAG = DateHelper.class.getSimpleName();

    public static SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat format_HHMM = new SimpleDateFormat("HH:mm");

    public static SimpleDateFormat format_YYYYMMMDDHHMM = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static SimpleDateFormat format_YYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

    public static SimpleDateFormat format_MMDDHHMM = new SimpleDateFormat("MM-dd HH:mm");

    /**
     * @param date
     * @return
     */
    public static String getDefaultDate(Object date) {
        String strDate = defaultFormat.format(date);
        return strDate;
    }

    /**
     * @param date
     * @return
     */
    public static String getHHMM(Object date) {
        String strDate = format_HHMM.format(date);
        return strDate;
    }

    /**
     * @param date
     * @return
     */
    public static String getYYYYMMMDDHHMM(Object date) {
        String strDate = format_YYYYMMMDDHHMM.format(date);
        return strDate;
    }

    /**
     * @param date
     * @return
     */
    public static String getYYYYMMDD(Object date) {
        String strDate = format_YYYYMMDD.format(date);
        return strDate;
    }

    /**
     * @param date
     * @param format
     * @return
     */
    public static String getCustomerDate(Object date, SimpleDateFormat format) {
        try {
            String strTime = format.format(date);
            return strTime;
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getCustomerDate(long timeMillis) {
        String ret = "";
        try {
            Calendar now = Calendar.getInstance();
            long ms = 1000 * (now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND));//毫秒数
            long ms_now = now.getTimeInMillis();
            String time = format_HHMM.format(new Date(timeMillis));
            if (ms_now - timeMillis < ms) {
                ret = MessageFormat.format("今天 {0}", time);
            } else if (ms_now - timeMillis < (ms + 24 * 3600 * 1000)) {
                ret = MessageFormat.format("昨天 {0}", time);
            } else if (ms_now - timeMillis < (ms + 24 * 3600 * 1000 * 2)) {
                ret = MessageFormat.format("前天 {0}", time);
            } else {
                Date date = new Date(timeMillis);
                ret = format_MMDDHHMM.format(date);
            }
            return ret;
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getWeekOfDate(Object date) {
        String[] weekDaysName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(defaultFormat.parse(String.valueOf(date)));
            int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            return weekDaysName[intWeek];
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static String changeDateFormet(String date) {
        return date.replaceAll("-", "/").substring(5, 10);
    }

    /**
     * 日期转换成字符串
     *
     * @param date
     * @return str
     */
    public static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date StringToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param sDate
     * @param eDate
     * @return
     * @throws ParseException
     */
    public static int daysBetween(Date sDate, Date eDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sDate = sdf.parse(sdf.format(sDate));
            eDate = sdf.parse(sdf.format(eDate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(sDate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(eDate);
            long time2 = cal.getTimeInMillis();
            long between_days = (time2 - time1) / (1000 * 3600 * 24);
            return Integer.parseInt(String.valueOf(between_days));
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * 获取两时间相隔的分钟
     *
     * @param sDate 开始时间
     * @param eDate 截至时间
     * @return
     */
    public static long minutesBetween(Date sDate, Date eDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sDate = sdf.parse(sdf.format(sDate));
            eDate = sdf.parse(sdf.format(eDate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(sDate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(eDate);
            long time2 = cal.getTimeInMillis();
            long between_minutes = (time2 - time1) / (1000 * 60);
            return between_minutes;
        } catch (Exception ex) {
            return 0;
        }
    }

}
