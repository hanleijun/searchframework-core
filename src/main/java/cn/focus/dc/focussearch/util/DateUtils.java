package cn.focus.dc.focussearch.util;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

public class DateUtils {

	/**
	 * 根据月份，获取第一天时间
	 * @param month
	 * @param pattern
	 * @return
	 */
	public static String firstDay(int month, String pattern) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        return DateFormatUtils.format(calendar, pattern);
	}
	
	/**
	 * 返回给定年月日(yyyyMMdd)的
	 * @param relativeTime
	 * @param offsetMonth
	 * @return
	 */
	public static String relativeFirstDay(String relativeTime, int offsetMonth){
		String currentYear = StringUtils.substring(relativeTime, 0, 4);
		int currentMonth = Integer.parseInt(StringUtils.substring(relativeTime, 4, 6));
		int reMonth = currentMonth+offsetMonth;
		if(reMonth<1){
			reMonth = 1;
		}
		if(reMonth > 12){
			reMonth = 12;
		}
		String monthstr = String.valueOf(reMonth);
		if(monthstr.length() == 1){
			monthstr = "0" + monthstr;
		}
		return currentYear+monthstr+"01";
	}
	
	public static String relativeLastDay(String relativeTime, int offsetMonth){
		String currentYear = StringUtils.substring(relativeTime, 0, 4);
		int currentMonth = Integer.parseInt(StringUtils.substring(relativeTime, 4, 6));
		int reMonth = currentMonth+offsetMonth;
		if(reMonth<1){
			reMonth = 1;
		}
		if(reMonth > 12){
			reMonth = 12;
		}
		String monthstr = String.valueOf(reMonth);
		if(monthstr.length() == 1){
			monthstr = "0" + monthstr;
		}
		return currentYear+monthstr+"31";
	}
	
	/**
	 * 获取最后一天时间
	 * @param month
	 * @param pattern
	 * @return
	 */
	public static String lastDay(int month, String pattern) {
		 Calendar calendar = Calendar.getInstance();
		 calendar.add(Calendar.MONTH, month);
		 calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
         return DateFormatUtils.format(calendar, pattern);
	}
	
	/**
	 * 当前时间
	 * @param pattern
	 * @return
	 */
	public static String currentDay(String pattern) {
	        return DateFormatUtils.format(Calendar.getInstance(), pattern);
	}
	
	/**
	 * 获取过一段时间之后的时间
	 * @param days
	 * @param pattern
	 * @return
	 */
	public static String afterDays(int days, String pattern) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, days);
        return DateFormatUtils.format(calendar, pattern);
	}
}
