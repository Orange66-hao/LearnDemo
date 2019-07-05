/**
$(function(){
	//add cilck event to "emailask"
    $("a[data-opera='email']").bind("click",function(){
		var productId = $(this).attr("product-id");
		alert(productId)
        //ajax request validate user is login?
    	$.post("/emailAsk/validateUser.jspx",
    	function(data){
			var url = window.location.href;
			if(data.code==1){
				//user had logined ,then ,get user info  and  show in pages
	    		$("body").append("<div class='emailAskMark' style='background:#666;position:fixed; width:100%; height:100%;left:0px;" +
				"top:0px;z-index:9999999999;filter:alpha(opacity=50);-moz-opacity:0.5;-khtml-opacity:0.5;opacity: 0.5;'></div><div class='emailAskMark' style='position: fixed;left: 30%;top: 10%;" +
				"background: #FFFFFF;border: 1px solid #DBE0E4;overflow: hidden;border-radius: 5px;width: 680px;min-height: 425px;z-index:99999999999;padding-top:20px;'><form id='AskForm' action='/emailAsk/sendAskEmail.jspx' method='post'>" +
				"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>姓名：</p><p style='float:left;'><input type='hidden' name='productId' value='"+productId+"'/><input type='hidden' name='url' value='"+url+"'/>" +
				"<input type='text' name='realName' class='txt' value='"+data.realName+"' style='width:400px;height:30px;'></p><p class='rr'></p></div>" +
				"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>手机号码：</p><p style='float:left;'><input type='text' name='mobile' class='txt' value='"+data.phone+"' style='width:400px;height:30px;'></p><p class='rr'></p></div>" +
				"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>地址：</p><p style='float:left;'><input type='text' name='address' class='txt' value='"+data.address+"' style='width:400px;height:30px;'></p><p class='rr'></p></div>" +
				"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>邮箱：</p><p style='float:left;'><input type='text' name='email' class='txt' value='"+data.email+"' style='width:400px;height:30px;'></p><p class='rr'></p></div>" +
				"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>留言：</p><p style='float:left;'><textarea type='text' maxlength='50' name='ramark' class='text' style='width:400px;height:80px;'></textarea></p><p class='rr'></p></div>"+
				"<div style='text-align:center;margin-top:30px;'><input type='button' value='关闭' onclick='closeAsk()' style='background:#EA8B43;width:80px; height:35px;color:#fff;border:0px;font-size:16px;line-height:35px;'/><input type='button' value='确认' onclick='submitAsk()' style='background:#5BD271;width:80px; height:35px;color:#fff;border:0px;font-size:16px;line-height:35px;margin-left:60px;'/></div>"+
				"</form></div>");
			}else{
	    		$("body").append("<div class='emailAskMark' style='background:#666;position:fixed; width:100%; height:100%;left:0px;" +
				"top:0px;z-index:9999999999;filter:alpha(opacity=50);-moz-opacity:0.5;-khtml-opacity:0.5;opacity: 0.5;'></div><div class='emailAskMark' style='position: fixed;left: 30%;top: 10%;" +
				"background: #FFFFFF;border: 1px solid #DBE0E4;overflow: hidden;border-radius: 5px;width: 680px;min-height: 425px;z-index:99999999999;padding-top:20px;'><form  id='AskForm' action='/emailAsk/sendAskEmail.jspx' method='post'>" +
				"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>姓名：</p><p style='float:left;'><input type='hidden' name='url' value='"+url+"'/>" +
				"<input type='hidden' name='productId' value='"+productId+"'/><input type='text' name='realName' class='txt' value='' style='width:200px;height:30px;'></p><p style='color:red;'></p></div>" +
				"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>手机号码：</p><p style='float:left;'><input type='text' name='mobile' class='txt' value='' style='width:200px;height:30px;'></p><p class='rr'></p></div>" +
				"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>地址：</p><p style='float:left;'><input type='text' name='address' class='txt' value='' style='width:200px;height:30px;'></p><p class='rr'></p></div>" +
				"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>邮箱：</p><p style='float:left;'><input type='text' name='email' class='txt' value='' style='width:200px;height:30px;'></p><p class='rr'></p></div>" +
				"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>留言：</p><p style='float:left;'><textarea type='text' maxlength='50' name='ramark' class='text' style='width:400px;height:80px;'></textarea></p><p class='rr'></p></div>"+
				"<div style='text-align:center;margin-top:30px;'><input type='button' value='关闭' onclick='closeAsk()' style='background:#EA8B43;width:80px; height:35px;color:#fff;border:0px;font-size:16px;line-height:35px;'/><input type='button' value='确认' onclick='submitAsk()' style='background:#5BD271;width:80px; height:35px;color:#fff;border:0px;font-size:16px;line-height:35px;margin-left:60px;'/></div>"+
				"</form></div>");
			}
    	});
    });
});
*/

