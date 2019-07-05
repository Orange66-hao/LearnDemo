(function ($) {
	// jQuery版本兼容性
	$.fn.extend({
		live: function (events, data, handler) {
			$(this).on(events, data, handler);
		}
	});
	function uaMatch(ua) {
		ua = ua.toLowerCase();
		var rwebkit = /(webkit)[ \/]([\w.]+)/,
			ropera = /(opera)(?:.*version)?[ \/]([\w.]+)/,
			rmsie = /(msie) ([\w.]+)/,
			rmozilla = /(mozilla)(?:.*? rv:([\w.]+))?/;

		var match = rwebkit.exec(ua) ||
			ropera.exec(ua) ||
			rmsie.exec(ua) ||
			ua.indexOf("compatible") < 0 && rmozilla.exec(ua) || [];

		return {browser: match[1] || "", version: match[2] || "0"};
	}

	$.browser = {};
	var browserMatch = uaMatch(navigator.userAgent);
	if (browserMatch.browser) {
		$.browser[browserMatch.browser] = true;
		$.browser.version = browserMatch.version;
	}
	if ($.browser.webkit) {
		$.browser.safari = true;
	}


	$(document).on('click', '.tab-btn', function(){
		var that = $(this);
		if(that.hasClass('active'))
			return false;
		var p = that.data('tabPanels'), i = that.data('tabId');
		if(p && typeof i !== 'undefined'){
			var panel = $('#'+p), child = panel.children(), theChild = child.eq(i), tabBtns = that.siblings();
			if(theChild.length){
				child.removeClass('active');
				theChild.addClass('active');
				tabBtns.removeClass('active');
				that.addClass('active');
				return false;
			}
		}
		return true;
	});

	$.fn.expandable = function(){
		function expandText($expand){
			if(!$.support.tbody){
				$expand.text($expand.parent().hasClass('expanded') ? '^' : 'v');
			}
		}
		$(this).each(function(idx, ele){
			var expandable = $(ele);
			var $o = $('<div class="expand"></div>');
			expandable.append($o);
			expandable.on('click', '.expand', function(){
				var that =$(this);
				that.parent().toggleClass('expanded');
				expandText(that);
			});
			expandText($o);
		});
		return this;
	};

	$.fn.showPopover = function (opt) {
		var data_name = 'my-popover';
		var defaults = {
			placement: 'bottom',
			trigger: 'manual',
			destroyTimeout: 2000
		};
		return $(this).each(function (idx, ele) {
			var $item = $(ele), data = $item.data(data_name);
			//console.log('showPopover ' + opt.content + ' on ' + $item.text());
			if (!data) {
				data = {};
				$item.data(data_name, data);
			}
			var f = function () {
				var o = $.extend(true, {}, defaults, opt);
				data.opt = o;
				//console.log('showPopover show: ' + opt.content);
				$item.popover(o).popover('show');
				data.destoryTimeoutId = setTimeout(function () {
					//console.log('showPopover timeout: ' + data.destoryTimeoutId);
					delete data.destoryTimeoutId;
					//console.log('showPopover destroy');
					$item.popover('destroy');
				}, o.destroyTimeout);
				//console.log('showPopover setTimeout: ' + data.destoryTimeoutId);
			};
			if (data && data.destoryTimeoutId) {
				clearTimeout(data.destoryTimeoutId);
				//console.log('showPopover clearTimeout ' + data.destoryTimeoutId);
				delete data.destoryTimeoutId;
				//console.log('showPopover destroy');
				try {
					$item.popover('destroy');
					// IMPORTANT: 这里必须大于等于150，否则新的popover不会出现
					setTimeout(f, 150);
					return;
				} catch (e) {
					console.log(e);
				}
			}
			f();
		});
	};
})(jQuery);