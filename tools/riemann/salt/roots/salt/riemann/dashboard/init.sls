riemann-dash:
  gem.installed:
    - user: rvm
    - require:
      - gem: riemann-client
      - gem: riemann-tools
      - gem: thin
  service:
    - running
    - enable: True
    - require:
      - file: /etc/init.d/riemann-dash

/etc/init.d/riemann-dash:
  file:
    - managed
    - mode: 755
    - source:
      - salt://riemann/files/default/etc/init.d/riemann-dash
    - require:
      - file: /home/rvm
      - gem: riemann-dash

/home/rvm:
  file.recurse:
    - source: salt://riemann/files/default/home/rvm
    - file_mode: 755

gcc-c++:
  pkg.installed

thin:
  gem.installed:
    - user: rvm
    - require:
      - pkg: gcc-c++

riemann-client:
  gem.installed:
    - user: rvm

riemann-tools:
  gem.installed:
    - user: rvm

