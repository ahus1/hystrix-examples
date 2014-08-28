mkdir ..\..\hystrix-pages\manual
copy ..\tools\tomcat\manual\*.png ..\..\hystrix-pages\manual
copy ..\tools\jmeter\manual\*.png ..\..\hystrix-pages\manual
copy manual\*.png ..\..\hystrix-pages\manual
asciidoctor manual.adoc -d book -D ..\..\hystrix-pages
