<%@include file="parts/head.jsp" %>

<body>
    <%@include file="parts/headerNotInLane.jsp" %>
    
    <div>
        <div class="pageMessage">
            This is a list of all instances in the model with the corresponding direct classes.
        </div>
        
        <div class="pageListing">
	        <ul>
			    <c:forEach items="${individuals}" var="individual">
			        
                    <c:url value="/instance/" var="url">
                        <c:param name="individualURI" value="${individual.getURI()}" />
                    </c:url>
			        
			        <li>
			            <a href="${url}"><c:out value="${individual.toString()}"/></a>
			        </li>
		            <ul>
			            <c:forEach items="${classesByIndividuals.get(individual)}" var="individualClass">
			                <li>
		                        <a href="/class/${individualClass.getURI()}"><c:out value="${individualClass.toString()}"/></a>
		                    </li>
			            </c:forEach>
			        </ul>
			    </c:forEach>
		    </ul>
	    </div>
	</div>
</body>

<%@include file="parts/bottom.jsp" %>