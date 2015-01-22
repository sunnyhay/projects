<%@ page language="java" contentType="text/html; charset=GB18030"
	pageEncoding="GB18030"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<script type="text/javascript" src="../jslib/jquery-1.7.2.js"></script>
<script type="text/javascript">


</script>
<title>邮件定制</title>
</head>
<body>
	<form name="mail" action="" method="post">
		邮件地址：<input type="text" name="subAdr">
		<br> 
		选择城市
		<input type="checkbox" name="subCheck" value="北京" checked="checked">
		<input type="checkbox" name="subCheck" value="泰山">
		<input type="checkbox" name="subCheck" value="兵马俑">
		<input type="checkbox" name="subCheck" value="海口" checked="checked">
		<input type="checkbox" name="subCheck" value="凤凰">
		<br>
		<input type="button" value="submit" onclick="javascript:document.mail.action='add';document.mail.submit();" />
	</form><br>
	
	
	
</body>
</html>