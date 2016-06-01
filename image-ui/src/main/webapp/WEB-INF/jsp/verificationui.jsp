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

<link href='https://fonts.googleapis.com/css?family=Titillium+Web' rel='stylesheet' type='text/css'>
<style>

	html, body {
		margin:0; padding:0;
		overflow: hidden;
	}


	#header {
		width:100%;
		height:120px;
		background-color:black;
		padding-left:40px;
	}

	#header h1 {
		margin:0; padding:0;
		line-height: 60px;
		color:white;
		font-family: 'Titillium Web', sans-serif;

	}

	#header h2 {
		margin:0; padding:0;
		line-height: 20px;
		font-size:16px;
		color:white;
		font-family: 'Titillium Web', sans-serif;
	}

</style>

<body>


<div id="header">


	<h1>GPIG Group 2</h1><h2>Image Verification Display</h2>



</div>


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
