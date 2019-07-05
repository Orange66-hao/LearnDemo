
var intel;
function submit_Order(orderId,price,addr){
	//判断收货地址
	if(addr.length <= 0 || addr == ""||addr==null){
		window.wxc.xcConfirm("<a href='/addressManage/toAddressPage.jhtml'>请填写收货地址！</a>", window.wxc.xcConfirm.typeEnum.warning);
		return;
	}
	//获取支付方式
	var payType = $(".uorderdd2 input[name='paytype']:checked").val();
	
	if(payType == 3){
		//邮局汇款
		$.post("/pay/remit.jspx",{"orderId":orderId,"payType"
		:payType,"sumPrice":price},function(data){
			if(data&&data == "ok"){
				window.wxc.xcConfirm("订单确认中，请尽快完成汇款,然后等待通知！", 
				window.wxc.xcConfirm.typeEnum.success,{onOk:function(){
					openDialog();
				}});
			}else if(data == "statErr"){
				window.wxc.xcConfirm("该订单状态不可支付，可能是已经支付过了！", window.wxc.xcConfirm.typeEnum.error,
				{onOk:function(){
					window.location.href = window.location.href;
				}});
			}else if(data == "notFund"){
				window.wxc.xcConfirm("订单不存在,刷新页面后再试！", window.wxc.xcConfirm.typeEnum.error);
			}else{
				window.wxc.xcConfirm("支付失败，请稍后再试！", window.wxc.xcConfirm.typeEnum.error);
			}
		});
	}else{
		$.post("/pay/payOrder.jspx",{"orderId":orderId
		,"payType":payType,"sumPrice":price},
		function(data){
			if(data&&data.code=="ok"){
				if(payType==1){
					//微信支付
					$("#model_content").css("display","block");
					jQuery('#qrcodeCanvas').qrcode({
						render   : "canvas",//设置渲染方式  
						width       : 210,     //设置宽度  
						height      : 210,     //设置高度  
						typeNumber  : -1,      //计算模式  
						correctLevel    : QRErrorCorrectLevel.H,//纠错等级  
						background      : "#ffffff",//背景颜色  
						foreground      : "#000000", //前景颜色
						text	: data.msg
					});
					intel =setInterval(function(){
						//请求查询订单状态
						queryOrderStatus(orderId,intel);
					},2000); 
				}else if(payType==2){
					//支付宝
					$("#ali_pay").html(data.msg);
				}
			}else{
				window.wxc.xcConfirm("失败，请联系管理员！", window.wxc.xcConfirm.typeEnum.error);
			}
		});
	}
}

//关闭微信支付窗口
$("#icon_close").bind("click",function(){
	clearInterval(intel);
	$("#model_content").hide();
});

$(".tip_dialog_close").click(function(){
	closeDialog();
});


function closeDialog(){
	$(".payBox1").hide();
	$(".tip_dialog").hide();
}

function openDialog(){
	$(".payBox1").show();
	$(".tip_dialog").show();	
}

function repay(){
	closeDialog();
}

function clearLoading(){
	$("#processing").hide();
	$(".payBox1").hide();
}

function paySuccess(id){
	//请求支付宝查询接口
	
}

var _count=0;
var pay_result = -1;
/*
function paySuccess(id){
	//显示正在加载图片
	if(_count==4){
		if(pay_result == -1){
			clearLoading()
			//支付失败，刷洗页面
			window.wxc.xcConfirm("支付失败！", window.wxc.xcConfirm.typeEnum.error,{
				onOk:function(){
					//刷新页面
					window.location.href = window.location.href;
				}
			});
		}
		return;
	}else{
		_count++;
	}
	$(".tip_dialog").hide();
	$(".payBox1").css("background","#fff");
	$("#processing").show();
	
	var _pay_result= 0;
	
	$.post("/order/queryOrderStatus.jspx",
	{"orderId":id},function(data){
		if(data && data.orderId == id){
			//查询成功
			if(data.code=="ok"){
				//订单查询成功
				if(data.status==1){
					//支付成功,修改支付结果并清除轮训
					window.location.href = "/order/toSuccess.jspx?orderId="+id+"&guid="+guid();
				}
			}else{
				//支付失败，刷洗页面
				window.wxc.xcConfirm("支付失败！", window.wxc.xcConfirm.typeEnum.error,{
					onOk:function(){
						//刷新页面
						window.location.href = window.location.href;
					}
				});
			}
		}
	});
		
	setTimeout("paySuccess("+id+")",1500);
	/*
	var _count = 4;
	//支付结果
	var _pay_result= 0;
	var setIn;
	setIn = setInterval(function(){
		_count--;
		if(_count <= 0){
			clearInterval(setIn);
		}
		$.post("/order/queryOrderStatus.jspx",
		{"orderId":id},function(data){
			if(data && data.orderId == id){
				//查询成功
				if(data.code=="ok"){
					//订单查询成功
					if(data.status==1){
						//支付成功,修改支付结果并清除轮训
						_pay_result = 1;
						clearInterval(setIn);
					}
				}else{
					//支付失败，清除轮训并刷洗页面
					clearInterval(setIn);
					window.wxc.xcConfirm("支付失败！", window.wxc.xcConfirm.typeEnum.error,{
						onOk:function(){
							//刷新页面
							window.location.href = window.location.href;
						}
					});
				}
			}
		});
	},1500);
	
	if(_pay_result == 1){
		//跳转到支付成功页面
		window.location.href = "/order/toSuccess.jspx?orderId="+id;
	}else{
		window.location.href = window.location.href;
	}
	* */
