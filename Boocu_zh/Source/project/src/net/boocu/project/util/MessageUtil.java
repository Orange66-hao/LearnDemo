package net.boocu.project.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.boocu.web.Message;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;


public class MessageUtil {
	
	private  HashMap<String ,Object> map = new HashMap<String ,Object>();

	
	public MessageUtil() {
		
	}
	public MessageUtil( String name , Object value) {
		push(name,value);
	}
	public static MessageUtil getInstance( String name , Object value){
		
		return new MessageUtil(name,value);
	}
	public static MessageUtil getInstance(){
		
		return new MessageUtil();
	}
	
	public HashMap<String ,Object> getItems(){
		return map;
	}
	
	public MessageUtil setItems(HashMap<String ,Object> items){
		map = items;
		return this;
	}
	
	public HashMap<String ,Object> getItem(String name){
		HashMap<String, Object> hashMap =  new HashMap<String, Object>();
		hashMap.put(name, getItemValue(name));
		return hashMap;
		
	}
	public Object getItemValue(String name){
		return getItems().get(name);
	}
	
	public boolean hasMessage(){
		return !getItems().isEmpty();
	}
	public MessageUtil push(String name,Object value){
		getItems().put( name,value);
		return this;
	}
	
	public  String toLinkString(){
		
		List<String> list = new ArrayList<String>();
		
		Iterator<Entry<String, Object>> iter = getItems().entrySet().iterator();
		while (iter.hasNext()) {
			
			 Entry<String, Object> entry = iter.next();
			 String key = entry.getKey();
			 Object val = entry.getValue();
			 if(val == null){
				 list.add(key+"=");
			 }else{
				 list.add(key+"="+val.toString());
			 }
			 
		}
		
		return StringUtils.join(list, "&");

	}
	
	public  String toString(){
		return JSONObject.toJSONString(getItems());
	}
	
	public static Message error (String name,String value){
		return Message.error(getInstance(name,value).toString());
	}
	public static Message error (MessageUtil MessageUtil){
		return Message.error(MessageUtil.toString());
	}
	public static Message success (String name,String value){
		return Message.success(getInstance(name,value).toString());
	}
	public static Message success (MessageUtil message){
		return Message.success(message.toString());
	}
	public static Message warn (String name,String value){
		return Message.warn(getInstance(name,value).toString());
	}
	public static Message warn (MessageUtil message){
		return Message.warn(message.toString());
	}
	public static MessageUtil fromJson(String json){
		HashMap<String ,Object> hashMap = new HashMap<String ,Object> ();
		try{
			hashMap =  JSONObject.parseObject(json, hashMap.getClass());
		}catch(Exception e){
			
		}
		return MessageUtil.getInstance().setItems(hashMap);
	}
}