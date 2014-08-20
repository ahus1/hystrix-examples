powershell set-executionpolicy remotesigned
rem this will disable deployment to localhost as there seems to be a bug
rem https://bugs.openjdk.java.net/browse/JDK-8025065?page=com.atlassian.jira.plugin.system.issuetabpanels:all-tabpanel
rem SET MAVEN_OPTS=-Djava.net.useSystemProxies=true
powershell %~dp0run-maven.ps1  %*