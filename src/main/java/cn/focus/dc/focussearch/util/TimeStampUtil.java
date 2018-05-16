package cn.focus.dc.focussearch.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class TimeStampUtil {
	
	public static final String DATE_PATTERN = "yyyyMMdd";
	
	public static final String TIME_PATTERN = "yyyyMMddhhmmss";

	public static Long dateFormat(Long date) {
		long javaTimeStamp = date * 1000l;
		return Long.parseLong(format(javaTimeStamp, DATE_PATTERN));
	}
	
	public static Long timeFormat(Long date) {
		long javaTimeStamp = date * 1000l;
		return Long.parseLong(format(javaTimeStamp, TIME_PATTERN));
	}
	
	public static String format(long date, String pattern) {
		return format(new Date(date), pattern);
	}
	
    public static String format(Date date, String pattern) {
        DateFormat df = createDateFormat(pattern);
        return df.format(date);
    }
    
    private static DateFormat createDateFormat(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
/*        TimeZone gmt = TimeZone.getTimeZone("GMT");
        sdf.setTimeZone(gmt);
        sdf.setLenient(true);*/
        return sdf;
    }
    
    public static long dateAdd(long date) {
    	long javaTimeStamp = date * 1000l;
		Calendar now = Calendar.getInstance();  
		now.setTimeInMillis(javaTimeStamp);
		now.add(Calendar.DATE, +1);
		return Long.parseLong(format(now.getTime(), DATE_PATTERN));
    }
}
