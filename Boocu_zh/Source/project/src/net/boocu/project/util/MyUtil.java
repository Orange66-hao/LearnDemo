package net.boocu.project.util;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;

public class MyUtil{
	

	public static HttpClient httpclient;
    static {
    	HttpClientBuilder create = HttpClientBuilder.create();
    	CloseableHttpClient build = create.build();
        httpclient =build;
    }
		
    public static Map  generateShortUrl(String url) {
        try {
        	Map map=new HashMap<>();
        	map.put("url", url);
        	String jsonString = JSON.toJSONString(map);
            HttpPost httpost = new HttpPost("http://dwz.cn/admin/create");
            httpost.setEntity(new StringEntity(jsonString));
            HttpResponse response = httpclient.execute(httpost);
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils
                    .toString(entity, "utf-8");
            Map rs=(Map) JSON.parse(jsonStr);
            /*System.out.println(rs.get("Code"));
            System.out.println(rs.get("ShortUrl"));
            System.out.println(rs.get("LongUrl"));
            System.out.println(rs.get("ErrMsg"));*/
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}