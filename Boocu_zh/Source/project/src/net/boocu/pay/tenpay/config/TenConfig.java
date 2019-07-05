package net.boocu.pay.tenpay.config;

/**
 * User: cgf
 * Date: 2016/04/04
 * Time: 09:40
 * 这里放置各种配置数据
 */
public class TenConfig {
//这个就是自己要保管好的私有Key了（切记只能放在自己的后台代码里，不能放在任何可能被看到源代码的客户端程序中）
	// 每次自己Post数据给API的时候都要用这个key来对所有字段进行签名，生成的签名会放在Sign这个字段，API收到Post数据的时候也会用同样的签名算法对Post过来的数据进行签名和验证
	// 收到API的返回的时候也要用这个key来对返回的数据算下签名，跟API的Sign数据进行比较，如果值不一致，有可能数据被第三方给篡改

	//private static String key = "05ada06c4e2c924b2a0627e120c9e0ec";

	private static String key = "7tEMvb7od7lZFHGRSz0EapIcS4MOK0dK";
	
	//微信分配的公众号ID（开通公众号之后可以获取到）
	private static String appID = "wx3ce8350338e362a1";

	//微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
	private static String mchID = "1369955602";

	//是否使用异步线程的方式来上报API测速，默认为异步模式
	private static boolean useThreadToDoReport = true;
	
	private static String notify_url = "http://test.wl95.com/pay/ten_notify_url.jspx";

	//以下是几个API的路径：
	//1）被扫支付API
	public static String PAY_API = "https://api.mch.weixin.qq.com/pay/micropay";

	//2）被扫支付查询API
	public static String PAY_QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

	//3）退款API
	public static String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	//4）退款查询API
	public static String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";

	//5）撤销API
	public static String REVERSE_API = "https://api.mch.weixin.qq.com/secapi/pay/reverse";

	//6）下载对账单API
	public static String DOWNLOAD_BILL_API = "https://api.mch.weixin.qq.com/pay/downloadbill";

	//7) 统计上报API
	public static String REPORT_API = "https://api.mch.weixin.qq.com/payitil/report";
	
	//8) 生成二维码的API
	public static String BUILD_TOW_CODE = "weixin://wxpay/bizpayurl";
	
	//9)统一下单API
	public static String UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	//服务器ip
	public static String SPBILL_CREATE_IP = "120.24.72.7";
	

	public static String getNotify_url() {
		return notify_url;
	}

	public static void setNotify_url(String notify_url) {
		TenConfig.notify_url = notify_url;
	}

	public static boolean isUseThreadToDoReport() {
		return useThreadToDoReport;
	}

	public static void setUseThreadToDoReport(boolean useThreadToDoReport) {
		TenConfig.useThreadToDoReport = useThreadToDoReport;
	}

	public static String HttpsRequestClassName = "net.boocu.pay.tenpay.common.HttpsRequest";

	public static void setKey(String key) {
		TenConfig.key = key;
	}

	public static void setAppID(String appID) {
		TenConfig.appID = appID;
	}

	public static void setMchID(String mchID) {
		TenConfig.mchID = mchID;
	}

	public static String getKey(){
		return key;
	}
	
	public static String getAppid(){
		return appID;
	}
	
	public static String getMchid(){
		return mchID;
	}

	public static void setHttpsRequestClassName(String name){
		HttpsRequestClassName = name;
	}

}
