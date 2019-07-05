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
	
	//百度统计代码开始
	var _hmt = _hmt || [];
	  var hm = document.createElement("script");
	  hm.src = "https://hm.baidu.com/hm.js?7def3c302a96a82e436b284cd8cf6a41";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	//百度统计代码结束
	  
	// 百度商桥代码开始
	 var _hmt2 = _hmt || [];
	 var hm2 = document.createElement("script");
	 hm2.src = "https://hm.baidu.com/hm.js?be7f6a5c6cf25ae883a8a5a131a4311f";
	 var s2 = document.getElementsByTagName("script")[0]; 
	 s2.parentNode.insertBefore(hm2, s2);
	 //百度商桥代码结束
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
		$.includePath = ''; 
		$(target).html(data);
		$.include(['/statics/front/js/jquery.min.js', '/statics/front/js/lazyload/jquery.lazyload.min.js', '/statics/front/js/jquery.SuperSlide.2.1.1.js','/statics/front/js/main.js']);
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

