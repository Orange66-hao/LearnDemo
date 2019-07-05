/**
 * 将数值四舍五入(保留2位小数)后格式化成金额形式
 *
 * @param num 数值(Number或者String)
 * @param precision 保留几位小数
 * @return string 金额格式的字符串,如'1,234,567.45'
 */
function formatCurrency(num, precision) {
	if (isNaN(precision)) {
		precision = 2;
	}
	var a = Math.pow(10, precision);
	num = num.toString().replace(/\$|,/g, '');
	if (isNaN(num))
		num = 0;
	var sign = num >= 0;
	num = Math.floor(num * a + 0.5);
	var cents = num % a;
	num = Math.floor(num / a).toString();
	if (precision == 0) {
		cents = '';
	} else {
		if (cents < 10)
			cents = "0" + cents;
		cents = '.' + cents
	}
	for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
		num = num.substring(0, num.length - (4 * i + 3)) + ',' +
		num.substring(num.length - (4 * i + 3));
	return (((sign) ? '' : '-') + num + cents);
}


/**
 * 将数值四舍五入(保留1位小数)后格式化成金额形式
 *
 * @param num 数值(Number或者String)
 * @return string 金额格式的字符串,如'1,234,567.4'
 */
function formatCurrencyTenThou(num) {
	num = num.toString().replace(/\$|,/g, '');
	if (isNaN(num))
		num = "0";
	var sign = num >= 0;
	num = Math.floor(num * 10 + 0.50000000001);
	var cents = num % 10;
	num = Math.floor(num / 10).toString();
	for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
		num = num.substring(0, num.length - (4 * i + 3)) + ',' +
		num.substring(num.length - (4 * i + 3));
	return (((sign) ? '' : '-') + num + '.' + cents);
}
