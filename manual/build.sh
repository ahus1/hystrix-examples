mkdir -p $HOME/pages/manual
BASEDIR=$(dirname $0)
cd $BASEDIR
cp ../tools/tomcat/manual/*.png $HOME/pages/manual
cp ../tools/jmeter/manual/*.png $HOME/pages/manual
cp ../tools/riemann/manual/*.png $HOME/pages/manual
cp ../tools/zabbix/manual/*.png $HOME/pages/manual
cp manual/*.png $HOME/pages/manual
asciidoctor manual.adoc -d book -D $HOME/pages
