<%@include file="parts/head.jsp" %>

<body>
    <%@include file="parts/header.jsp" %>
    
    <div>
        <h4 class="currentStateDescription">
            ${currentState.toString()}
        </h4>
	
	   <form action="makeTransition" method="post">
           <c:forEach items="${actions}" var="action">
               <div class="actionTypeDescription">
                   Action Type: <c:out value="${action.actionType}"/>
               </div>
               
               <div class="classDescription">
                   Class: <c:out value="${action.getJbpsClass().toString()}"/>
               </div>
               
               <c:if test="${errorMessage != null}">
               <div class="errorMessage">
                   An Error has ocurr while executing the actions,
                   try a different assignment of the properties.<br/>
                   Error Message:
                   <c:out value="${errorMessage}"/>
               </div>
               </c:if>
               
               <table class="table propertyAssignmentsTable">
                   
                   <tr><th colspan="2">Select values for different fields</th></tr>
                   
                   <c:forEach items="${action.propertyAssignments}" var="propertyAssignment">
                       <tr>
                           <td>
                               <c:out value="${propertyAssignment.getJbpsProperty().toString()}"/></div>
                           </td>
                           <td>
                            <select class="propertyAssignmentSelect" name="${propertyAssignment.getPropertyURI()}">
	                            <option value=""></option>
	                            <c:forEach items="${propertyAssignment.possibleAssignments}" var="possibleAssignment">
	                                
	                                <c:choose>
	                                   <c:when test="${possibleAssignment.getURI().equals(propertyAssignment.getPropertyValue())}">
	                                       <option selected value="<c:out value="${possibleAssignment.getURI()}"/>">
                                               <c:out value="${possibleAssignment.toString()}"/>
                                           </option>
	                                   </c:when>
	                                   <c:otherwise>
	                                       <option value="<c:out value="${possibleAssignment.getURI()}"/>">
	                                           <c:out value="${possibleAssignment.toString()}"/>
	                                       </option>
	                                   </c:otherwise>
	                                </c:choose>
	                            </c:forEach>
                            </select>
                           </td>
			       </tr>
			   </c:forEach>
		   </table>
		</c:forEach>
		
		<table class="table propertyAssignmentsTable">
		<tr><th colspan="2">Select which transition you want to take</th></tr>
		
		<tr>
               <td>Transition</td>
			<td>
			<select class="propertyAssignmentSelect" name="transition">
			    <c:forEach items="${transitions.keySet()}" var="transition">
	                <option value="<c:out value="${transition.getTransitionURI()}"/>">
	                    <c:out value="${transition.toString()}"/>
			        </option>
	            </c:forEach>
	        </select>
	        </td>
        </tr>
        </table>
        <input type="submit" value="Submit"/>
    </form>
	
	<script src="/static/js/jquery.js"></script>
	<script src="/static/bootstrap/js/bootstrap.min.js"></script>
</div>
</body>
<%@include file="parts/bottom.jsp" %>