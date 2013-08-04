<%@include file="parts/head.jsp" %>

<body>
    <%@include file="parts/headerNotInLane.jsp" %>
    
    <div>
        <div class="pageMessage">
            Instance: <c:out value="${individual.toString()}"/>
        </div>
        
        <table class="table propertyAssignmentsTable">
        
	        <c:forEach items="${properties.keySet()}" var="property">
                <tr>
                    <td>
                        <c:out value="${property.getURI()}"/>
                    </td>
                    <td>
                        <c:out value="${properties.get(property)}"/>
                    </td>
                </tr>
	        </c:forEach>
        </table>
	</div>
</body>

<%@include file="parts/bottom.jsp" %>