<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript">

</script>
</head>
<body>
<div>

	<input type= "button" value="main" onclick="location.href='calendar.do'">
	
	<h1>::: 일정리스트 :::</h1>
	<table>
		<c:forEach var="dateList" items="${dateList}" varStatus="date_status">
			<input type="hidden" name="schedule_idx" value="${dateList.schedule_idx}">
				<tr>
					<td>${dateList.schedule_idx}</td>
					<td>${dateList.schedule_num}</td>
					<td>${dateList.schedule_subject}</td>
					<td>${dateList.schedule_desc}</td>
					<td>${dateList.schedule_date}</td>
					<td>${dateList.schedule_share}</td>
				</tr>
		</c:forEach>
	</table>
</div>
</body>
</html>