function emailAsk(productId){
	$.post("/emailAsk/validateUser.jspx",
    function(data){
		var url = window.location.href;
		if(data.code==1){
			//user had logined ,then ,get user info  and  show in pages
    		$("body").append("<div class='emailAskMark' style='background:#666;position:fixed; width:100%; height:100%;left:0px;" +
			"top:0px;z-index:9999999999;filter:alpha(opacity=50);-moz-opacity:0.5;-khtml-opacity:0.5;opacity: 0.5;'></div><div class='emailAskMark' style='position: fixed;left: 30%;top: 10%;" +
			"background: #FFFFFF;border: 1px solid #DBE0E4;overflow: hidden;border-radius: 5px;width: 680px;min-height: 425px;z-index:99999999999;padding-top:20px;'><form id='AskForm' action='/emailAsk/sendAskEmail.jspx' method='post'>" +
			"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>姓名：</p><p style='float:left;'><input type='hidden' name='productId' value='"+productId+"'/><input type='hidden' name='url' value='"+url+"'/>" +
			"<input type='text' name='realName' class='txt' value='"+data.realName+"' style='width:400px;height:30px;'></p><p class='rr'></p></div>" +
			"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>手机号码：</p><p style='float:left;'><input type='text' name='mobile' class='txt' value='"+data.phone+"' style='width:400px;height:30px;'></p><p class='rr'></p></div>" +
			"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>地址：</p><p style='float:left;'><input type='text' name='address' class='txt' value='"+data.address+"' style='width:400px;height:30px;'></p><p class='rr'></p></div>" +
			"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>邮箱：</p><p style='float:left;'><input type='text' name='email' class='txt' value='"+data.email+"' style='width:400px;height:30px;'></p><p class='rr'></p></div>" +
			"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>留言：</p><p style='float:left;'><textarea type='text' maxlength='50' name='ramark' class='text' style='width:400px;height:80px;'></textarea></p><p class='rr'></p></div>"+
			"<div style='text-align:center;margin-top:30px;'><input type='button' value='关闭' onclick='closeAsk()' style='background:#EA8B43;width:80px; height:35px;color:#fff;border:0px;font-size:16px;line-height:35px;'/><input type='button' value='确认' onclick='submitAsk()' style='background:#5BD271;width:80px; height:35px;color:#fff;border:0px;font-size:16px;line-height:35px;margin-left:60px;'/></div>"+
			"</form></div>");
		}else{
    		$("body").append("<div class='emailAskMark' style='background:#666;position:fixed; width:100%; height:100%;left:0px;" +
			"top:0px;z-index:9999999999;filter:alpha(opacity=50);-moz-opacity:0.5;-khtml-opacity:0.5;opacity: 0.5;'></div><div class='emailAskMark' style='position: fixed;left: 30%;top: 10%;" +
			"background: #FFFFFF;border: 1px solid #DBE0E4;overflow: hidden;border-radius: 5px;width: 680px;min-height: 425px;z-index:99999999999;padding-top:20px;'><form  id='AskForm' action='/emailAsk/sendAskEmail.jspx' method='post'>" +
			"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>姓名：</p><p style='float:left;'><input type='hidden' name='url' value='"+url+"'/>" +
			"<input type='hidden' name='productId' value='"+productId+"'/><input type='text' name='realName' class='txt' value='' style='width:200px;height:30px;'></p><p style='color:red;'></p></div>" +
			"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>手机号码：</p><p style='float:left;'><input type='text' name='mobile' class='txt' value='' style='width:200px;height:30px;'></p><p class='rr'></p></div>" +
			"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>地址：</p><p style='float:left;'><input type='text' name='address' class='txt' value='' style='width:200px;height:30px;'></p><p class='rr'></p></div>" +
			"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>邮箱：</p><p style='float:left;'><input type='text' name='email' class='txt' value='' style='width:200px;height:30px;'></p><p class='rr'></p></div>" +
			"<div style='margin-left: auto;margin-right: auto;overflow:hidden;margin-top:20px;'><p style='float:left;width:100px;text-align:right;margin-left:70px;'><i style='color:#F00;margin-right:10px;'>*</i>留言：</p><p style='float:left;'><textarea type='text' maxlength='50' name='ramark' class='text' style='width:400px;height:80px;'></textarea></p><p class='rr'></p></div>"+
			"<div style='text-align:center;margin-top:30px;'><input type='button' value='关闭' onclick='closeAsk()' style='background:#EA8B43;width:80px; height:35px;color:#fff;border:0px;font-size:16px;line-height:35px;'/><input type='button' value='确认' onclick='submitAsk()' style='background:#5BD271;width:80px; height:35px;color:#fff;border:0px;font-size:16px;line-height:35px;margin-left:60px;'/></div>"+
			"</form></div>");
		}
	});
}

function closeAsk(){
	$(".emailAskMark").remove();
}

function submitAsk(){
	$.post("/emailAsk/sendAskEmail.jspx",$("#AskForm").serialize(),
	function(result){
		if(result=="ok"){
			alert("咨询成功！");
			closeAsk();
		}else{
			alert("咨询失败，请稍后再试。")
		}
	});
}