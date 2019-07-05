$(function(){
	//图片延迟加载
	$('img.lazy').lazyload({ effect:'fadeIn', threshold:100, skip_invisible:false, failurelimit:7 });
	
	//导航
	$('#navlid>li').mouseenter(function(){ $(this).addClass('mouse'); });
	$('#navlid>li').mouseleave(function(){ $(this).removeClass('mouse'); });
	
	// 左侧产品分类
	$('.maintyped li').mouseenter(function(){ $(this).addClass('mouse'); });
	$('.maintyped li').mouseleave(function(){ $(this).removeClass('mouse'); });
	
	$('.gotopd a').click(function(){ $("body").animate({scrollTop:"0"},200); $(window).scrollTop(0); });
});

//前台首页 add by fang 20150907
function ChangeContent(obj){
	$this = $(obj);
	//填充内容
	putContent(".indexContent",$this.attr("s_href"));
}
function putContent(target,url){
	$(".indexContent").hide();
	$("#indexLoading").show();
	$.get(url,function(data){
		$.includePath = '/statics/'; 
		$(target).html(data);
		alert("target:"+target);
		alert("url:"+url);
		$.include(['js/jquery-1.11.2.js', 'js/lazyload/jquery.lazyload.min.js', 'js/jquery.SuperSlide.2.1.1.js','js/main.js']);
		//动态加载css和js
		$("#indexLoading").hide();
		$(".indexContent").show();
	});
}
//add by fang 20150907 动态加载css和js文件
$.extend({
    includePath: '',
    include: function(file) {
       var files = typeof file == "string" ? [file]:file;
       for (var i = 0; i < files.length; i++) {
           var name = files[i].replace(/^\s|\s$/g, "");
           var att = name.split('.');
           var ext = att[att.length - 1].toLowerCase();
           var isCSS = ext == "css";
           var tag = isCSS ? "link" : "script";
           var attr = isCSS ? " type='text/css' rel='stylesheet' " : " language='javascript' type='text/javascript' ";
           var link = (isCSS ? "href" : "src") + "='" + $.includePath + name + "'";
           if ($(tag + "[" + link + "]").length == 0) document.write("<" + tag + attr + link + "></" + tag + ">");
       }
  }
});


