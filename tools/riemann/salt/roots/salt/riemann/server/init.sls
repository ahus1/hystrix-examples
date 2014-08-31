daemonize:
  pkg.installed

java:
  pkg.installed:
    - pkgs: 
      - java-1.7.0-openjdk-devel  
  
riemann-server:
  pkg.installed:
    - sources:
      - riemann: http://aphyr.com/riemann/riemann-0.2.6-1.noarch.rpm
    - require:
      - pkg: daemonize
      - pkg: java
  service:
    - name: riemann
    - running
    - enable: True
    - require:
      - pkg: riemann-server
