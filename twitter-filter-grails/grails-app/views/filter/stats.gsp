<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
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
      <g:each in="${allHashTags}" var="hashTag">
        <tr>
          <td>${hashTag}</td>
          <td>${stats[hashTag].numTweets}</td>
          <td>${stats[hashTag].numUrls}</td>
          <td><g:link class="g-button red" controller="filter" action="clear" params="[hashTag: hashTag]">Clear</g:link></td>
        </tr>
     </g:each>
    </table>
  </div>
</body>
</html>
