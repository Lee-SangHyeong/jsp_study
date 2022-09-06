<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<%
pageContext.setAttribute("result", "hello");
%>
<body>
	<%=request.getAttribute("result") %>입니다.<br>
	${result}<br>
	${requestScope.result}<br>
	${names[0]}<br>
	${names[1]}<br>
	${names[2]}<br>
	${notice.id}<br>
	${notice.title}<br>
	${result}<br>
	${param.num}<br>
	${header.accept}<br>
	<br>
	EL의 연산자-------------------------<br>
	${param.num}이라는 숫자는<br>
	5보다 큰가? ${param.num > 5}<br>
	3 ge? ${param.num ge 3}<br>
	empty 인가? ${empty param.num}<br>
	not empty 인가? ${not empty param.num}<br>
	${empty param.num?'값이 비어 있습니다.':param.num}<br>
	2로나누기:${param.num/2}<br>
</body>
</html>