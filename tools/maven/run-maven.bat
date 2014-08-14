powershell set-executionpolicy remotesigned
powershell %~dp0run-maven.ps1 '-Djava.net.useSystemProxies=true' %*