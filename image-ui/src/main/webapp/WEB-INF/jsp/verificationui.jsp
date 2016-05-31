<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:form="http://www.springframework.org/tags/form"
	xmlns:c="http://java.sun.com/jsp/jstl/core">
<head>
<meta charset="UTF-8" />
<meta http-equiv="refresh" content="1" />
<title>Image Verification</title>
</head>
<body>
	<div style="text-align: center">
		<c:if test="${empty imgModel}">
			<p>No more images to verify</p>
		</c:if>
		<c:if test="${not empty imgModel}">
			<form:form commandName="imgModel">
			    <br/> 
				<img src="${imgModel.imageUrl}"><br/>
				<form:hidden path="id" />
				<form:button name="yes" value="YES">YES</form:button>
				<form:button name="no" value="NO">NO</form:button><br/><hr/>
			</form:form>
		</c:if>
	</div>
</body>
</html>
