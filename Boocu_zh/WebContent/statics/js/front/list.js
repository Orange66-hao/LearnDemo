
$(function(){
	//产品分类内二阶搜索
	seachChild("#proClass","#proClass_children","/productClass/dataJson/");
	//行业分类内二阶搜索
	seachChild("#indClass","#indClass_children","/industryClass/dataJson/");
	//字母区域对应品牌
	seachChild("#brandArea","#brandArea_children","/brand/dataJson/");
	//品牌点击搜索
	$("#brands").find(".contd").find("li a").click(function(){
		addCondition(this,false);
	});
	//综合排序搜索
	$(".prosortd").find("input[type='radio']").change(function(){
		addCondition(this,false);
	});
	//关键字搜索
	$(".btnd").click(function(){
		var keyword = $(this).prev().find("input").val();
		$("#searchForm").find("input[name='keyword']").attr("value",keyword);
		submitPost();
	});
	
	//分页
	$(".pagd").find("a").click(function(){
		$("#searchForm").find("input[name='pageNum']").attr("value",$(this).attr("s_val"));
		if(!$(this).hasClass("on")){
			submitPost();
			$(document).scrollTop(300);
			$("#searchForm").find("input[name='pageNum']").attr("value","");
		}
	});
});

//取得该节点下的子节点
function seachChild(parentId,childId,url){
	$(parentId).find("li a").click(function(){
		var val = $(this).attr("s_val"),data = {},isMutCheck = false;
		$that = $(this);
		if(!val){
			//点击品牌字母时
			val = "areaBrands";
			data.area =$(this).text();
		}else{
			addCondition(this,isMutCheck);
		}
		$.ajax({
			url:url+val,
			type:"post",
			datatype:"json",
			data:data,
			success:function(data){
				$(childId).find("li").remove();
				$(childId+"_children").find("li").remove();
				for(var item in data){
					$(childId).find("ul").append("<li><a href='javascript:void(0);' onclick='addCondition(this);' s_val='"+data[item].id+"'>"+data[item].text+"</a></li>");
				}
				//加入三阶搜索 add by fang 20150911
				$(childId).find("li a").click(function(){
					var childData = {};
					$.ajax({
						url:url+$(this).attr("s_val"),
						type:"post",
						datatype:"json",
						success:function(data){
							$(childId+"_children").find("li").remove();
							for(var item in data){
								$(childId+"_children").find("ul").append("<li><a href='javascript:void(0);' onclick='addCondition(this);' s_val='"+data[item].id+"'>"+data[item].text+"</a></li>");
							}
						},
						error:function(error){}
					});
				});
			},
			error:function(error){}
		});
		
	});
}

//增加条件
function addCondition(obj,isMutCheck){
	var $this = $(obj),isExist = false,isTypeExist = false,whereItem ="",text ="";
	//是否是综合排序
	var isCooOrder = false;
	var id = $this.attr("s_val");
	var type = $this.closest(".listtypelid").attr("id");
	if(!type){
		//综合排序时情况
		type = $this.attr("name");
		isCooOrder = true;
		text = $this.parent().text();
	}else{
		text = $this.text();
	}
	
	//表单条件增添20150908
	$("#searchForm").find("input[name='"+type+"']").attr("value",id);
	
	$("#seletedCons").show();
	if(!isCooOrder){
		$this.closest(".listtypelid").find("li").removeClass("on");
		$this.closest("li").addClass("on");
	}
	 $(".whered .wlid").each(function(){
		if( $(this).attr("s_type") == type){
			isTypeExist = true;
			if($(this).attr("s_id") == id){
				isExist = true;
			}
			$(this).remove();
		}
	}); 
	
	//显示已选条件
	whereItem ="<div class='wlid' s_type='"+type+"' s_id='"+id+"' ><p>"+text+"</p><p class='ico'><a href='javascript:;' onclick='remCondition(this,"+false+","+isCooOrder+");'></a></p></div>";
	$(".whered").append(whereItem);	
	//提交表单数据
	submitPost();
	
}
//删除条件
function remCondition(obj,isMutCheck,isCooOrder){
	var $this = $(obj) ,  type = $this.closest(".wlid").attr("s_type") , id = $this.closest(".wlid").attr("s_id");
	$this.closest(".wlid").remove();
	//重置条件样式
	if(isCooOrder){
		$(".prosortd").find("input[name='"+type+"']").each(function(){
			$(this).attr("checked",false);
		});
	}else{
		$("#"+type).find("a[s_val='"+id+"']").parent().removeClass("on");
	}
	//重置表单条件数据
	$("#searchForm").find("input[name='"+type+"']").attr("value","");
	submitPost();
}

//提交表单
function submitPost(){
	alert("6");
	$(".prould").html("");
	$(".pagd").html("");
	$(".prolistpd .listloadingd").show();
	$.ajax({
		url:"/product/dataJson",
		data:$("#searchForm").serialize(),
		datatype:"html",
		type:"post",
		success:function(data){
			$(".pageList").html(data);
			$(".prolistpd .listloadingd").hide();
		}
	});
}