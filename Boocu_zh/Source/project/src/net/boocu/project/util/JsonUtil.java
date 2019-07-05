package net.boocu.project.util;

import org.apache.commons.lang3.StringUtils;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

public class JsonUtil {
	private static JsonConfig jsonConfig;
	
	private static final String stopStr ="";
	
	public static JSONObject getJsonObj(Object obj){
		jsonConfig = new JsonConfig();
		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object arg2) {
				return arg2==null ;
			}
		});
		return JSONObject.fromObject(obj, jsonConfig) ;
	}
	
	public static JSONObject getJsonObjFor121(Object obj,JsonConfig jsonConfig){
		return JSONObject.fromObject(obj, jsonConfig) ;
	}
}
