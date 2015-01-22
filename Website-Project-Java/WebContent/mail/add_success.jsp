<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>邮件定制成功</title>
<script type="text/javascript" src="../jslib/jquery-1.7.2.js"></script>
<script type="text/javascript">
$(function(){
  var count = 5;
  countdown = setInterval(function(){
    $("#countdown").html(count);
    if (count == 0) {
      window.location = '../index.html';
    }
    count--;
  }, 1000);
});
</script>
</head>
<body>
	<p>您的邮件定制已经完成，5秒后会自动跳转回主页</p>
	<p>倒计时：<label id="countdown"></label>秒后自动跳转</p>
	<p>如果5秒后没有跳转，请点击<b><a href="../index.html">该链接</a></b>回到主页</p>
</body>
</html>