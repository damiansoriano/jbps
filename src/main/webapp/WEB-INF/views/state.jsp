<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Insert title here</title>
		
		<link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
		<link href="/static/css/style.css" rel="stylesheet" media="screen">
	</head>

    <body>
    <div>
		Current state: ${currentStateURI}
		
		<br/>
		<form action="makeTransition" method="post">
			<c:forEach items="${actions}" var="action">
			   
			   Action Type: <c:out value="${action.actionType}"/><br/>
		       Class URI: <c:out value="${action.classURI}"/><br/>
			   
			   <c:forEach items="${action.propertyAssignments}" var="propertyAssignment">
			       <div>Choose value for property <c:out value="${propertyAssignment.getPropertyURI()}"/>: </div>
			       
			       <select name="${propertyAssignment.getPropertyURI()}">
			       <option value=""></option>
			       <c:forEach items="${propertyAssignment.possibleAssignments}" var="possibleAssignment">
			           <option value="<c:out value="${possibleAssignment.getURI()}"/>">
			               <c:out value="${possibleAssignment.getLabel(null)}"/>
		               </option>
			       </c:forEach>
			       </select>
			       
			   </c:forEach>
			</c:forEach>
			
			<div>Transitions</div>
			
			<select name="transition">
			    <c:forEach items="${transitions.keySet()}" var="transition">
	                <option value="<c:out value="${transition.getTransitionURI()}"/>">
	                    <c:out value="${transition.getTransitionURI()}"/>
			        </option>
	            </c:forEach>
	        </select>
	        
	        <input type="submit" value="Submit"/>
	    </form>
		
		<script src="/static/js/jquery.js"></script>
		<script src="/static/bootstrap/js/bootstrap.min.js"></script>
	</div>
	</body>
</html>