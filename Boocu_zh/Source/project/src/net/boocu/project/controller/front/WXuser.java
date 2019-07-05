package net.boocu.project.controller.front;

import com.qq.connect.utils.json.JSONException;
import com.qq.connect.utils.json.JSONObject;

public class WXuser {
	
	private String nickname;
	
	private String openid;
	private String sex;
	private String unionid;
	private String headimgurl;
	
	public WXuser(String json){
		try {
			JSONObject obj = new JSONObject(json);
		
	        if (obj != null) {
	            Object nickname = obj.get("nickname");
	            Object openid = obj.get("openid");
	            Object sex = obj.get("sex");
	            Object unionid = obj.get("unionid");
	            Object headimgurl = obj.get("headimgurl");
	            if(nickname!=null){
	            	this.setNickname(nickname.toString());
	            }
	            if(openid!=null){
	            	this.setOpenid(openid.toString());
	            }
	            if(sex!=null){
	            	this.setSex(sex.toString());
	            }
	            if(unionid!=null){
	            	this.setUnionid(unionid.toString());
	            }
	            if(nickname!=null){
	            	this.setHeadimgurl(headimgurl.toString());
	            }
	        
	            
	        }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public String getHeadimgurl() {
		return headimgurl;
	}
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	
	
}
