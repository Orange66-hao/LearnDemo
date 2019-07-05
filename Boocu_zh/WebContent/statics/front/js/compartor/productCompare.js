jQuery(document).ready(function() {

    //比较栏HTML框架
    var compareBox = "";
    compareBox += '<div id="CompareBox">';
/*    compareBox += '    <div id="CompareShowBox">';
    compareBox += '        <span><!--收起--></span>'; 
    compareBox += '    </div>';*/
    //compareBox += '    <span id="ShowProductCount"></span>';//显示已选择了几种商品（后来公司不需要此需求）
    //compareBox += '    <div id="CompareBtnBox">';
    //compareBox += '        ;
	//下面两个功能自我感觉没什么用处，但是当时公司商品部就是需要这样的功能，没办法只能从了。当时这个花的时间
	//也较多
    //compareBox += '        <div id="prevBtn" title="向上"></div>';
    //compareBox += '        <div id="nextBtn" title="向下"></div>';
    //compareBox += '    </div>';
    compareBox += '    <div id="CompareProductList">';
    compareBox += '        <ul></ul>';
    compareBox += '    </div>';
    compareBox += '    <a class="ShowCompareSubmit" id = "ShowCompare" title="比较分析" href="javascript:void(0);" target="_blank" >开始对比</a><a id="ClearAllProduct">清空</a><div class="comparepop-alert"></div>';
    compareBox += '</div>';
    compareBox += '<a herf="javascript:;" class=".CompareHideBox" id="CompareHideBox">商品对比</a><i class="icon10"></i>';

    if (jQuery("#CompareBox").length == 0) {
        jQuery("body").append(compareBox);
    }

    var compareBoxUpTop = 200; //比较栏顶部距离窗口顶部的初始值 
    var speed = 500; //动画运行速度（ms）
    var pageSize = 4; //比较栏一次能显示的品种数
    var pageIndex = 0;
    var productCount = 0;
    var cookieExpires = null; //cookie的有效期:null表示有效期为浏览进程；可填入天数和具体的有效日期
    var cookieDomain = ""; //cookie的域,默认为网站的域名
    var cookiePath = '/'; //cookie的路径,如果发生cookie错误，一般是这里的路径没设置对
    if (document.URL.indexOf('WebApplication') > 0) {
        cookiePath = '/WebApplication/';
    }
     

    /*添加品种信息到cookie*/
    function AddProductToCookie(productID, productName, imgSrc, link) {
        var compareProducts = "";
        if (jQuery.cookie("compareProducts") != "" && jQuery.cookie("compareProducts") != null) {
            compareProducts = jQuery.cookie("compareProducts");
        }
     	var newCompareProduct = '{"ProductID":' + productID + ',"ProductName":"' + productName + '","ImgSrc":"' + imgSrc + '","Link":"' + link + '"};';
        compareProducts += newCompareProduct;
        jQuery.cookie("compareProducts", compareProducts, { path: cookiePath, expires: cookieExpires });
    }; 

    /*获取比较品种cookie*/
    if (jQuery.cookie("compareProducts") != "" && jQuery.cookie("compareProducts") != null) {
        var compareProducts = jQuery.cookie("compareProducts");
        compareProducts = compareProducts.substring(0, compareProducts.length - 1).split(";");
        for (var i = 0; i < compareProducts.length; i++) {
            var compareProduct = eval('(' + compareProducts[i] + ')');
            if (IsExistProduct(compareProduct["ProductID"])) {
                productCount++;
                continue;
            }
            AddProduct(compareProduct["ProductID"], compareProduct["ProductName"], compareProduct["ImgSrc"], compareProduct["Link"]);
        }
    };
    
    /*从cookie移除品种*/
    function RemoveProductFromCookie(productID, productName, imgSrc, link) {
       var compareProducts = jQuery.cookie("compareProducts");
        var regxLink = link;
		//将链接里的.和/进行转移，以免在71行代码中使用正则表达式不方便处理
        regxLink = regxLink.replace(".", "\.").replace("/", "\/");
        var regx = new RegExp('\{"ProductID":' + productID + ',[\\s\\S]*?,"Link":"' + regxLink + '"\};');
        if (compareProducts != null) {
            compareProducts = compareProducts.replace(regx, "");
        }
        jQuery.cookie("compareProducts", compareProducts, { path: cookiePath, expires: cookieExpires });
    }

    /*为添加按钮绑定点击事件*/
    jQuery(".addCompare").each(function(index) {
        jQuery(this).click(function() {
            if(jQuery(this).is(':checked')){
                if (IsExistProduct(jQuery(this).attr("id"))) {
                    $(".comparepop-alert").html("已经存在该商品！"); 
                    return false;
                }
                if (productCount >= 4){
                	$(".comparepop-alert").html("最多只能选4个商品！"); 
                    return false;
                }
                $(".comparepop-alert").html(""); 
                AddProduct(jQuery(this).attr("id"),
                    jQuery(this).attr("name"),
                    jQuery(this).attr("imgSrc"),
                    jQuery(this).attr("link")
                    );
                AddProductToCookie(jQuery(this).attr("id"),
                    jQuery(this).attr("name"),
                    jQuery(this).attr("imgSrc"),
                    jQuery(this).attr("link")
                    );
            }else{
                productCount--;
                if (productCount < 0) {
                    productCount = 0;
                }
                jQuery("#ShowProductCount").text("商品总数:" + productCount);
                var $product = jQuery("#" + jQuery(this).attr("id"), "#CompareProductList ul");
                $product.slideUp("fast", function() { $product.remove()});
                if (productCount == 0) {
                    jQuery("#CompareProductList").css("height", "");
                    jQuery("#CompareProductList ul").css("margin-top", "0px");
                }
                var oldProductID = $product.attr("id");
                var oldProductName = jQuery("a", $product).not(".removeProduct").text();
                var oldImgSrc = jQuery("img", $product).attr("src");
                var oldLink = jQuery("a", $product).not(".removeProduct").attr("href");
                RemoveProductFromCookie(oldProductID, oldProductName, oldImgSrc, oldLink);
                SetControlButton();
                if (productCount <= pageSize) {
                    jQuery("#CompareProductList ul").animate({
                        marginTop: 0
                    }, 'normal');
                }
            }
        });
    });

	//对比较器展开和收起功能的处理
    jQuery("#CompareShowBox span").click(function() {
        jQuery("#CompareBox").show("fast");
        jQuery("#CompareHideBox").show("fast");
    });

    jQuery("#CompareHideBox").click(function() {
        jQuery(this).show("fast");
        jQuery("#CompareBox").toggle("fast");
        jQuery(".icon10").toggleClass("icon10-left");
    });

    jQuery("#ClearAllProduct").click(function() {
        ClearAllProduct();
    });

	//比较栏向上滚动
    function movePrev(event) {
        if (event.target == this) {
            MoveProductList("prev");
        }
    };

	//比较栏向下滚动
    function moveNext(event) {
        if (event.target = this) {
            MoveProductList("next");
        }
    };

    /*滚动比较栏*/
    function MoveProductList(dir) {
        var l = jQuery("#CompareProductList li").length;
        var ts = l - 1;
        var ot = pageIndex;
        switch (dir) {
            case "next":
                pageIndex = (ot >= ts) ? ts : pageIndex + 1;
                break;
            case "prev":
                pageIndex = (pageIndex <= 0) ? 0 : pageIndex - 1;
                break;
            default:
                break;
        }

        var $productItems = jQuery("#CompareProductList li");
        var th = 0;
        for (var i = 0; i < pageIndex; i++) {
            th += $productItems.eq(i).outerHeight(true);
        }

        var p = th * -1;
        jQuery("#CompareProductList ul").animate({
            marginTop: p
        }, 'normal');

        SetControlButton();
    };

    /*设置滚动按钮*/
    function SetControlButton() {
        var ts = jQuery("#CompareProductList li").length - 1;
        if (pageIndex == ts) {
            jQuery("#nextBtn").unbind("click", moveNext);
        }
        else {
            jQuery("#nextBtn").bind("click", moveNext);
        }
        if (pageIndex == 0) {
            jQuery("#prevBtn").unbind("click", movePrev);
        }
        else {
            jQuery("#prevBtn").bind("click", movePrev);
        }
    };

    /*清空比较栏品种*/
    function ClearAllProduct() {
        productCount = 0;
        jQuery.cookie("compareProducts", null, { path: cookiePath });
        jQuery("#ShowProductCount").text("商品总数:0");
        jQuery("#CompareProductList li").remove();
        jQuery("#CompareProductList ul").css("margin-top", "0px");
        jQuery("#CompareProductList").css("height", "");
        jQuery("#nextBtn").unbind("click", moveNext);
        jQuery("#prevBtn").unbind("click", movePrev);
        jQuery(".addCompare").attr("checked",false);
    };

    /*判断是否已存在品种在比较栏中*/
    function IsExistProduct(productID) {
        var isExist = false;
        jQuery("li", "#CompareProductList ul").each(function() {
            if (jQuery(this).attr("id") == productID) {
                isExist = true;
                return false;
            }
        });
        return isExist;
    };

    /*显示信息*/
    function ShowMsg(msg, obj) {
    	alert(msg);
    	return false;
    	
		//当点击商品时将信息显示在商品的前面，由于公司现在不需要这样的功能，可以自己取消注释进行测试
        var position = jQuery(obj).position();
        var msgBox = '<div class="msgBox">' + msg + '</div>';
        jQuery(msgBox).insertBefore(obj).css({ top: position.top + "px", left: (position.left + jQuery(obj).outerWidth()) + "px" }).oneTime(1000, function() {
            jQuery(this).show("fast", function() {
                jQuery(this).hide("fast", function() {
                    jQuery(this).remove();
                })
            })
        }); 
    };

    /*添加品种到比较栏*/
    function AddProduct(productID, productName, imgSrc, link) {
        var html = "";
        html += '<li id="' + productID + '">';
        html += '<a value="' + productID + '" class="removeProduct" ></a >';
        html += '<div class="recad"><a href="' + link.replace(".do",".do?") + '" title="查看品种详情"><img src="' + imgSrc + '" class="thumbnail"/></a></div>';
        html += '<div class="reName"><a href="' + link.replace(".do",".do?") + '"id="reName" title="查看品种详情">' + productName + '</a></div>';
        html += '</li>';
        jQuery("#CompareProductList ul").prepend(html);
        jQuery("a.removeProduct").each(function() {
            jQuery(this).click(function() {
                productCount--;
                if (productCount < 0) {
                    productCount = 0;
                }
                jQuery("#ShowProductCount").text("商品总数:" + productCount);
                var $product = jQuery("#" + jQuery(this).attr("value"), "#CompareProductList ul");
                $product.slideUp("fast", function() { $product.remove()});
                if (productCount == 0) {
                    jQuery("#CompareProductList").css("height", "");
                    jQuery("#CompareProductList ul").css("margin-top", "0px");
                }
                var oldProductID = $product.attr("id");
                var oldProductName = jQuery("a", $product).not(".removeProduct").text();
                var oldImgSrc = jQuery("img", $product).attr("src");
                var oldLink = jQuery("a", $product).not(".removeProduct").attr("href");
                RemoveProductFromCookie(oldProductID, oldProductName, oldImgSrc, oldLink);
                SetControlButton();
                if (productCount <= pageSize) {
                    jQuery("#CompareProductList ul").animate({
                        marginTop: 0
                    }, 'normal');
                }
                jQuery("input[id="+jQuery(this).attr("value")+"]").attr("checked",false);
            });
            return false;
        });

        productCount++;
        jQuery("#ShowProductCount").text("商品总数:" + productCount);
        SetControlButton();
        jQuery("#CompareBox").show("normal");
        jQuery("#CompareHideBox").show("fast");
        checkHeight();
    };
	
    /*比较器在页面上滚动以适应屏幕的位置*/
/*    function Flow() {
        var newUpTop = compareBoxUpTop + jQuery(document).scrollTop();
        if (jQuery.browser.msie) {
            newUpTop = compareBoxUpTop + document.documentElement.scrollTop;
        }
        jQuery("#CompareBox").css("top", newUpTop);
        jQuery("#CompareHideBox").css("top", newUpTop);
    };
*/
    jQuery(".ShowCompareSubmit").click(function(event) {
        if (event.target == this) {
            var idList = new Array();
            var index = 0;
            jQuery("#CompareProductList li").each(function() {
                if (index >= 4) {
                    return false;
                }
                idList.push(jQuery(this).attr("id"));
                index++;
            });
            
           	if (index == 0){
           		$(".comparepop-alert").html("请选择对比商品");
           		return false;
            }
            //var location = document.URL;
            //var url = "analytics.do?pid=";
            this.href = "/ProCompartor/list.jspx?ids="+idList; //document.URL.substring(0,document.URL.lastIndexOf('/')+1)+url + idList.join(",");
        }
    }); 

/*    jQuery(window).everyTime(10, function() {//每10ms对比较器进行位置的重新调整，可以自己设置时间进行调试
        Flow(); //checkHeight();
    });*/

    function checkHeight() {
        if (productCount > pageSize) {
            var h = 0;
            var $productItems = jQuery("#CompareProductList li");
            for (var i = 0; i < pageSize; i++) {
                h += parseInt($productItems.eq(i).outerHeight(true));
            }
           //"asa".lastIndexOf
            jQuery("#CompareProductList").css("height", h.toString() + "px");
        }
    }

    setTimeout(checkHeight, 300);
});