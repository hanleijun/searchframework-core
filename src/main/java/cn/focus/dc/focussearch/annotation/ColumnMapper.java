package cn.focus.dc.focussearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnMapper {

	public Class<?> clazzName() default Object.class;
	
	public String methodName() default "";
	
	public String[] columnMapperNames() default {};
	
	public Class<?>[] paramTypes() default {};
	
	public boolean isStatic() default false;
}
