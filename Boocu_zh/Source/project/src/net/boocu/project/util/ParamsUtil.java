package net.boocu.project.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import net.boocu.framework.util.ReqUtil;
import net.boocu.web.service.BaseService;


/**
 * 
 * @author fang
 * request开发优化方法
 */
public class ParamsUtil {
	
	/**
	 * 
	 * 概述:根据request的参数自动生成实体类
	 * 具体逻辑:反射机制
	 */
	public static <T> T reflectEntity(HttpServletRequest request, Class<T> entity , BaseService< T, Long> baseService) {
		T obj = null;
		long id =ReqUtil.getLong(request, "id", 0l);
		if(id != 0l){
			//编辑
			obj = baseService.find(id); 
		}else{
			try {
				obj = (T) entity.newInstance();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
    	for(Field item :entity.getDeclaredFields()){
			try {
				Method setmethod = entity.getDeclaredMethod("set"+ item.getName().substring(0, 1).toUpperCase()+item.getName().substring(1), item.getType());
				setmethod.invoke(obj, getParamValue(item,request));
			} catch (Exception e) {
				continue;
			}
    	}
    	baseService.save(obj);
		return obj;
	}

	private static Object getParamValue(Field item,HttpServletRequest request) {
		Object obj = null ;
		switch(item.getType().toString()){
			case "class java.lang.String":
				obj =  ReqUtil.getString(request, item.getName(), "");
				break;
			case "int" :
				obj =  ReqUtil.getInt(request, item.getName(), -1);
				break;
			case "class java.lang.Long" :
				obj =  ReqUtil.getLong(request, item.getName(), 0l);
				break;
			case "class java.util.Date" :
				obj =  ReqUtil.getDate(request, item.getName(), null);
				break;
			case "class java.lang.Float" :
				obj =   ReqUtil.getFloat(request, item.getName(), 0.0f);
				break;
			case "class java.lang.Double" :
				obj =  ReqUtil.getDouble(request, item.getName(), 0.0d);
				break;
			case "class java.math.BigDecimal" :
				obj =  new BigDecimal(ReqUtil.getString(request, item.getName(), "0.0"));
				break;
			default :
				System.out.println("反射类型为定义:"+item.getType().toString());
		}
		return obj;
	}
}
