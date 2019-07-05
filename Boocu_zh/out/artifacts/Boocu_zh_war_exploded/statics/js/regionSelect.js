/**
 * 椤圭洰鍚嶏細璇昏€呬簯鍥句功棣嗙綉绔�
 * 鍔熻兘绠€杩帮細琛屾斂鍖哄煙閫夋嫨
 * 浣滆€咃細璇昏€呬簯鍥句功棣嗙綉绔欓」鐩粍
 * $Id: regionSelect.js 721 2014-04-21 08:32:20Z wangjie $
 */
(function($) {
	var initCheckers = null;
	var data_name = 'regionSelect_jquery_data';
	var methods = {
		init : function(options) {
			return this.each(function() {
				var $this = $(this), data = $this.data(data_name);
				var settings = $.extend({ }, $.fn.regionSelect.defaults);
				if ($.meta) {
					$.extend(settings, $this.data());
				}
				$.extend(settings, options);

				if (!data) {
					data = { };
					$this.data(data_name, data);
				}

				data.settings = settings;
				data.input = null;
				data.params = { };

				data.container = $('<'+settings.containerTagName+' class="regionSelect_container"></'+settings.containerTagName+'>');
				if ($this.is(':input')) {
					$this.after(data.container);
					data.input = $this;
					data.oldCssDisplay = $this.css('display');
					if(typeof settings.initValue == 'undefined')
						settings.initValue = $this.val();
					else {
						settings.initValue = '' + settings.initValue;
						$this.val(settings.initValue);
					}
					$this.css('display', 'none');
				} else {
					$this.append(data.container);
				}

				var dd = {
					$this : $this,
					data : data
				};
				CheckerRunner(initCheckers, function(callbackData) {
					appendSelect(callbackData.$this, callbackData.data);
				}, dd);

				// if(settings.initValue)
				// setValue.call($this, settings.initValue);
			});
		},
		checkValid : checkValid,
		getLastError: function() {
			var $this = $(this), data = $this.data(data_name);
			return data.lastError;
		},
		getValue : getValue,
		setValue : setValue,
		destroy : function() {
			return this.each(function() {
				var $this = $(this), data = $this.data(data_name);
				if(!data || !data.container)
					return;
				data.container.remove();
				if(data.input) {
					if(data.oldCssDisplay)
						$this.css('display', data.oldCssDisplay);
					else
						$this.removeCss('display');
				}
				$this.removeData(data_name);
			});
		},
		isRegionSelect: function() {
			var $this = $(this), data = $this.data(data_name);
			return (data && data.container);
		}
	};

	initCheckers = [
		function(checkerData, successCallback, callbackData) {
			if (checkerData.data.settings.initValue && !checkerData.data.params.initInfo) {
				urlGetInfo(checkerData.data.settings, checkerData.data.settings.initValue, function(json) {
					if (json.status) {
						checkerData.data.params.initInfo = json.data;
						successCallback(callbackData);
					} else {
						checkerData.data.settings.error('initInitInfo failed!');
					}
				});
			} else
				successCallback(callbackData);
		},
		function(checkerData, successCallback, callbackData) {
			if (checkerData.data.settings.parent && !checkerData.data.params.parentInfo) {
				urlGetInfo(checkerData.data.settings, checkerData.data.settings.parent, function(json) {
					if (json.status) {
						checkerData.data.container.append('<span class="regionSelect_parent_name">' + json.data[checkerData.data.settings.regionFullNameFieldName] + '</span>');
						checkerData.data.params.parentInfo = json.data;
						successCallback(callbackData);
					} else {
						checkerData.data.settings
								.error('initParentInfo failed!');
					}
				});
			} else
				successCallback(callbackData);
		},
		function(checkerData, successCallback, callbackData) {
			if (checkerData.data.params.initInfo && checkerData.data.params.parentInfo) {
				var level1 = parseInt(checkerData.data.params.initInfo[checkerData.data.settings.levelFieldName]);
				var level2 = parseInt(checkerData.data.params.parentInfo[checkerData.data.settings.levelFieldName]);
				if (level1 < level2) {
					checkerData.data.settings.error('init level < parent level');
					return;
				}
				var codeSystem = checkerData.data.settings.codeSystem;
				for ( var i = 1; i <= level2; i++) {
					if (codeGetLevelPart(codeSystem, checkerData.data.settings.initValue, i) != codeGetLevelPart( codeSystem, checkerData.data.settings.parent, i)) {
						checkerData.data.settings.error('init , parent not match');
						return;
					}
				}
			}
			successCallback(callbackData);
		}
	];

	function CheckerRunner(checkers, successCallback, checkerData) {
		var dd = {
			checkers : checkers,
			successCallback : successCallback,
			checkerData : checkerData,
			id : 0
		};
		checkers[0](checkerData, CheckerRunner_runner, dd);
	}
	function CheckerRunner_runner(data) {
		if (++data.id >= data.checkers.length) {
			data.successCallback(data.checkerData);
		} else {
			data.checkers[data.id](data.checkerData, CheckerRunner_runner, data);
		}
	}

	function appendSelect($this, data) {
		if (data.params.parentInfo && data.params.parentInfo[data.settings.levelFieldName])
			data.params.level = parseInt(data.params.parentInfo[data.settings.levelFieldName]) + 1;
		else
			data.params.level = 1;
		data.params.initCodeParts = [];
		if (data.settings.initValue) {
			var l = codeGetLevel(data.settings.codeSystem, data.settings.initValue);
			for ( var i = 1; i <= l; i++) {
				data.params.initCodeParts.push(codeGetLevelPart(data.settings.codeSystem, data.settings.initValue, i));
			}
		}
		data.params.currFullCode = data.settings.parent;
		appendSelect2($this, data);
	}
	function appendSelect2($this, data) {
		if (data.params.level > data.settings.maxLevel) {
			appendEnd($this, data);
			return;
		}

		var $select = $('<select class="regionSelect_select"><option value="">璇烽€夋嫨鍖哄煙</option></select>');
		data.container.append($select);
		$select.data(data_name, {
			root : $this,
			rootData : data,
			level : data.params.level
		});
		$select.change(selectChanged);
		urlGetList(data.settings, data.params.currFullCode, function(json) {
			var initCodePart = null;
			if (data.params.initCodeParts
					&& data.params.level <= data.params.initCodeParts.length)
				initCodePart = data.params.initCodeParts[data.params.level - 1];
			var needNext = false;
			$.each(json.data, function(idx, data1) {
				var str = '<option value="' + data1[data.settings.fullCodeFieldName] + '"';
				if (initCodePart && initCodePart == data1[data.settings.currCodeFieldName]) {
					str += ' selected="selected" ';
					data.params.currFullCode = data1[data.settings.fullCodeFieldName];
					needNext = true;
				}
				str += '>' + data1[data.settings.regionNameFieldName] + '</option>';
				var $option = $(str);
				$option.data(data_name, data1);
				$select.append($option);
			});
			data.params.level++;
			if (needNext)
				appendSelect2($this, data);
			else {
				appendEmptySelect($this, data);
			}
		});
	}
	function appendEmptySelect($this, data) {
		if (data.params.level > data.settings.maxLevel || data.params.level > data.settings.minLevel) {
			appendEnd($this, data);
			return;
		}
		var $select = $('<select class="regionSelect_select"><option value="">璇烽€夋嫨</option></select>');
		data.container.append($select);
		data.params.level++;
		appendEmptySelect($this, data);
	}
	function appendEnd($this, data) {
		delete data.params.initCodeParts;
	}

	function selectChanged() {
		var selectData = $(this).data(data_name);
		var $this = selectData.root, data = selectData.rootData, level = selectData.level;
		$(this).nextAll(".regionSelect_select").remove();
		data.params.currFullCode = getValue.call($this);
		if (data.input) {
			data.input.val(data.params.currFullCode);
		}
		var thisValue = $(this).val();
		if (thisValue) {
			data.params.level = level + 1;
			appendSelect2($this, data);
		} else {
			data.params.level = level + 1;
			appendEmptySelect($this, data);
		}
	}

	function checkValid() {
		var $this = $(this), data = $this.data(data_name);
		var val = getValue.call(this);
		var check = data.settings.check;
		if(check.notEqualParent && val == data.settings.parent){
			data.lastError = '娌℃湁閫夋嫨鏈夋晥鐨勫尯鍩�';
			return false;
		}
		if(typeof check.minLevel != 'undefined' && data.params.currLevel < check.minLevel) {
			data.lastError = '閫夋嫨鐨勫眰绾у繀椤诲ぇ浜庣瓑浜�' + check.minLevel;
			return false;
		}
		if(typeof check.maxLevel != 'undefined' && data.params.currLevel > check.maxLevel){
			data.lastError = '閫夋嫨鐨勫眰绾у繀椤诲皬浜庣瓑浜�' + check.maxLevel;
			return false;
		}
		data.lastError = '';
		return true;
	}

	function getValue() {
		var $this = $(this), data = $this.data(data_name);
		var selects = data.container.find('.regionSelect_select');
		for ( var i = selects.length - 1; i >= 0; i--) {
			var v = $(selects[i]).val();
			if (v) {
				data.params.currLevel = $(selects[i]).data(data_name).level;
				if(data.input)
					data.input.val(v);
				return v;
			}
		}
		if (data.params.parentInfo) {
			data.params.currLevel = data.params.parentInfo[data.settings.levelFieldName];
		} else
			data.params.currLevel = 0;
		if(data.input)
			data.input.val(data.settings.parent);
		return data.settings.parent;
	}
	function setValue() {
		var $this = $(this), data = $this.data(data_name);
		var settings = data.settings;
		settings.initValue = arguments[0] ? arguments[0] : '';
		settings.initValue = '' + settings.initValue;
		$this.regionSelect('destroy').regionSelect(settings);
	}

	function urlGetInfo(settings, code, callback) {
		var ajaxData = { };
		ajaxData[settings.infoIdParamName] = code;
		$.getJSON(settings.infoUrl, ajaxData, callback);
	}
	function urlGetList(settings, code, callback) {
		var ajaxData = { };
		ajaxData[settings.listIdParamName] = code;
		$.getJSON(settings.listUrl, ajaxData, callback);
	}

	function codeGetLevel(codeSystem, code) {
		var level = 0, p = 0;
		while (true) {
			var len = codeSystem.fixLevel.levelLengths[level];
			var ff = '';
			for ( var i = 0; i < len; i++) {
				ff += codeSystem.fixLevel.levelFiller;
			}
			var ss = code.substr(p, len);
			if (ss.length == 0 || ss == ff)
				break;
			level++;
			if (level >= codeSystem.fixLevel.levelLengths.length)
				break;
		}
		;
		return level;
	}
	function codeGetLevelPart(codeSystem, code, level) {
		var p = 0;
		var i;
		level -= 1;
		for (i = 0; i < level; i++) {
			p += codeSystem.fixLevel.levelLengths[i];
		}
		return code.substr(p, codeSystem.fixLevel.levelLengths[i]);
	}
	function codeGetFullCode(codeSystem, codeParts) {
		var ret = '';
		for ( var level = 0; level < codeSystem.levelCount.length; level++) {
			if (level < codeParts.length) {
				ret += codeParts[level];
			} else {
				var len = codeSystem.fixLevel.levelLengths[level];
				var ff = '';
				for ( var i = 0; i < len; i++)
					ff += codeSystem.fixLevel.levelFiller;
				ret += ff;
			}
		}
		return ret;
	}

	$.fn.regionSelect = function(method) {
		// Method calling logic
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.regionSelect');
		}
	};

	$.fn.regionSelect.defaults = {
		infoUrl : '/ajax.php?act=region',
		listUrl : '/ajax.php?act=regionList',
		infoIdParamName : 'id',
		listIdParamName : 'id',
		currCodeFieldName : 'curr_code',
		fullCodeFieldName : 'full_code',
		regionNameFieldName : 'region_name',
		regionFullNameFieldName : 'region_full_name',
		levelFieldName : 'hiberarchy_level',
		
		containerTagName : 'span',
		
		parent : '',
		minLevel : 3,
		maxLevel : 3,

		check: {
			notEqualParent: false,
			minLevel: 0,
			maxLevel: 3
		},
		
		error : function(err) {
			alert(err);
		},

		codeSystem : {
			levelCount : {
				fix : true,
				length : 3
			},
			levelType : 'fix',
			fixLevel : {
				levelLengths : [ 2, 2, 2 ],
				levelFiller : '0'
			}
		}

	};
})(jQuery);