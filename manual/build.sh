mkdir -p $HOME/pages/manual
copy ../tools/tomcat/manual/*.png $HOME/pages/manual
copy ../tools/jmeter/manual/*.png $HOME/pages/manual
copy ../tools/riemann/manual/*.png $HOME/pages/manual
copy ../tools/zabbix/manual/*.png $HOME/pages/manual
copy manual/*.png $HOME/pages/manual
asciidoctor manual.adoc -d book -D$HOME/pages
