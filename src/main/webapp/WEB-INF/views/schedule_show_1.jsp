<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="ko">
<head>
<script src="//code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<!-- jquery datepicker -->
<!-- <link rel="stylesheet" href="http://code.jquery.com/ui/1.8.18/themes/base/jquery-ui.css" type="text/css" />
<script src="http://code.jquery.com/ui/1.8.18/jquery-ui.min.js"></script> -->
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.8.18/themes/base/jquery-ui.css" type="text/css" />
<!-- jquery datepicker 끝 -->

<title>FrmPopup.htm : 팝업창</title>
</head>
<body>
	
	<h1>::: 일정리스트 :::</h1>
	
				
	<table border="0" align="center">
		<caption>::: 일정 :::</caption>
		<c:forEach var="vo" items="${schedule_show}">
			<input type="hidden" name="schedule_idx" value="${vo.schedule_idx}">
			<tr>
				<th>제목<th>
				<td>${vo.schedule_idx}</td>
			</tr>
			<tr>
				<th>작성자<th>
				<td>${user.schedule_subject}</td>
			</tr>
			<tr>
				<th>내용<th>
				<td>${vo.schedule_desc}</td>
			</tr>
		</c:forEach>
	</table>
	

</body>
</html>