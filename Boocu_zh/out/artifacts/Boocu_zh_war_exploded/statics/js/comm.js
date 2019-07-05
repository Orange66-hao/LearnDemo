/**
 * 项目名：读者云图书馆网站
 * 功能简述：通用js函数
 * 作者：读者云图书馆网站项目组
 * $Id: comm.js 4436 2015-07-17 03:46:54Z duzhe2 $
 */
$(document).ready(function(){
	var version = $.browser.version ;
	if ( $.browser.msie && version < 7 ){
		alert ('您使用的浏览器版本不支持本系统，请选择IE7及以上版本的浏览器');
		window.open("http://windows.microsoft.com/zh-CN/internet-explorer/download-ie");
	}else if ( $.browser.msie && version < 8 ){
		alert ('您使用的IE浏览器版本版本较低。为了实现更好的显示效果，请选择IE8及以上版本的浏览器');
	}
});

(function(){
	if(typeof JSON === 'undefined'){
		var a = document.createElement('script');
		a.setAttribute('src', '/js/json2.min.js');
		document.getElementsByTagName('head')[0].appendChild(a);
	}
	if(window.opener){
		try{
			window.opener.$.blankPopupPrevent.clear(true);
		}catch(e){
			console.log(e);
		}
	}
	$.blankPopupPrevent = {
		active: true,
		list : [],
		clear : function(clearActive) {
			this.list = [];
			if(clearActive && this.active){
				this.active = false;
				$(document).off('click', 'a[target]', watchIt);
			}
		}
	};
	$(document).on('click', 'a[target]', watchIt);
	function watchIt(){
		var $o = $(this);
		if(arguments[0] && arguments[0].target && arguments[0].target.tagName == 'AREA'){
			$o = $(arguments[0].target);
		}
		if($o.hasClass('noWatchIt'))
			return true;
		var id = setTimeout(function() {
			if(0 <= $.inArray(id, $.blankPopupPrevent.list)){
				$.blankPopupPrevent.clear();
				alert('请允许浏览器弹出窗口！');
			}
		}, 5000);
		$.blankPopupPrevent.list.push(id);
		return true;
	}

	$(document).on('click', '.adClicker', function(){
		var that = $(this);
		switch(that.data('adPostStatus')){
		case 'posting':
			return false;
			break;
		case 'posted':
			that.removeData('adPostStatus');
			return true;
			break;
		default:
			if(that.attr('target') && !$.inArray(that.attr('target'), ['_self', '_top', '_parent'])) {
				$.ajax({
					url: "/ajax.php?act=addAdClickCount&ad_id=" + that.data('adId') + "&ad_position_rlt_id=" + that.data('adPosRltId')
				});
				return true;
			}
			var f = function(){
				if(arguments.length)
					clearTimeout(timeId);
				if(that.data('adPostStatus') == 'posting') {
					that.data('adPostStatus', 'posted');
					that[0].click();
				}
			};
			that.data('adPostStatus', 'posting');
			var timeId = setTimeout(f, 2000);
			$.ajax({
				url: "/ajax.php?act=addAdClickCount&ad_id="+that.data('adId')+"&ad_position_rlt_id="+that.data('adPosRltId'),
				success: f
			});
			return false;
		}
	});
})();

