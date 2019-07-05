/**
 * 使用dataTables的options来调用ajax，通过renderCallback函数来显示数据
 * Created by xyf on 2015/3/10.
 */
(function ($) {
	var data_name = 'listOfDataTables_data';

	var defaults = {
		/**
		 * 显示数据的回调函数。格式：function(data, paginate, options){}。其中paginate为：{pageCount: 总页数, page: 当前页码, recordCount: 总记录数,recordBegin: 显示的第一条记录号, recordEnd: 显示的最后一条记录号+1}
		 */
		renderCallback: null,
		/**
		 * 每页记录数
		 */
		pageSize: 10,
		/**
		 * dataTables格式的数据说明
		 */
		dataTable: {
			bFilter: false,
			aaSortingFixed: null,
			aaSorting: [[0, 'asc']]
		},
		defaultRender: {
			bookBlock2Html: null,
			bookBlock3Html: null,
			bookButtonRenderCallback: null
		},
		defaultPages2: {
			pageBtnTemplate: '<button class="btn pager-btn"></button>',
			dividerTemplate: '<span style="font-size-adjust: 0.3;" class="glyphicon glyphicon-option-horizontal"></span>',
			pageShowCount: 3,
			edgeShowCount: 1,
			btnPrevClassName: 'pager-btn-prev',
			btnNextClassName: 'pager-btn-next',
			btnGroupClassName: 'pager-btn-group'
		}
	};
	var methods = {
		init: function (options) {
			return $(this).each(function (idx, ele) {
				var $ele = $(ele);
				var data = $.extend(true, {oInstance: $ele}, defaults, options), oSettings = data.dataTable;
				$ele.data(data_name, data);
				oSettings._iDisplayLength = data.pageSize;
				oSettings._iDisplayStart = 0;
				oSettings.oInstance = $ele;
				data.page = 1;
			});
		},
		bindDefaultPagesEventFunc: function ($prev, $next, enableDisableFunc) {
			var that = $(this);
			$.listOfDataTables.bindDefaultPagesEventFunc(that, $prev, $next, enableDisableFunc);
			return this;
		},
		bindDefaultPagesEventFunc2: function ($pager, renderPageButtonsFunc, enableDisableFunc) {
			var that = $(this);
			$.listOfDataTables.bindDefaultPagesEventFunc2(that, $pager, renderPageButtonsFunc, enableDisableFunc);
			return this;
		},
		nextPage: function () {
			return $(this).each(function (idx, ele) {
				var $ele = $(ele), data = $ele.data(data_name);
				var page = parseInt(data.page);
				if (page >= 1)
					page++;
				else
					page = 1;
				$ele.listOfDataTables('showPage', page);
			});
		},
		prevPage: function () {
			return $(this).each(function (idx, ele) {
				var $ele = $(ele), data = $ele.data(data_name);
				var page = parseInt(data.page);
				if (page > 1)
					page--;
				else
					page = 1;
				$ele.listOfDataTables('showPage', page);
			});
		},
		showPage: function (page) {
			return $(this).each(function (idx, ele) {
				var $ele = $(ele), data = $ele.data(data_name), oSettings = data.dataTable;
				page = parseInt(page);
				if (isNaN(page) || page == 0)
					page = 1;
				data.page = page;
				oSettings._iDisplayStart = (page - 1) * data.pageSize;
				var aoData = _fnAjaxParameters(oSettings);

				fnServerData.call(oSettings.oInstance, oSettings.sAjaxSource, aoData,
					function (json) {
						if (data.renderCallback) {
							//{pageCount: 总页数, page: 当前页码, recordCount: 总记录数,recordBegin: 显示的第一条记录号, recordEnd: 显示的最后一条记录号+1}
							var count = json.iTotalDisplayRecords;
							var pageSize = oSettings._iDisplayLength;
							var pages = {page: page, pageCount: Math.ceil(count / pageSize), recordCount: count};
							if (json.aaData.length) {
								pages.recordBegin = (page - 1) * pageSize;
								pages.recordEnd = pages.recordBegin + json.aaData.length;
							}
							try {
								data.renderCallback.call($ele, json.aaData, pages, data);
							} catch (e) {
								console.error(e);
							}
							$(oSettings.oInstance).trigger('pages.lodt', [pages]);
						}
					}, oSettings);
			});
		}
	};

	$.listOfDataTables = function (options) {
		var ele = document.createElement('div'), $ele = $(ele);
		//noinspection JSUnresolvedVariable
		if (options.debug || options.debugShow) {
			//noinspection JSUnresolvedVariable
			if (!options.debugShow)
				$ele.hide();
			$(document.body).append($ele);
		}
		return $ele.listOfDataTables(options);
	};
	$.listOfDataTables.data_name = data_name;
	$.listOfDataTables.defaultEnableDisableFunc = function ($ele1, isEnabled) {
		if (isEnabled) {
			$($ele1).removeAttr('disabled');
		} else {
			$($ele1).attr('disabled', 'disabled');
		}
	}

	$.listOfDataTables.defaultRenderPageButtonsFunc = function ($pager, pages) {
		var $ele = $(this), data = $ele.data(data_name);
		var pdata = pages.pdata, page = pages.page, pageCount = pages.pageCount;
		var btnTemplate = data.defaultPages2.pageBtnTemplate;
		var divider = data.defaultPages2.dividerTemplate;
		var $group = $pager.find('.' + data.defaultPages2.btnGroupClassName);
		$group.html('');
		$.each(pdata, function (idx, ele) {
			if (typeof ele == 'number') {
				var p = $(btnTemplate);
				if (ele > pageCount)
					p.attr('disabled', 'disabled');
				if (ele === page)
					p.addClass('active');
				else
					p.click(function () {
						$ele.listOfDataTables('showPage', ele);
					});
				p.text(ele).appendTo($group);
			} else if (ele === '') {
				$(divider).appendTo($group);
			} else {
				$(ele).appendTo($group);
			}
		});
	}

	/**
	 * 绑定监听pages事件的函数，只处理上一页、下一页按钮
	 * @param $ele
	 * @param $prev 上一页的按钮
	 * @param $next 下一页的按钮
	 * @param enableDisableFunc 设置enable、disable的函数。格式：function($ele, isEnabled){}
	 */
	$.listOfDataTables.bindDefaultPagesEventFunc = function ($ele, $prev, $next, enableDisableFunc) {
		$prev.unbind('click').bind('click', function () {
			$ele.listOfDataTables('prevPage');
		});
		$next.unbind('click').bind('click', function () {
			$ele.listOfDataTables('nextPage');
		});
		var func = enableDisableFunc || $.listOfDataTables.defaultEnableDisableFunc;
		var f = function (ev, pages) {
			func.call($ele, $prev, pages.page > 1);
			func.call($ele, $next, pages.page < pages.pageCount);
		};
		$($ele).unbind('pages.lodt').bind('pages.lodt', f);
		return f;
	};

	/**
	 * 绑定监听pages事件的函数，处理上一页(.pager-btn-prev)、下一页(.pager-btn-next)按钮和中间的页码
	 * @param $ele
	 * @param $pager 页码的包裹对象
	 * @param renderPageButtonsFunc 显示页码按钮的函数。格式：function($pager, pages){this为$ele}
	 * @param enableDisableFunc 设置enable、disable的函数。格式：function($ele, isEnabled){}
	 * @returns {Function}
	 */
	$.listOfDataTables.bindDefaultPagesEventFunc2 = function ($ele, $pager, renderPageButtonsFunc, enableDisableFunc) {
		var data = $ele.data(data_name);
		$pager.find('.' + data.defaultPages2.btnPrevClassName).unbind('click').bind('click', function () {
			$ele.listOfDataTables('prevPage');
		})
		$pager.find('.' + data.defaultPages2.btnNextClassName).unbind('click').bind('click', function () {
			$ele.listOfDataTables('nextPage');
		});
		var func = enableDisableFunc || $.listOfDataTables.defaultEnableDisableFunc;
		var renderFunc = renderPageButtonsFunc || $.listOfDataTables.defaultRenderPageButtonsFunc;
		var f = function (ev, pages) {
			pages.page = parseInt(pages.page) || 1;
			pages.pageCount = parseInt(pages.pageCount) || 0;
			func.call($ele, $pager.find('.pager-btn-prev'), pages.page > 1);
			func.call($ele, $pager.find('.pager-btn-next'), pages.page < pages.pageCount);
			$.each($ele, function (idx, ele) {
				var $ele1 = $(ele), data = $ele1.data(data_name),
					showCount = parseInt(data.defaultPages2.pageShowCount) || 3, halfCount = Math.floor((showCount - 1) / 2),
					edgeCount = parseInt(data.defaultPages2.edgeShowCount) || 1;

				var pdata = [];
				var i;
				if (pages.pageCount > 0) {
					if (pages.page <= edgeCount + 1 + halfCount) {
						for (i = 1; i <= pages.page; i++) pdata.push(i);
					} else {
						for (i = 1; i <= edgeCount; i++) pdata.push(i);
						pdata.push('');
						for (i = pages.page - halfCount; i <= pages.page; i++) pdata.push(i);
					}
					if (pages.pageCount <= pages.page + edgeCount + halfCount) {
						for (i = pages.page + 1; i <= pages.pageCount; i++) pdata.push(i);
					} else {
						for (i = pages.page + 1; i <= pages.page + halfCount; i++) pdata.push(i);
						pdata.push('');
						for (i = pages.pageCount - edgeCount + 1; i <= pages.pageCount; i++) pdata.push(i);
					}
				}
				pages.pdata = pdata;
				renderFunc.call($ele1, $pager, pages);
			});
		};
		$($ele).unbind('pages.lodt').bind('pages.lodt', f);
		return f;
	};

	$.fn.showStar = function () {
		showStar($(this));
		return this;
	}
	function showStar($ele) {
		$.each($ele, function (idx, ele) {
			var $ele1 = $(ele), data = parseFloat($ele.text());
			if ($ele1.data('score_shown'))
				return;
			if (isNaN(data)) data = 0;
			data = Math.round(data);
			if (data > 5) data = 5;
			$ele1.html('').data('score', data).data('score_shown', 1);
			for (var i = 1; i <= 5; i++) {
				$('<span class="iicon"></span>').addClass(i <= data ? 'star-on' : 'star-off').appendTo($ele1);
			}
		});
	}

	function showPrice($price, data, $price_origin) {
		var s0 = data.book_price_after_discount, p0 = parseFloat(s0);
		if (isNaN(p0))
			p0 = 0;
		var s1 = data.book_orginal_price, p1 = parseFloat(s1);
		if (isNaN(p1))
			p1 = 0;
		var s2 = data.book_price_before_discount, p2 = parseFloat(s2);
		if (isNaN(p2))
			p2 = 0;
		var s3, p3;
		if (p1 < p2) {
			p3 = p2;
			s3 = s2;
		} else {
			p3 = p1;
			s3 = s1;
		}
		if (p0 == 0) {
			$price.addClass('free').text(p2 == 0 ? '免费' : '限免');
		} else
			$price.text('￥' + s0);
		if ($price_origin) {
			if (p3 > p0)
				$price_origin.text(s3).show();
			else
				$price_origin.text(s3).hide();
		}
	}

	/**
	 * 配合defaultRender使用的columns
	 * @type {{sName: string, mDataProp: string}[]}
	 */
	$.listOfDataTables.defaultBookColumns = [
		{"sName": "book_create_date", mDataProp: 'book_create_date'},
		{"sName": "book_id", mDataProp: 'book_id'},
		{"sName": "book_name", mDataProp: 'book_name'},
		{"sName": "book_author", mDataProp: 'book_author'},
		{"sName": "book_desc", mDataProp: 'book_desc'},
		{"sName": "book_price", mDataProp: 'book_price'},
		{"sName": "book_orginal_price", mDataProp: 'book_orginal_price'},
		{"sName": "book_score", mDataProp: 'book_score'},
		{"sName": "is_virtual", mDataProp: 'is_virtual'},
		{"sName": "buy_count", mDataProp: 'buy_count'},
		{"sName": "click_count", mDataProp: 'click_count'},
		{"sName": "book_preview_id", mDataProp: 'book_preview_id'}
	];

	/**
	 * 根据html模版绘制book，并调用data.defaultRender.bookButtonRenderCallback来绘制按钮
	 * @param html html模版
	 * @param json 图书数据
	 * @param data listOfDataTables的配置参数
	 * @param pages 页数数据
	 * @returns {Object}
	 */
	$.listOfDataTables.defaultRenderBookBlock = function (html, json, data, pages) {
		var that = this;
		that.html('');
		var hasData = false;
		$.each(json, function (idx, ele) {
			var $o = $(html);
			that.append($o);
			hasData = true;
			$o.find('a.book-link').attr({href: 'bookShow.php?book_id=' + ele.book_id, title: ele.book_name});
			$o.find('img.book-cover').attr({
				src: '/cover.php?book_id=' + ele.book_id + '&type=small',
				alt: ele.book_name
			});
			$o.find('.book-name').text(ele.book_name);
			$o.find('.book-score').text(ele.book_score).showStar();
			if (ele.book_author)
				$o.find('.book-author').attr({alt: ele.book_author}).text(ele.book_author);
			var $book_desc = $o.find('.book-desc');
			$book_desc.addClass('text-long');
			var $bb = $('<div class="text-real"></div>');
			$bb.text(ele.book_desc).appendTo($book_desc);
			//setTimeout(function() {
			var h = parseFloat($book_desc.css('height'));
			if (h > 0 && $bb.height() > h) {
				$book_desc.attr('title', ele.book_desc);
				$book_desc.append('<div class="text-more">...</div>');
			}
			//}, 200);

			showPrice($o.find('.book-price'), ele, $o.find('.book-original-price'));
			try {
				if (data.defaultRender.bookButtonRenderCallback) {
					data.defaultRender.bookButtonRenderCallback.call(that, $o, ele);
				}
			} catch (e) {
				console.error(e);
			}
		});
		if(hasData) {
			$.css3check.fix(that);
		}else{
			that.text('没有数据');
		}
		return this;
	}

	/**
	 * 缺省的绘制图书按钮的函数
	 * @param $o book-buttons对象
	 * @param ele 图书数据
	 * @returns {Object}
	 */
	$.listOfDataTables.defaultRenderBookButton = function ($o, ele) {
		var defaultBookBtnHtml = '<button class="btn book-btn"><span class="webicon-yuntu"></span></button>';
		var that = $(this), data = that.data(data_name);
		var $b = $o.find('.book-buttons');
		if ($b.length) {
			var bookStatus;
			var f = function () {
				if (!bookStatus.isFree) {
					var $c = $(defaultBookBtnHtml).addClass('book-btn-cart').appendTo($b).click(getClickFuncCart(ele));
					$c.find('.webicon-yuntu').addClass('webicon-yuntu-cart');
					if (bookStatus.isInCart)
						$c.addClass('active');
				}
				$c = $(defaultBookBtnHtml).addClass('book-btn-heart').appendTo($b).click(getClickFuncFocus(ele));
				$c.find('.webicon-yuntu').addClass('webicon-yuntu-heart');
				if (bookStatus.hasFocus)
					$c.addClass('active');


				$c = $(defaultBookBtnHtml);
				if (bookStatus.canRead) {
					$c.addClass('book-btn-read').appendTo($b).text('阅读').click(getClickFuncRead(ele));
					$c.find('.webicon-yuntu').addClass('webicon-yuntu-read');
				} else if (bookStatus.hasPreview) {
					$c.addClass('book-btn-preview').appendTo($b).text('试读').click(getClickFuncPreview(ele));
					$c.find('.webicon-yuntu').addClass('webicon-yuntu-preview');
				}
				$.css3check.fix();
			};
			var promiseArr = [];

			var a = new $.Deferred();
			promiseArr.push(a.promise());
			// TODO: get canRead
			$.getJSON('/ajax.php?act=getBookStatusByUser', {book_id: ele.book_id}, function (data) {
				if (data.status) {
					bookStatus = data.data;
					a.resolve();
				} else {
					a.reject();
				}
			});

			if (promiseArr.length) {
				$.when.apply($, promiseArr).done(f);
			} else {
				f();
			}
		}
		return this;
	}

	var bookBlock2Html = '<li>\n\t<div><a target="_blank" class="book-link">\n\t\t<div><img class="book-cover"/></div>\n\t\t\t<div class="book-name"></div>\n\t</a>\n\t\t\t<div class="book-score"></div>\n\t\t\t<div class="book-author"></div>\n\t\t<div>\n\t\t\t<div class="pull-left book-price"></div>\n\t\t\t<div class="pull-right book-original-price"></div>\n\t\t</div>\n\t</div>\n</li>';
	var bookBlock3Html = '<li><a target="_blank" class="book-link">\n\t<div class="pull-left">\n\t\t<img class="book-cover"/>\n\t\t<div>\n\t\t\t<div class="pull-left book-price"></div>\n\t\t\t<div class="pull-right book-original-price"></div>\n\t\t</div>\n\t</div>\n\t<div class="pull-right">\n\t\t<div class="book-name"></div>\n\t\t<div class="book-score"></div>\n\t\t<div class="book-author"></div>\n\t\t<div class="book-desc"></div>\n\t\t<div class="book-buttons"></div>\n\t</div>\n</a></li>';

	/**
	 * 绘制较窄的book
	 * @param json
	 * @param data
	 * @param pages
	 * @returns {Object}
	 * @see $.listOfDataTables.defaultRenderBookBlock
	 */
	$.listOfDataTables.defaultRenderBookBlock2 = function (json, data, pages) {
		var $ele = $(this), data = $ele.data(data_name);
		Array.prototype.splice.call(arguments, 0, 0, data.defaultRender.bookBlock2Html || bookBlock2Html);
		return $.listOfDataTables.defaultRenderBookBlock.apply(this, arguments);
	}

	/**
	 * 绘制较宽的book
	 * @param json
	 * @param data
	 * @param pages
	 * @returns {Object}
	 * @see $.listOfDataTables.defaultRenderBookBlock
	 */
	$.listOfDataTables.defaultRenderBookBlock3 = function (json, data, pages) {
		var $ele = $(this), data = $ele.data(data_name);
		Array.prototype.splice.call(arguments, 0, 0, data.defaultRender.bookBlock3Html || bookBlock3Html);
		return $.listOfDataTables.defaultRenderBookBlock.apply(this, arguments);
	}

	$.fn.listOfDataTables = function (method) {
		// Method calling logic
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.listOfDataTables');
		}
	};

	function _fnColumnOrdering(oSettings) {
		var sNames = '';
		for (var i = 0, iLen = oSettings.aoColumns.length; i < iLen; i++) {
			sNames += oSettings.aoColumns[i].sName + ',';
		}
		if (sNames.length == iLen) {
			return "";
		}
		return sNames.slice(0, -1);
	}

	function _fnAjaxParameters(oSettings) {
		var iColumns = oSettings.aoColumns.length;
		var aoData = [], mDataProp, aaSort, aDataSort;
		var i, j;

		aoData.push({"name": "sEcho", "value": oSettings.iDraw});
		aoData.push({"name": "iColumns", "value": iColumns});
		aoData.push({"name": "sColumns", "value": _fnColumnOrdering(oSettings)});
		aoData.push({"name": "iDisplayStart", "value": oSettings._iDisplayStart});
		aoData.push({
			"name": "iDisplayLength", "value": oSettings.bPaginate !== false ?
				oSettings._iDisplayLength : -1
		});

		for (i = 0; i < iColumns; i++) {
			mDataProp = oSettings.aoColumns[i].mDataProp;
			aoData.push({"name": "mDataProp_" + i, "value": typeof(mDataProp) === "function" ? 'function' : mDataProp});
		}

		/* Filtering */
		try {
			if (oSettings.bFilter !== false) {
				if (oSettings.oPreviousSearch) {
					aoData.push({"name": "sSearch", "value": oSettings.oPreviousSearch.sSearch});
					aoData.push({"name": "bRegex", "value": oSettings.oPreviousSearch.bRegex});
				}
				for (i = 0; i < iColumns; i++) {
					if (oSettings.oPreviousSearch && oSettings.oPreviousSearch[i]) {
						aoData.push({"name": "sSearch_" + i, "value": oSettings.aoPreSearchCols[i].sSearch});
						aoData.push({"name": "bRegex_" + i, "value": oSettings.aoPreSearchCols[i].bRegex});
						aoData.push({"name": "bSearchable_" + i, "value": oSettings.aoColumns[i].bSearchable});
					}
				}
			}
		} catch (e) {
			console.error(e);
		}

		/* Sorting */
		if (oSettings.bSort !== false) {
			var iCounter = 0;

			aaSort = ( oSettings.aaSortingFixed !== null ) ?
				oSettings.aaSortingFixed.concat(oSettings.aaSorting) :
				oSettings.aaSorting.slice();

			for (i = 0; i < aaSort.length; i++) {
				aDataSort = oSettings.aoColumns[aaSort[i][0]].aDataSort;
				if (aDataSort && aDataSort.length) {
					for (j = 0; j < aDataSort.length; j++) {
						aoData.push({"name": "iSortCol_" + iCounter, "value": aDataSort[j]});
						aoData.push({"name": "sSortDir_" + iCounter, "value": aaSort[i][1]});
						iCounter++;
					}
				} else {
					aoData.push({"name": "iSortCol_" + iCounter, "value": aaSort[i][0]});
					aoData.push({"name": "sSortDir_" + iCounter, "value": aaSort[i][1]});
					iCounter++;
				}
			}
			aoData.push({"name": "iSortingCols", "value": iCounter});

			for (i = 0; i < iColumns; i++) {
				aoData.push({"name": "bSortable_" + i, "value": oSettings.aoColumns[i].bSortable !== false});
			}
		}

		return aoData;
	}

	function fnServerData(sUrl, aoData, fnCallback, oSettings) {
		oSettings.jqXHR = $.ajax({
			"url": sUrl,
			"data": aoData,
			"success": function (json) {
				$(oSettings.oInstance).trigger('xhr', oSettings);
				fnCallback(json);
			},
			"dataType": "json",
			"cache": false,
			"type": oSettings.sServerMethod,
			"error": function (xhr, error, thrown) {
				if (error == "parsererror") {
					console.log(error, thrown);
				}
			}
		});
	}
})(jQuery);