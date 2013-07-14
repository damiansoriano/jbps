<%@include file="parts/head.jsp" %>

<body>
    <%@include file="parts/headerHome.jsp" %>
    <div>
        <div id="welcomeMessage">
            Welcome to the Java Business Process Simulator, choose one of the following
            applications in order execute.
        </div>
        
        <div id="homeLanes">
	        <ul>
	        <c:forEach items="${lanes}" var="lane">
		        <li>
		            <a href="/${lane}/startSimulation">
		                <c:out value="${lanesDescriptions.get(lane)}"/>
		            </a>
		        </li>
	        </c:forEach>
	        </ul>
        </div>
    </div>
</body>
<%@include file="parts/bottom.jsp" %>