function getClickFuncCart(ele) {
	return function () {
		var $e = $(this);
		if ($e.hasClass('active')) {
			$e.attr('disabled', 'disabled');
			if(ele.is_virtual){
				$.getJSON('/front/cart/remove.jhtml', {id:ele.book_id}, function(data){
					if(data.result){
						$e.removeClass('active');
						//$(document).trigger('numberChange.cart', data.cart_number);
						$e.showPopover({content: '移出购物车成功'});
						$(document).trigger('numberChange.cart', data.cart_number);
						location.reload();
					}else{
						$e.showPopover({content: '移出购物车失败'});
					}
					$e.removeAttr('disabled');
				});
			}else{
				$.getJSON('/ajax.php?act=addBookToCar', {id: ele.book_id}, function (data) {
					if (data.status) {
						$e.addClass('active');
						$e.showPopover({content: '添加到购物车成功'});
						moveToCartAnim.call($e);
						$(document).trigger('numberChange.cart', data.cart_number);
					} else {
						if(data.code == 1) {
							$e.showPopover({content: '添加到购物车成功'});
							moveToCartAnim.call($e);
						}else {
							$e.showPopover({content: data.msg || '添加到购物车失败'});
						}
					}
					$e.removeAttr('disabled');
				});
			}
			//alert('已经在购物车中了');
		} else {
			$e.attr('disabled', 'disabled');
			$.getJSON('/front/cart/add.jspx', {id: ele.book_id}, function (data) {
				if (data.statu) {
					$e.addClass('active');
					$(document).trigger('numberChange.cart', data.cart_number);
					$e.showPopover({content: '添加到购物车成功'});
					moveToCartAnim.call($e);
				} else {
					if(data.result == 1) {
						$e.addClass('active');
						$(document).trigger('numberChange.cart', data.cart_number);
						$e.showPopover({content: '添加到购物车成功'});
						moveToCartAnim.call($e);
					}else
						alert(data.msg || '添加到购物车失败');
				}
				$e.removeAttr('disabled');
			});
		}
		return false;
	};
}
function getClickFuncFocus(ele){
	return function () {
		var $e = $(this);
		if ($e.hasClass('active')) {
			$.getJSON('/ajax.php?act=removeBookFocus', {book_id: ele.book_id}, function (data) {
				if (data.status) {
					$e.removeClass('active');
					$e.showPopover({content: data.msg || '删除收藏成功'});
				} else {
					if (data.needLogin) {
						$(document).trigger('redirectLogin');
						$e.showPopover({content: data.msg || '需要登录'});
					} else {
						$e.showPopover({content: data.msg || '删除收藏失败'});
					}
				}
			});
		} else {
			$.getJSON('/ajax.php?act=addBookFocus', {book_id: ele.book_id}, function (data) {
				if (data.status) {
					$e.addClass('active');
					$e.showPopover({content: '添加收藏成功'});
				} else {
					if (data.needLogin) {
						$(document).trigger('redirectLogin');
						$e.showPopover({content: '需要登录'});
					} else {
						$e.showPopover({content: data.msg || '添加收藏失败'});
					}
				}
			});
		}
		return false;
	};
}
function getClickFuncRead(ele){
	return function () {
		window.open('/catalog.php?book_id=' + ele.book_id + '&preview=0');
		return false;
	};
}
function getClickFuncPreview(ele){
	return function () {
		window.open('/catalog.php?book_id=' + ele.book_id + '&preview=1');
		return false;
	};
}
function getClickFuncBuy(ele){
	return function(){
		location.href='trolley.php?buybook=' + ele.book_id;
		return false;
	};
}

$(document).on('numberChange.cart', function(ev, cart_number){
	showCartNumber(cart_number);
});

function showCartNumber(val) {
	if(!val)
		val = '0';
	$('.cart-number').text(val);
}

function isMobil(s) {
    var patrn = /(^0?1[3-9][0-9]{9}$)/;
    return patrn.exec(s);

}

/**
 * @return {boolean}
 */
function IsURL(str_url) {
    var strRegex = "^((https|http|ftp|rtsp|mms)?://)"
        + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@  
       + "(([0-9]{1,3}\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184  
        + "|" // 允许IP和DOMAIN（域名） 
       + "([0-9a-z_!~*'()-]+\.)*" // 域名- www.  
        + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\." // 二级域名  
        + "[a-z]{2,6})" // first level domain- .com or .museum  
        + "(:[0-9]{1,4})?" // 端口- :80  
       + "((/?)|" // a slash isn't required if there is no file name  
        + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
    var re = new RegExp(strRegex);
    //re.test() 
    if (re.test(str_url)) {
        return (true);
    } else {
        return (false);
    }
}


function checkusername(username){
	var str=username;
	var Expression=/^([A-Za-z_\u4E00-\u9FA5])([A-Za-z0-9_\u4E00-\u9FA5])*$/;
	return Expression.test(str);
}



