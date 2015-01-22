<%@ page language="java" contentType="text/html; charset=GB18030"
	pageEncoding="GB18030"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>admin Add</title>
</head>
<body>
	<form name="commonlist" action="" method="post">
		加入新的后台管理项：<input type="text" name="item"><br> 
		<input type="button" value="submit" onclick="javascript:document.commonlist.action='add';document.commonlist.submit();" />
	</form><br>
</body>
</html>