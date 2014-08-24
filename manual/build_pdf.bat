mkdir ..\..\asciidoctor-fopub\manual
copy ..\tools\tomcat\manual\*.png ..\..\asciidoctor-fopub\manual
copy ..\tools\jmeter\manual\*.png ..\..\asciidoctor-fopub\manual
copy manual\*.png ..\..\asciidoctor-fopub\manual
call asciidoctor -b docbook -d article manual.adoc
copy manual.xml ..\..\asciidoctor-fopub 
