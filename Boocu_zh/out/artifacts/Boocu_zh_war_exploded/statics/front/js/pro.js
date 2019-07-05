$(function(){
	// 产品列表效果
	$('.prolid .picd').mouseenter(function(){ $(this).addClass('mouse'); });
	$('.prolid .picd').mouseleave(function(){ $(this).removeClass('mouse'); });
	
	//产品详情
	$(".propic_left .bd li").each(function(){ $(this).append("<i></i>"); });
	$(".propic_left").slide({titCell:".hd ul",mainCell:".bd ul",autoPage:true,effect:"left",autoPlay:false,scroll:5,vis:5,pnLoop:false});
	
	$(".propic_left .bd li").click(function(){
		$(".propic_left .bd li").removeClass("on");
		$(this).addClass("on");
		var bigUrl = $(this).find("img").attr("big-src");
		$("#bigpicdID").attr("src",bigUrl);
	});
});