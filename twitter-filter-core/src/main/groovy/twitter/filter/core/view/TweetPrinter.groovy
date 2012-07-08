package twitter.filter.core.view

import java.util.regex.Matcher

import twitter.filter.core.Tweet

class TweetPrinter {
    void printPlainText(Tweet tweet) {
        println getPlainText(tweet)
    }

    def getPlainText(Tweet tweet) {
        "${tweet.from_user}: ${tweet.text}"
    }

    void printHtml(Tweet tweet) {
        println getHtml(tweet)
    }

    def getHtml(Tweet tweet) {
        def htmlText = convertUrlsToHtmlLinks(tweet)

        """
        <div class="tweet_row" style="margin: 10px; font-family: Verdana; font-size: 10pt">
            <div class="tweet_image" style="vertical-align: middle; display: inline; float: left; margin-right: 10px"><img width="48" height="48" src="${tweet.profile_image_url}"></div>
            <div class="tweet_info" style="font-weight: bold;"><a href="https://twitter.com/#!/${tweet.from_user}">${tweet.from_user}</a></div>
            <div class="tweet_text">${htmlText}</div>
            <div class="tweet_date" style="color: #888888; font-size: 8pt">${tweet.created_at}</div>
        </div>"""
    }

    def convertUrlsToHtmlLinks(Tweet tweet) {
        String html = tweet.text
        
        def urls = tweet.urls.clone().sort().unique()
        urls.each {
            Matcher matcher = html =~ "(${it})"
            html = matcher.replaceAll('<a href=\"$1\">$1</a>')
        }
        
        tweet.text = html
        
        html
    }
}