//}

//查询订单状态
function queryOrderStatus(orderId,intel){
	$.post("/order/queryOrderStatus.jspx",{"orderId":orderId},
	function(data){
		if(data && data.orderId == orderId){
			//查询成功
			if(data.code=="ok"){
				//订单查询成功
				if(data.status==1){
					//支付成功,清除轮训并刷新页面
					clearInterval(intel);
					window.location.href = "/order/toSuccess.jspx?orderId="+orderId+"&guid="+guid();			
				}
			}
		}
	});
}

function getAddrs(orderId){
	$.post("/addressManage/addressJson.json",function(result){
		console.log(result)
		var data = $.parseJSON(result);
		if(data!="null"){
			var forhtml = ""
			for(var i=0; i<data.length; i++) {
				if(data[i].isDefault == 1){
					forhtml += "<label><input type='radio' checked='checked' name='addr' value='"+data[i].id+"' />"+data[i].country+
					" "+data[i].province+" "+data[i].city+" "+data[i].area+" "+data[i].detail+"</label></br>";					
				}else{
					forhtml += "<label><input type='radio' name='addr' value='"+data[i].id+"' />"+data[i].country+
					" "+data[i].province+" "+data[i].city+" "+data[i].area+" "+data[i].detail+"</label></br>";					
				}
			}
			var html = "<div class='addrBox' style='background:#666;position:fixed; width:100%; height:100%;left:0px;" +
			"top:0px;z-index:9999999999;filter:alpha(opacity=50);-moz-opacity:0.5;-khtml-opacity:0.5;opacity: 0.5;'>" +
			"</div><div class='addrBox' style='position: fixed;left: 30%;top: 10%;" +
			"background: #FFFFFF;border: 1px solid #DBE0E4;overflow: hidden;border-radius: 5px;width: 680px;min-height: 425px;z-index:99999999999;padding-top:20px;'>" +
			"<form id='updateAddrForm' action='' method='post'><input type='hidden' name='orderId' value='"+orderId+"'/>" +
			"<div style='margin-left: 130px;margin-right: 150px;overflow:hidden;margin-top:20px;font-size: 16px;'>" +
				forhtml +
			"<div style='text-align:center;margin-top:50px;margin-left: -165px;'><input type='button' value='关闭' onclick='closeAddrBox()' " +
			"style='background:#EA8B43;width:80px; height:35px;color:#fff;border:0px;font-size:16px;line-height:35px;'/>" +
			"<input type='button' value='确认' onclick='updateAddr()' style='background:#5BD271;width:80px; height:35px;" +
			"color:#fff;border:0px;font-size:16px;line-height:35px;margin-left:60px;'/></div></form></div>";
			$("body").append(html);
		}
	});
}

function updateAddr(){
	$.post("/order/updateAddr.jspx",$("#updateAddrForm").serialize(),
	function(result){
		//成功后关闭遮罩	
		closeAddrBox();
		if(result=="ok"){
			window.wxc.xcConfirm("修改成功！", window.wxc.xcConfirm.typeEnum.success
			,{onOk:function(){
				window.location.href=window.location.href;
			}});
		}else{
			window.wxc.xcConfirm("失败，请联系管理员！", window.wxc.xcConfirm.typeEnum.error);
		}
	});
}

function closeAddrBox(){
	$(".addrBox").remove();
}

function mergePay(){
	var ids = "";
	$(".uotab").find("input[name='ckId']:checked").each(function(){
		if($(this).attr("checked")){
			ids += $(this).attr("value") + ",";
		}
	});
	if(ids == ""){
		window.wxc.xcConfirm("请至少选中一个订单！", window.wxc.xcConfirm.typeEnum.warning);
	}else{
		$.post("/pay/mergePay.jspx",{"orderIds":ids},
		function(data){
				
		})
	}
}

function selPayType(id){
	//微信
	$(id).attr("checked","checked")
}

function S4() {
   return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}

function guid() {
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}