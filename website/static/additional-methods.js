function byteLength(s) {
　　　　var totalLength = 0;
　　　　var i;
　　　　var charCode;
　　　　for (i = 0; i < s.length; i++) {
　　　　　charCode = s.charCodeAt(i);
　　　　　if (charCode < 0x007f) {
　　　　　　totalLength = totalLength + 1;
　　　　　} else if ((0x0080 <= charCode) && (charCode <= 0x07ff)) {
　　　　　　totalLength += 2;
　　　　　} else if ((0x0800 <= charCode) && (charCode <= 0xffff)) {
　　　　　　totalLength += 3;
　　　　　}
　　　　}
　　　　return totalLength;
　　　}

jQuery.validator.addMethod("byteRangeLength", function(value, element, param) {   
    var length = byteLength(value);
    return this.optional(element) || length <= param;
}, "字符串过长");


jQuery.validator.addMethod("noSpace", function(value, element) {   
    var tel = / /;
    return this.optional(element) || !(tel.test(value));
}, "不能含有空格");