// JavaScript Document


$(document).ready(function(){

    //slide img=======================start 
    	var autoRoll;
        var slide_image = new Object();
        slide_image.auto = null;
        slide_image.slideInit = function () {
            slide_image.imgwidth = $("div.slide-img-cont").width();
            slide_image.imgheight = $("div.slide-img-cont").height();
            slide_image.imageNum = $("ul.slide-list").children().size();
            $("ul.slide-list").css("width", function () { return slide_image.imgwidth * slide_image.imageNum; });
            for (var i = 0; i < slide_image.imageNum; i++) {
                if ($("ul.slide-control").children().size() == 0) {
                    $("ul.slide-control").html("<li>" + (i + 1).toString() + "</li>");
                } else {
                    $("ul.slide-control").children().last().after("<li>" + (i + 1).toString() + "</li>");
                }
            }
            $("ul.slide-control").children().first().addClass("select");
            slide_image.atNum = 1;
            slide_image.controlNum = $("ul.slide-control").children().size();

            autoRoll=setInterval("rollToNext(0);",5000);//启动自动滚动
        };
        //参数设置为0时表示滚动至下一张    设置为n(n>=1时) 滚动至第n张   toi设置为-1表示向左滚动，toi设置为-2时表示向右滚动
        rollToNext = function (toi) {//toiΪ0�����ǹ�������һ��ͼƬ�����������toi���Ӧ��ͼƬ
            if (toi == 0) {
            	clearInterval(autoRoll);
                var j = slide_image.atNum + 1;
                if (j > slide_image.imageNum) { j %= slide_image.imageNum; }
                slide_image.atNum = j;
                lef = (j - 1) * (-slide_image.imgwidth);
                $("ul.slide-list").animate({ left: (lef + "px") }, 1000);
                $("ul.slide-control").find(".select").removeClass("select");
                $("ul.slide-control").children().eq(j - 1).addClass("select");
                autoRoll=setInterval("rollToNext(0);",5000);
            }else if(toi == -1){
            	
            	
            }else if(toi <=-2){
            	
            	
            }else {
            	clearInterval(autoRoll);
                var j = toi;
                if (j > slide_image.imageNum) { j %= slide_image.imageNum; }
                slide_image.atNum = j;
                lef = (j - 1) * (-slide_image.imgwidth);
                $("ul.slide-list").animate({ left: (lef + "px") }, 500);
                $("ul.slide-control").find(".select").removeClass("select");
                $("ul.slide-control").children().eq(j - 1).addClass("select");
                autoRoll=setInterval("rollToNext(0);",5000);
            }

        }

        slide_image.slideInit();
        $("ul.slide-control li").mouseover(function () {
            i = $(this).index() + 1;
            $("ul.slide-list").stop();
            rollToNext(i);
        });
        $("ul.slide-control li").click(function () {
            i = $(this).index() + 1;
            $("ul.slide-list").stop();
            rollToNext(i);
        });
    //slide img=======================end
	
	//serch input
	$(":input.input_text").focus(function(){
			$("span.search-input-tishi").hide();
		}
	);
	$(":input.input_text").blur(function(){
			var thisVal=$(this).val();
			if(thisVal!=""){
				$("span.search-input-tishi").hide();
			}else{
				$("span.search-input-tishi").show();
			}
		}
	);
});
						
						
						//$(window).scroll(function() {
						//$("body").css({"background-position":"center "+$(window).scrollTop()+""});
						//	if($(window).scrollTop()>=90){
						//		$(".daohang").addClass("fixeddaohang");
						//	}else{
						//		$(".daohang").removeClass("fixeddaohang");
						//	} 
						//});
