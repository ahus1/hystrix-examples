mkdir ..\..\hystrix-pages\manual
copy ..\tools\tomcat\manual\*.png ..\..\hystrix-pages\manual
copy ..\tools\jmeter\manual\*.png ..\..\hystrix-pages\manual
asciidoctor manual.adoc -D ..\..\hystrix-pages
