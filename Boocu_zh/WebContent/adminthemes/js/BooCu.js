$.fn.dialog.defaults.top=100;
//$.fn.combo.defaults.height=308;
/* $.fn.combo.defaults=$.extend({},$.fn.combo.defaults,{heihgt:28} } */


//时间公共getFormatDateByLong(value, "yyyy-MM-dd")
    Date.prototype.format = function (format) {  
        var o = {  
            "M+": this.getMonth() + 1,  
            "d+": this.getDate(),  
            "h+": this.getHours(),  
            "m+": this.getMinutes(),  
            "s+": this.getSeconds(),  
            "q+": Math.floor((this.getMonth() + 3) / 3),  
            "S": this.getMilliseconds()  
        };  
        if (/(y+)/.test(format)) {  
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));  
        }  
        for (var k in o) {  
            if (new RegExp("(" + k + ")").test(format)) {  
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));  
            }  
        }  
        return format;  
    };  
    function getFormatDateByLong(l, pattern) {  
        return getFormatDate(new Date(l), pattern);  
    }     
    
    function getFormatDate(date, pattern) {  
        if (date == undefined) {  
            date = new Date();  
        }  
        if (pattern == undefined) {  
            pattern = "yyyy-MM-dd hh:mm:ss";  
        }  
        return date.format(pattern);  
    }
 
 function newTab(title,url){
	parent.addTab1(title,url);
}

$.extend($.fn.validatebox.defaults.rules, {
    mobile: {
        validator: function (value, param) {
        	if(value==""){
        		return false;
        	}
            return /^0?(13[0-9]|15[012356789]|18[0236789]|14[57]|170)[0-9]{8}$/.test(value);
        },
        message: '手机号码不正确'
    },
    tel: {
        validator: function (value, param) {
            return /^((\+86)?|\(\+86\)|\+86\s)0?([0-9]\d-?\d{6,8}|[3-9][134579]\d-?\d{6,7}|[3-9][24680]\d{2}-?\d{6})(-\d{3})?$/.test(value);
        },
        message: '固定电话号码不正确'
    },
    money:{
    	validator: function (value, param) {
            return /^(([1-9]\d*)|\d)(\.\d{1,2})?$/.test(value);
        },
        message: '只能为浮点数并且不能为空'
    }
});

var isScroll = function (el) {  
    // test targets  
    var elems = el ? [el] : [document.documentElement, document.body];  
    var scrollX = false, scrollY = false;  
    for (var i = 0; i < elems.length; i++) {  
        var o = elems[i];  
        // test horizontal  
        var sl = o.scrollLeft;  
        o.scrollLeft += (sl > 0) ? -1 : 1;  
        o.scrollLeft !== sl && (scrollX = scrollX || true);  
        o.scrollLeft = sl;  
        // test vertical  
        var st = o.scrollTop;  
        o.scrollTop += (st > 0) ? -1 : 1;  
        o.scrollTop !== st && (scrollY = scrollY || true);  
        o.scrollTop = st;  
    }  
    // ret  
    return {  
        scrollX: scrollX,   
        scrollY: scrollY  
    };  
};  
$(function(){
	resetBtn();
}); 

$(window).resize(function() {
	resetBtn();
	//为搜索框增加回车事件 add by fang 20150811
	$("#searchKeyword").keydown(function(e){
		if(e.which == 13){
			$(this).next().trigger("click");
		}
		
	});
	//增加窗口自适应
	$(window).resize(function(){
		$(".easyui-datagrid").datagrid('resize');
	});
})

function resetBtn(){
	 if(isScroll().scrollY){
	    	$(".buttonWrap").addClass("fixed");
	    }else{
	    	$(".buttonWrap").removeClass("fixed");
	    }
}