<%@ page language="java" contentType="text/html; charset=GB18030"
	pageEncoding="GB18030"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>admin Comment</title>
</head>
<body>
	<form name="commonlist" action="" method="post">
		用户对于后台管理的评论：<input type="text" name="comment"><br> 
		<input type="button" value="submit" onclick="javascript:document.commonlist.action='comment';document.commonlist.submit();" />
	</form><br>
</body>
</html>