function checkepassword(password){
	var str=password;
	var Expression=/^[A-Za-z0-9._]{6,20}$/;
	return Expression.test(str);
}


/** 获取COOKIE
 */
function GetCookie(name) {
	var arg = name + "=";
	var alen = arg.length;
	var clen = document.cookie.length;
	var i = 0;
	while (i < clen) {
		var j = i + alen;
		if (document.cookie.substring(i, j) == arg) return getCookieVal(j);
		i = document.cookie.indexOf(" ", i) + 1;
		if (i == 0) break;
	}
	return null;
}


/** 设置COOKIE */
function SetCookie (name, value) {
	var argv = arguments;
	var argc = arguments.length;
	var expires = (argc > 2) ? argv[2] : null;
	var path = (argc > 3) ? argv[3] : null;
	var domain = (argc > 4) ? argv[4] : null;
	var secure = (argc > 5) ? argv[5] : false;
	document.cookie = name + "=" + escape (value)
	+ ((expires == null) ? "" : ("; expires=" + expires.toGMTString()))
	+ ((path == null) ? "" : ("; path=" + path))
	+ ((domain == null) ? "" : ("; domain=" + domain))
	+ ((secure == true) ? "; secure" : "");
}


/** 删除COOKIE */
function DeleteCookie (name) {
	var exp = new Date();
	exp.setTime (exp.getTime() - 1);
	var cval = 0;
	document.cookie = name + "=" + cval + "; expires=" + exp.toGMTString();
}

function getCookieVal(offset) {
	var endstr = document.cookie.indexOf (";", offset);
	if (endstr == -1)endstr = document.cookie.length;
	return decodeURIComponent(document.cookie.substring(offset, endstr));
}

function __typeof__(o){
	if(typeof o == 'undefined')
		return 'Null';
	return o.constructor.name;
}

try{
	
// 将js对象序列化为JSON字符串
Object.defineProperty(Object.prototype, "Serialize", {
	enumerable : false,	// 不让它可枚举。以免jQuery.on({ })失败
	value: function()
	{
		var type = __typeof__(this);
		switch(type)
		{
		case 'Null':
			return 'null';
		case 'Array' :
			var strArray = '['; 
			for ( var i=0 ; i < this.length ; ++i )
			{
				var value = ''; 
				if ( this[i] )
				{
					value = this[i].Serialize();
				}
				strArray += value + ',';
			}
			if ( strArray.charAt(strArray.length-1) == ',' )
			{
				strArray = strArray.substr(0, strArray.length-1);
			}
			strArray += ']';  
			return strArray;
		case 'Date' :
			return 'new Date(' + this.getTime() + ')';
		case 'Boolean' :
		case 'Function' :
		case 'Number' :
			return this.toString();
		case 'String' :
			return '"' + this + '"';
		default :
			var serialize = '{'; 
			for ( var key in this )
			{
				if ( key == 'Serialize' ) continue; 
				var subserialize = 'null';
				if ( this[key] != undefined )
				{
					subserialize = this[key].Serialize();
				}
				serialize += '\r\n' + key + ' : ' + subserialize + ',';
			}
			if ( serialize.charAt(serialize.length-1) == ',' )
			{
				serialize = serialize.substr(0, serialize.length-1);
			}
			serialize += '\r\n}';
			return serialize;
		}
	}
});

}catch(e) {
//console.log(e);
}

/**
 * 选项卡效果
 */
function showTab(arg1,arg2){
	
	$(arg1).removeClass('on');
	$(arg1+':eq(0)').addClass('on');
	$(arg2).hide();
	$(arg2+':eq(0)').show();
	
	$(arg1).each(function(index){
		var mouseTime;
		$(this).mouseover(function(){
			mouseTime = setTimeout(function(){
				$(arg2).hide();
				$(arg2+':eq('+index+')').show();
				
				$(arg1).removeClass('on');
				$(arg1+':eq('+index+')').addClass('on');
			},200);

		}).mouseout(function(){
			clearTimeout(mouseTime);
		});
	});
}

