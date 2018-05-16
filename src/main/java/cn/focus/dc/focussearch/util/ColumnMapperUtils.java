package cn.focus.dc.focussearch.util;

import cn.focus.dc.focussearch.annotation.ColumnMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.SimpleTypeConverter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @Description: 将source类根据相应转化为target
 * warn:该类有个问题，调用isField方法的时候会判断该字段是否在当前source中，如果多次调用mapped方法，前面的target
 *      不变，则后面调用的会覆盖前面的字段，尽量保证传递的source字段不一样
 * @author daweiliu205543
 */
public class ColumnMapperUtils {
	
	private final static String DIVISION = ".";
	
	private final static int DEFAULT_MAPPER_COLUMN_LENGTH = 0;
	
	/**
	 * 进行参数转换
	 * @param target
	 * @param source
	 * @param clazzMap
	 * @throws Exception
	 */
	public static void mapped(Object target, Object source, Map<Class<?>, Object> clazzMap) throws Exception {
		try {
			Field[] fields = target.getClass().getDeclaredFields();
			for (Field field : fields) {
				Object value = null;
				if (field.isAnnotationPresent(ColumnMapper.class)) {
					ColumnMapper columnMapper = field.getAnnotation(ColumnMapper.class);
					//没有映射字段，则直接跳过
					if (columnMapper.columnMapperNames().length == DEFAULT_MAPPER_COLUMN_LENGTH) {
						continue;
					}
					//判断是否该类参数
					if (!isField(source, columnMapper.columnMapperNames())) {
						continue;
					}
					//判断是否有映射类，有则进入映射类解析，没有则直接返回数组第一个值
					if (StringUtils.isBlank(columnMapper.methodName())) {
						value = getInnerValue(source, columnMapper.columnMapperNames()[0]);
					} else {
						Object[] params = new Object[columnMapper.columnMapperNames().length];
						for (int index = 0; index < columnMapper.columnMapperNames().length; index++) {
							//获取映射字段名
							String columnName = columnMapper.columnMapperNames()[index];
							params[index] = new SimpleTypeConverter().convertIfNecessary(getInnerValue(source, columnName), columnMapper.paramTypes()[index]);
						}
						//获取转换调用方法
						Method method = getMethod(columnMapper.clazzName(), columnMapper.methodName(), columnMapper.paramTypes());
						if (columnMapper.isStatic()) {
							value = method.invoke(columnMapper.clazzName(), params);
						} else {
							value = method.invoke(clazzMap.get(columnMapper.clazzName()), params);
						}
					}
				} else {
					//没进行注解的并且是List类型的，则直接略过
					if (field.getType().equals(List.class)) {
						continue;
					}
					//判断是否包含column
					if (!isField(source, field.getName())) {
						continue;
					}
					value = getValue(source, field.getName());
				}
				setValue(target, field, value);
			}
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	public static void mapped(Object target, Object source){
		try {
			mapped(target, source, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @description inner value means that we get an object A which hold another object B(has a target prop, say p) as its property,
	 *              we should assign the columnMapperNames in the annotation as "B.p: when using A object to assembly, 
	 *              instead of get B object with JPA.
	 * @param source
	 * @param columnName
	 * @return
	 * @throws Exception
	 */
	private static Object getInnerValue(Object source, String columnName) throws Exception {
		Object sourceValue = null;
		if (isInnerColum(columnName)) {
			sourceValue = getValue(source, StringUtils.split(columnName, DIVISION));
		} else {
			sourceValue = getValue(source, columnName);
		}
		return sourceValue;
	}
	
	public static Object getValue(Object source, String columnName) throws Exception {
		return new PropertyDescriptor(columnName, source.getClass()).getReadMethod().invoke(source);
	}
	
	public static void setValue(Object target, Field field, Object value) throws Exception {
		new PropertyDescriptor(field.getName(), target.getClass()).getWriteMethod()
				.invoke(target,
						new SimpleTypeConverter().convertIfNecessary(value, field.getType()));
	}
	
	public static Method getMethod(Class<?> clazzName, String methodName, Class<?>[] params) throws Exception {
		return clazzName.getMethod(methodName, params);
	}
	
	public static Object getValue(Object source, String[] innerColumn) throws Exception {
		Object obj = source;
		for (String column : innerColumn) {
			obj = getValue(obj, column);
			if (obj == null) {
				return null;
			}
		}
		return obj;
	}
	
	private static boolean isInnerColum(String column) {
		return column.contains(DIVISION);
	}
	
	public static boolean isField(Object source, String[] columnNames) {
		for (String columnName : columnNames) {
			String fieldName = isInnerColum(columnName) ? StringUtils.split(columnName, DIVISION)[0] : columnName;
			if (!isField(source, fieldName)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isField(Object source, String columnName) {
		Field[] fields = source.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().equals(columnName)) {
				return true;
			}
		}
		return false;
	}
}
