<%@ page contentType="text/html;charset=ISO-8859-1" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="layout" content="main"/>
<title>Twitter filter</title>
</head>
<body>
  <div class="body">
  <div><b>Datastore statistics:</b></div>
    <table>
       <tr>
        <th>Hashtag</th>
        <th>Tweets</th>
        <th>Cached URLs</th>
        <th>&nbsp;</th>
      </tr>
      <g:each in="${queries}" var="query">
        <tr>
          <td>${query}</td>
          <td>${stats[query].numTweets}</td>
          <td>${stats[query].numUrls}</td>
          <td><g:link class="g-button red" controller="filter" action="clear" params="[query: query]">Clear</g:link></td>
        </tr>
     </g:each>
    </table>
  </div>
</body>
</html>
