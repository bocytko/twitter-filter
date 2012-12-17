<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta name="layout" content="main"/>
<title>Twitter filter (Configuration)</title>
</head>
<body>
  <div class="body">
  <g:form controller="filter" action="updateConfiguration" method="post">
  <div><b>Configuration:</b></div>
    <table>
       <tr>
        <th>Name</th>
        <th>Value</th>
        <th>&nbsp;</th>
      </tr>
        <tr>
          <td>hashtags</td>
          <td><g:textArea name="hashTags" value="${hashTags}" rows="2" cols="100"/></td>
          <td><g:submitButton name="Update" value="Update" class="g-button red" /></td>
        </tr>
        <tr>
          <td>ignoredUsers</td>
          <td><g:textArea name="ignoredUsers" value="${ignoredUsers}" rows="5" cols="100"/></td>
          <td><g:submitButton name="Update" value="Update" class="g-button red" /></td>
        </tr>
    </table>
  </g:form>
  </div>
</body>
</html>
