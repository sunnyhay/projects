<%@ page language="java" contentType="text/html; charset=GB18030"
	pageEncoding="GB18030"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<script type="text/javascript" src="../jslib/jquery-1.7.2.js"></script>
<script type="text/javascript">


</script>
<title>�ʼ�����</title>
</head>
<body>
	<form name="mail" action="" method="post">
		�ʼ���ַ��<input type="text" name="subAdr">
		<br> 
		ѡ�����
		<input type="checkbox" name="subCheck" value="����" checked="checked">
		<input type="checkbox" name="subCheck" value="̩ɽ">
		<input type="checkbox" name="subCheck" value="����ٸ">
		<input type="checkbox" name="subCheck" value="����" checked="checked">
		<input type="checkbox" name="subCheck" value="���">
		<br>
		<input type="button" value="submit" onclick="javascript:document.mail.action='add';document.mail.submit();" />
	</form><br>
	
	
	
</body>
</html>