/**
 * 排行榜异步加载
 */
function showTopNav(arg1){
	
	$(arg1).removeClass('on');
	$(arg1+':eq(0)').addClass('on');
	
	$(arg1).each(function(index){
		var mouseTime;
		$(this).mouseover(function(){	
			mouseTime = setTimeout(function(){
				$(arg1).removeClass('on');
				$(arg1+':eq('+index+')').addClass('on');
			},100);
		}).mouseout(function(){
			clearTimeout(mouseTime);
		});
	});
}

/**
 * 选项卡效果，自动关闭
 */
function showPopup(arg1,arg2,arg3){
	
	var $arg1 = $(arg1);
	var $arg2 = $arg1.children(arg2);
	var $arg3 = $arg1.children(arg3);
	
	var hideAll = function(){
		$arg2.removeClass('on');
		$arg3.hide();
	};
	
	hideAll();
	
	$arg1.each(function(index){
		var outTimer;
		$(this).hover(function(){
			
			outTimer = setTimeout(function(){
					hideAll();
					$(arg1+':eq('+index+')').children(arg3).show();
					$(arg1+':eq('+index+')').children(arg2).addClass('on');
				},250);
			},function(){
				clearTimeout(outTimer);
				hideAll();
			});
		
		
	});
	
	$('.close').click(function(){
		hideAll();
	});
	
	
}

function shortTimeStr(time) {
	var dateTimeStamp;
	if(typeof time == 'number')
		dateTimeStamp = time;
	else if(typeof time == 'string'){
		dateTimeStamp = new Date(Date.parse(time.split('.')[0].replace(/-/g, '/'))).getTime();
	}else if(time instanceof Date){
		dateTimeStamp = time.getTime();
	}else{
		return;
	}
//JavaScript函数：
	var minute = 1000 * 60;
	var hour = minute * 60;
	var day = hour * 24;
	var halfamonth = day * 15;
	var month = day * 30;

	var result;
	var now = new Date().getTime();
	var diffValue = now - dateTimeStamp;
	if (diffValue < 0) {
		//若日期不符则弹出窗口告之
		//alert("结束日期不能小于开始日期！");
		return
	}
	var monthC = diffValue / month;
	var weekC = diffValue / (7 * day);
	var dayC = diffValue / day;
	var hourC = diffValue / hour;
	var minC = diffValue / minute;
	if (monthC >= 1) {
		result = "发表于" + parseInt(monthC) + "个月前";
	}
	else if (weekC >= 1) {
		result = "发表于" + parseInt(weekC) + "周前";
	}
	else if (dayC >= 1) {
		result = "发表于" + parseInt(dayC) + "天前";
	}
	else if (hourC >= 1) {
		result = "发表于" + parseInt(hourC) + "个小时前";
	}
	else if (minC >= 1) {
		result = "发表于" + parseInt(minC) + "分钟前";
	} else
		result = "刚刚发表";
	return result;
}

String.prototype.endWith=function(s){
	if(s==null||s==""||this.length==0||s.length>this.length)
		return false;
	if(this.substring(this.length-s.length)==s)
		return true;
	else
		return false;
	return true;
}

String.prototype.startWith=function(s){
	if(s==null||s==""||this.length==0||s.length>this.length)
		return false;
	if(this.substr(0,s.length)==s)
		return true;
	else
		return false;
	return true;
}

$(document).ready(function(){
	var goTop = $('.fix-block .go-top');
	if(goTop.length){
		$(window).scroll(function(){
			if($(window).scrollTop()>20){
				goTop.fadeIn();
			}else{
				goTop.fadeOut();
			}
		});
		goTop.click(function(){
			$('html, body').animate({scrollTop:0}, 500);
			return false;
		});
	}

	var topCart = $('.top-cart');
	var topCartOffset = topCart.offset();
	var animate1 = {
			left: (topCartOffset.left - 100) + 'px', 
			top: (topCartOffset.top + 30)+'px',
			
			};
	var animate2 = {left: (topCartOffset.left) + 'px', top: (topCartOffset.top)+'px'};
	window.moveToCartAnim = function (isFix) {
		var that = $(this);
		var offset = that.offset();
		var that1 = that.clone();
		that1.css({position: 'absolute', 'z-index': 500, left: offset.left + 'px', top: offset.top + 'px'})
			.appendTo($(document.body))
			.animate($.extend(animate1,{width:(that.width()/2)+"px",height:(that.height()/2)+"px"}))
			.animate(animate2, function () {
				that1.remove();
			}).fadeTo(0, 0.1).hide(0);
	}
	
	
	
});


