/*
 * 异步加载品牌和型号
 */
$(function(){
	var typeHtml = "";
	$.post("/shopCart/barndLists.jspx",function(data){
		var typeO = $("#typeO").val();
		for(var i=0;i<data.length;i++){
			if(typeO == data[i].id){
				typeHtml += "<option value='"+data[i].id+"' selected='selected'>"+data[i].text+"</option>";
			}else{
				typeHtml += "<option value='"+data[i].id+"'>"+data[i].text+"</option>";				
			}
		}
		$("#selOType").append(typeHtml);
	});
	var proNo = "";
	$.post("/shopCart/proNoLists.jspx",function(data){
		if(data!=null){
			var noPro = $("#noPro").val();
			for(var i=0;i<data.length;i++){
				if(noPro == data[i].id){
					proNo += "<option value='"+data[i].id+"' selected='selected'>"+data[i].text+"</option>";					
				}else{
					proNo += "<option value='"+data[i].id+"'>"+data[i].text+"</option>";
				}
			}
			$("#selProNo").append(proNo);	
		}
	});
	var isfirst = $("#isfirst").val();
	if(isfirst != 1){
		//展开搜索条件
		$(".uosearchd .keyinpd .link").trigger("click");
	}
});


function addShopCart(proId){
	var proOriginal1 = $('#proOriginal1').attr('src');
	var price = $.trim($(".prodrpriced").find("span").html());
	if(price == "电议"){
		window.wxc.xcConfirm("该商品暂时无法购买，请联系客服。。。", window.wxc.xcConfirm.typeEnum.info);
	}else{
		//var url = 
		$.post("/shopCart/addShopCart.jspx",{"proId":proId,"price":price,"proOriginal1":proOriginal1,
			"url":window.location.href},
			function(data){
				if(data == "unlogin"){
					var txt=  "你还未登陆，是否前往登陆页面？";
					var option = {
						title: "登陆提示",
						btn: parseInt("0011",2),
						onOk: function(){
							window.location.href="/login.jspx";
						}
					}
					window.wxc.xcConfirm(txt, "custom", option);
				}else if(data == "ok"){
					var txt=  "恭喜你添加成功，是否去购物车结算？";
					var option = {
						title: "添加成功",
						btn: parseInt("0011",2),
						onOk: function(){
							window.location.href="/shopCart/toShopCart.jspx?isFirst=1";
						}
					}
					window.wxc.xcConfirm(txt, "custom", option);
				}else if(msg = "priceEx"){
					window.wxc.xcConfirm("价格异常，暂时无法购买，请联系管理员！", window.wxc.xcConfirm.typeEnum.warning);
				}else{
					window.wxc.xcConfirm("添加失败，请稍后再试！", window.wxc.xcConfirm.typeEnum.error);
				}
		})
	}
}

function settle(){
	var prices = [];
	$(".uinqtab").find("input[name='ckId']:checked").each(function(){
		if($(this).attr("checked")){
			prices.push($(this).attr("h_price"));
		}
	})
	if(prices.length <= 0){
		window.wxc.xcConfirm("请至少选中一件商品！", window.wxc.xcConfirm.typeEnum.warning);
	}else{
		/*
		var price = 0;
		$.each(prices,function(index){
			price += parseFloat(this);
		});
		*/
		//获得每个选中的id，拼成字符串
		var ids = '';
		var is_rate = new Array;
		var rate = new Array;
		$(".uinqtab").find("input[name='ckId']:checked").each(function(){
			if($(this).attr("checked")){
				ids += $(this).attr("value")+",";
				is_rate.push($(this).attr("h_is_rate"));
				rate.push($(this).attr("h_rate"));
			}
		});
		
		//能否结算
		var sett = false;
		if(is_rate.length > 1){
			for(var i=0;i<is_rate.length;i++){
				if(is_rate[i] == is_rate[i+1]){
					sett = true;
					break;
				}
			}
			if(sett){
				//判断税是不是相同
				for(var j=0;j<rate.length;j++){
					if(j+1 < rate.length){
						if(rate[j] == rate[j+1]){
							sett = true;
						}else{
							sett = false;
							break;
						}					
					}
				}
			}
		}else{
			sett = true;
		}
		
		if(sett){
			//生成订单，并到结算页面对订单进行结算
			$.post("/shopCart/toSettle.jspx",{"ids":ids},function(data){
				//跳转到订单详情页面
				window.location.href="/order/toOrderDetail.jspx?orderId="+data;
			});
		}else{
			window.wxc.xcConfirm("所选商品税率不同，不能进行结算", window.wxc.xcConfirm.typeEnum.error);
		}

	}
}

/**
 * batchDel
 * @param {type}  
 */
 function batchDel() {
	//获得每个选中的id，拼成字符串
	var ids = '';
	$(".uinqtab").find("input[name='ckId']:checked").each(function(){
		if($(this).attr("checked")){
			ids += $(this).attr("value")+",";
		}
	});
	if(ids == ''){
		window.wxc.xcConfirm("请至少选中一件商品！", window.wxc.xcConfirm.typeEnum.warning);
	}else{
		$.post("/shopCart/batchDel.jspx",{"ids":ids},function(data){
		if(data == "ok"){
			window.wxc.xcConfirm("删除成功！", window.wxc.xcConfirm.typeEnum.success,{onOk:function(){
				window.location.href="/shopCart/toShopCart.jspx?isFirst="+$("#isfirst").val();
			}});
		}else{
			window.wxc.xcConfirm("删除失败，请稍后再试！", window.wxc.xcConfirm.typeEnum.error);
		}
	});		
	}
 }

function sumCount(){
	var prices = [];
	$(".uinqtab").find("input[name='ckId']:checked").each(function(){
		if($(this).attr("checked")){
			prices.push($(this).attr("h_price"));
		}
	});
	
	var price = 0;
	$.each(prices,function(index){
		price += parseFloat(this);
	});
	$("#sumAmount").html("￥："+price);
}
