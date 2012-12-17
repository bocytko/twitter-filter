<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title><g:layoutTitle default="Grails"/></title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
        <link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
        <link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'google-buttons.css')}" type="text/css" media="screen" />
        <g:javascript library="jquery" />
        <g:layoutHead/>
        <r:layoutResources />
        
        <g:javascript>
        var interval = 0;
        hideProgress();
        
        function fetchProgress() {
            ${remoteFunction(method: 'GET', controller: 'filter', action: 'progress', params: [hashTag: params.hashTag], onSuccess: 'updateProgress(data,textStatus);')};
        }
        
        function updateProgress(data, textStatus) {
            ${"progress"}.innerHTML = data;
        }
        
        function showProgress() {
            interval = setInterval('fetchProgress()', 4000);
            $("#progress").show();
        }
        
        function hideProgress() {
            clearInterval(interval);
            $("#progress").hide();
        }
        </g:javascript>
    </head>
    <body>
        <div id="grailsLogo" style="text-align: center">
          <div>
	          <g:each in="${allHashTags}" var="tag">
	              <g:link class="g-button blue" controller="filter" action="index" params="[hashTag: tag]">${tag}</g:link>
	          </g:each>
	          <g:each in="${allHashTags}" var="tag">
	              <g:remoteLink class="g-button red" controller="filter" action="filter" params="[hashTag: tag]" update="fetch-result" onLoading="showProgress()" onSuccess="hideProgress()">${tag} <i class="icon-refresh icon-white"></i></g:remoteLink>
	          </g:each>
          </div>
          
          <span id="progress" style="font-size:10px;color:#FFF;padding:0 0 0 1em"></span>
          <span id="fetch-result" style="font-size:10px;color:#FFF;padding:0 0 0 1em"></span>
        </div>
        
        <div id="layout" style="padding: 5pt">
            <g:layoutBody/>
        </div>
        
        <div class="footer" role="contentinfo">
            <g:link class="g-button" controller="filter" action="stats">Datastore stats</g:link>
            <g:link class="g-button" controller="filter" action="config">Configuration</g:link>
        </div>
        
        <div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
        
        <g:javascript library="application"/>
        <r:layoutResources />
    </body>
</html>
