$(function() {
		  var it = $ ("[show_title='showText']");
	    $.each (it, function (i, data)
	    {
		    $ (data).attr ('title', $ (data).text ());
	    });
	});