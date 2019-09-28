package com.hashtech.idata.common.monitor.utils;

import com.hashtech.businessframework.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author xuchang
 * @create 2019/9/12 10:13
 */
public final class DateCommonUtils {
    private final static String DATE_FORMART = "yyyyMMddHHmmss";
    private DateCommonUtils() {
    }

    /**
     * 获取日期中的月份
     * @param date
     * @return
     */
    public static Integer getMonth(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date is null!");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    /**
     * 获取当月的1号0时0分0秒开始
     * @param date
     * @return
     */
    public static Date getThisMonthDate(Date date) {
        Integer month = getMonth(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    /**
     * 一个星期的第几天 1:周日，2：周一
     * @param date
     * @return
     */
    public static Integer getWeekDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }
    /**
     * 构建当周1的0时0分0开始的时间
     * @param date
     * @return
     */
    public static Date getThisWeekDate(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取时间段的每一天
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Date> getEveryDayOnTime(Date startDate, Date endDate) {
        List<Date> daysStrList = new ArrayList<Date>();
        startDate = getFirstSecondOfDate(startDate);
        endDate = getFirstSecondOfDate(endDate);
        if (startDate.getTime()==endDate.getTime()){
            daysStrList.add(startDate);
        }else {
            daysStrList.add(startDate);
            Calendar calBegin = Calendar.getInstance();
            calBegin.setTime(startDate);
            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(endDate);
            while (endDate.after(calBegin.getTime())) {
                calBegin.add(Calendar.DAY_OF_MONTH, 1);
                daysStrList.add(getFirstSecondOfDate(calBegin.getTime()));
            }
        }
        return daysStrList;
    }

    /**
     * 获取近day参数天数的每一天
     * @param day
     * @return
     */
    public static List<Date> getNearlyDays(Integer day) {
        Date endDate = new Date();
        endDate = getFirstSecondOfDate(endDate);
        Date startDate = DateUtils.addDate(endDate,-day);
        List<Date> daysStrList = new ArrayList<Date>();
        daysStrList.add(startDate);
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(startDate);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(endDate);
        while (endDate.after(calBegin.getTime())) {
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            daysStrList.add(calBegin.getTime());
        }
        return daysStrList;
    }
    public static Date getFirstSecondOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    public static void main(String[] args) {
        Date now = new Date();
        Date test = getThisWeekDate(now);
        System.out.println(test);

        String startTime = "20190326000000";
        String endTime  =  "20190329000000";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMART);
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.parse(startTime);
            endDate = sdf.parse(endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(Date days: getEveryDayOnTime(startDate,endDate)){
            System.out.println(days);
        }
    }
}