/**
 * @param paramName   参数键
 * @param replaceWith 参数值
 * @param url 要更改的url
 * @return  url 更改后的url 
 */

function replaceParamVal(paramName,replaceWith,url) {
    var oUrl = url || location.href.toString();
    if(oUrl.indexOf("?") == -1){
        return oUrl+"?"+paramName+"="+replaceWith;
    }

    if(oUrl.indexOf(paramName) == -1){
        if(oUrl.substr(-1,1) == "?")
            return oUrl+paramName+"="+replaceWith;
        return  oUrl+"&"+paramName+"="+replaceWith;
    }

    var re=eval('/('+ paramName+'=)([^&]*)/gi');
    return oUrl.replace(re,paramName+'='+replaceWith);

}


/**
 * 添加、删除 购物车
 */
$(document).ready(function(){	
	$(".book-btn-cart").click(function(){
		var bookInfoEle = $(this).parents('.book-info');
		var ele = {
			book_id:bookInfoEle.data('book_id')||0,
			quantity : bookInfoEle.data('quantity')||1
		}
		var $e = $(this);
		
		if ($e.hasClass('active')) {
			$e.attr('disabled', 'disabled');
			$.getJSON('/cart/remove.jspx', {id:ele.book_id,quantity:ele.quantity}, function(data){
				if(data.result == true ){
					$e.showPopover({content: data.msg || '删除成功'});
					$(document).trigger('numberChange.cart', data.cart_number);
					$e.removeClass("active");
				}else{
					$e.showPopover({content: data.msg || '删除失败'});
				}
				$e.removeAttr('disabled');
			});
			
		} else {
			$e.attr('disabled', 'disabled');
			$.getJSON('/cart/add.jspx', {id: ele.book_id,quantity:ele.quantity}, function (data) {
				if (data.result== true) {
					$e.addClass('active');
					$(document).trigger('numberChange.cart',data.msg ||  data.cart_number);
					$e.showPopover({content: data.msg || '添加到购物车成功'});
					
					moveToCartAnim.call($e.parents("li").find("img.book-cover"));
				} else {
					alert(data.msg || '添加到购物车失败');
				}
				$e.removeAttr('disabled');
			});
		}
		return false;
		
	});

	
	
	
	$(".container ").on("click",".book-btn-heart",function(event){
	
			var bookInfoEle = $(this).parents('.book-info');
			var ele = {
				book_id:bookInfoEle.data('book_id')||0
			}
			var $e = $(this);
			if ($e.hasClass('active')) {
				$.post('/collect/del.jspx', {bookId: ele.book_id}, function (data) {
					if (data.status) {
						$e.removeClass('active');
						$e.showPopover({content: '删除收藏成功'});
					} else {
						if (data.needLogin) {
							$(document).trigger('redirectLogin');
							$e.showPopover({content: data.msg || '需要登录'});
						} else {
							$e.showPopover({content: data.msg || '删除收藏失败'});
						}
					}
				},'json');
			} else {
				$.post('/collect/add.jspx', {bookId: ele.book_id}, function (data) {
					if (data.status) {
						$e.addClass('active');
						$e.showPopover({content:data.msg || '添加收藏成功'});

					} else {
						if (data.needLogin) {
							$(document).trigger('redirectLogin');console.log("fire me")
							$e.showPopover({content:data.msg || '需要登录'});
						} else {
							$e.showPopover({content: data.msg || '添加收藏失败'});
						}
					}
				},"json");
			}
			return false;
	});
	
});



