package cn.focus.dc.focussearch.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ColumnMapperToolUtils {
	
	private final static String COMMA = ",";
	
	private final static int ZERO = 0;

	/**
	 * 将坐标转化为es geo坐标
	 * @param lon
	 * @param lat
	 * @return
	 */
	public static List<Double> convertGeoPoint(String lon, String lat) {
		if (StringUtils.isBlank(lon) || StringUtils.isBlank(lat)) {
			return null;
		}
		return Arrays.asList(new Double[] {Double.valueOf(lon), Double.valueOf(lat)});
	}
	
	/**
	 * 对于两个int型字段共同构成主键的情况
	 * @param int1
	 * @param int2
	 * @return
	 */
	public static String mixedKey(Integer int1, Integer int2){
		String key = String.valueOf(int1)+String.valueOf(int2);
		return key;
	}
	/**
	 * 将PC端数据库中的unix时间戳转化为java时间戳
	 * @param unixTime
	 * @return
	 */
	public static Long convertJavaTimestamp(Long unixTime) {
		return unixTime == null ? null : unixTime * 1000;
	}
	
	public static List<String> split2Str(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		return Arrays.asList(StringUtils.split(str, COMMA));
	}
	
	/**
	 * 先获取房源的朝向，如果为空，则关联获取户型的朝向，如果都为空则为空
	 */
	public static List<Integer> directionProcessor(String houseDirec, String typeDirec){
		String direction = "";
		if(StringUtils.isNotBlank(houseDirec)){
			direction  = houseDirec;
		}else if(StringUtils.isNotBlank(typeDirec)){
			if(typeDirec.equals("0")){
				direction = "";
			}else{
				direction = typeDirec;
			}
		}
		return split2Int(direction);
	}
	
	/**
	 * 转化为int
	 * @param str
	 * @return
	 */
	public static List<Integer> split2Int(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		List<Integer> outputlist = new ArrayList<Integer>();
		CollectionUtils.collect(Arrays.asList(StringUtils.split(str, COMMA)), new Transformer() {

			@Override
			public Object transform(Object input) {
				return Integer.valueOf(input.toString());
			}
			
		}, outputlist);
		Collections.sort(outputlist);
		return outputlist;
	}
	
	public static Double orderTake(BigDecimal num1, BigDecimal num2) {
		if (isNotNullAndZero(num1)) {
			return num1.doubleValue();
		}
		if (isNotNullAndZero(num2)) {
			return num2.doubleValue();
		}
		return Double.valueOf(ZERO);
	}
	
	public static boolean isNotNullAndZero(Integer num) {
		return (num == null || num == 0) ? false : true;
	}

	public static boolean isNotNullAndZero(BigDecimal num) {
		return (num == null || num.doubleValue() == 0) ? false : true;
	}
	
	public static long int2Long(Integer para){
		long re = para;
		return re;
	}
}
