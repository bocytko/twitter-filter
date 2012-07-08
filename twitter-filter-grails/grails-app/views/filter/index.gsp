<%@ page contentType="text/html;charset=ISO-8859-1" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
<meta name="layout" content="main"/>
<title>Twitter filter</title>
</head>
<body>
  <div class="body">
  <div><b>${params.query}</b>: last ${tweets.size()} tweets:</div>
    <g:each in="${tweets}" var="tweet">
      <div class="tweet_row">
        <div class="tweet_image" style="vertical-align: middle; display: inline; float: left; margin-right: 10px"><img width="48" height="48" src="${tweet.profile_image_url}"></div>
        <div class="tweet_info" style="font-weight: bold;"><g:link url="https://twitter.com/#!/${tweet.from_user}">${tweet.from_user}</g:link></div>
        <div class="tweet_text">${tweet.text}</div>
        <div class="tweet_date" style="color: #888888; font-size: 8pt">${tweet.created_at}</div>
      </div>
    </g:each>
  </div>
</body>
</html>
