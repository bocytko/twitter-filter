<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta name="layout" content="main"/>
<title>Twitter filter</title>
</head>
<body>
  <div class="body">
  <div><b>${hashTag}</b>: last ${tweets.size()} tweets:</div>
    <g:each in="${tweets}" var="tweet">
      <div class="tweet_row">
        <div class="tweet_image" style="vertical-align: middle; display: inline; float: left; margin-right: 10px"><img width="48" height="48" src="${tweet.getProfileImageUrl()}"></div>
        <div class="tweet_info" style="font-weight: bold;"><g:link url="https://twitter.com/#!/${tweet.getUser()}">${tweet.getUser()}</g:link> (${tweet.getRelatedTweets()})</div>
        <div class="tweet_text">${tweet.getText()}</div>
        <div class="tweet_date" style="color: #888888; font-size: 8pt">${tweet.getCreatedAt()}</div>
      </div>
    </g:each>
  </div>
</body>
</html>
