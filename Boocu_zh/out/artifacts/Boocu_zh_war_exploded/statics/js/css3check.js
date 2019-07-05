/**
 * 检查是否支持CSS3。根据last-of-type来进行判断
 * 可以通过jQuery来修复ie8的不支持CSS3的一些问题。例如：
 * <pre>
 * function setMarginRightZero($ele){
 *     $ele.addClass('noMarginRight');
 * }
 * $.css3check.addProcess('.vendor-logo:last-of-type, .vendor-logo:nth-of-type(4n+0)', setMarginRightZero);
 * </pre>
 * 可以解决一些伪类在ie8中无效的问题
 * <pre>
 *    .vendor-logo｛
 *      margin-right: 30px;
 *    ｝
 *    .vendor-logo:last-of-type, .vendor-logo:nth-of-type(4n+0) {
 *      margin-right: 0;
 *    }
 * </pre>
 *
 * Created by xyf on 2015/2/13.
 */
(function ($) {
	function addCSSRule(sheet, selector, rules, index) {
		if ("insertRule" in sheet) {
			sheet.insertRule(selector + "{" + rules + "}", index);
		}
		else if ("addRule" in sheet) {
			sheet.addRule(selector, rules, index);
		}
	}

	/**
	 * 开始检查是否支持CSS3。根据last-of-type来进行判断
	 */
	function css3Check() {
		// 创建 <style> 标签
		style = document.createElement("style");

		// 可以添加一个媒体(/媒体查询,media query)属性
		// style.setAttribute("media", "screen")
		// style.setAttribute("media", "only screen and (max-width : 1024px)")

		// 对WebKit hack :(
		if (!$.browser.msie)
			style.appendChild(document.createTextNode(""));


		// 将 <style> 元素加到页面中
		document.getElementsByTagName('head')[0].appendChild(style);

		function onSheetReady() {
			$div = $('<div class="css3check" style="display: none;"><div></div><div></div></div>');
			$(document.body).append($div);
			$div2 = $('<div style="display:none;font-size: 10px;*font-size: 20px;"></div>');
			$(document.body).append($div2);
			//doCheck();
			setTimeout(doCheck, 0);
		}

		var sheet;
		if (!style || !(sheet = style.sheet)) {
			if (!$.browser.msie || !style)
				return;
			document.getElementsByTagName('head')[0].removeChild(style);
			sheet = document.createStyleSheet();
			style = sheet.owningElement;
			try {
				addCSSRule(sheet, '.css3check', 'display:none; width: 30px;');
				addCSSRule(sheet, '.css3check div', 'float: left; width: 10px; height: 1px; margin-right: 10px;');
				addCSSRule(sheet, '.css3check div:last-of-type', 'margin-right: 0;');
			} catch (e) {
				//console.log('addCSSRule err: ', e);
			}
			onSheetReady();
		} else {
			addCSSRule(sheet, '.css3check', 'display:none; width: 30px;');
			addCSSRule(sheet, '.css3check div', 'float: left; width: 10px; height: 1px; margin-right: 10px;');
			addCSSRule(sheet, '.css3check div:last-of-type', 'margin-right: 0;');
			onSheetReady();
		}
	}

	function doCheck() {
		isCss3 = ($div.height() == 1);
		hasCheck = true;
		isIe7 = ($div2.css('font-size') == '20px');
		console.log('isCss3: ' + isCss3 + ' isIe7: ' + isIe7);
		finish();
		if ($.css3check.autoFix)
			$.css3check.fix();
	}

	function finish() {
		document.getElementsByTagName('head')[0].removeChild(style);
		document.body.removeChild($div2[0]);
		document.body.removeChild($div[0]);
		style = null;
		$div = null;
		$div2 = null;
	}

	var style;
	var $div, $div2;
	var hasCheck = false;
	var isCss3 = false;
	var isIe7 = false;
	$(document).ready(function () {
		var dummyFunc = function(){};
		if(!window.console){
			window.console = {log: dummyFunc, debug: dummyFunc, error: dummyFunc};
		}

		css3Check();
	});

	var processes = [];

	/**
	 * 添加需要处理的对象的selector和处理函数
	 * @param matchSelector 需要处理的对象的selector
	 * @param callback 处理函数。格式function($ele)
	 */
	function addProcess(matchSelector, callback) {
		processes.push([matchSelector, callback]);
	}

	/**
	 * 寻找对象并处理
	 * @returns {boolean}
	 */
	function fix(node) {
		if (!(hasCheck && !isCss3)) {
			return false;
		}
		if(!node)
			node = document;
		else
			node = $(node).parent();
		$.each(processes, function (idx, ele) {
			try {
				ele[1]($(node).find(ele[0]));
			} catch (e) {
				console.log('processes '+ele[0]+'err: ', e);
			}
		});
		return true;
	}

	/**
	 * 检查是否支持CSS3。根据last-of-type来进行判断
	 * 可以通过jQuery来修复ie8的不支持CSS3的一些问题。例如：
	 * <pre>
	 * function setMarginRightZero($ele){
	 *     $ele.addClass('noMarginRight');
	 * }
	 * $.css3check.addProcess('.vendor-logo:last-of-type, .vendor-logo:nth-of-type(4n+0)', setMarginRightZero);
	 * </pre>
	 * 可以解决一些伪类在ie8中无效的问题
	 * <pre>
	 *    .vendor-logo｛
	 *      margin-right: 30px;
	 *    ｝
	 *    .vendor-logo:last-of-type, .vendor-logo:nth-of-type(4n+0) {
	 *      margin-right: 0;
	 *    }
	 * </pre>
	 */
	$.css3check = {
		/**
		 * 开始检查是否支持CSS3。根据last-of-type来进行判断。document加载后会自动进行check
		 */
		check: css3Check,
		/**
		 * 是否IE7
		 * @returns {boolean}
		 */
		isIe7: function(){
			return hasCheck && isIe7;
		},
		/**
		 * 是否支持CSS3
		 * @returns {boolean}
		 */
		isCss3: function () {
			return hasCheck && isCss3;
		},
		/**
		 * 添加需要处理的对象的selector和处理函数
		 * @param matchSelector 需要处理的对象的selector
		 * @param callback 处理函数。格式function($ele)
		 */
		addProcess: addProcess,
		/**
		 * 寻找对象并处理
		 * @returns {boolean}
		 */
		fix: fix,
		/**
		 * check之后是否自动fix
		 */
		autoFix: true
	};
	$.css3check.addProcess('', function () {
		if ($.css3check.isIe7()) {
			$('.glyphicon[data-content]').each(function (idx, ele) {
				var $ele = $(ele);
				$ele.text($ele.attr('data-content'));
			});
		}
	});

	$.css3check.addProcess('.breadcrumb > li + li', function(ele){
		if($.css3check.isIe7()) {
			$.each(ele, function (idx, ele1) {
				var $ele1 = $(ele1);
				if($ele1.prev().is('li') && !$ele1.prev().is('.breadcrumb-break')) {
					var $o = $('<before/>');
					$o.text($ele1.parent().attr('data-seperate-content') || '>');
					$ele1.before($o);
				}
			});
		}
	});
})(jQuery);
