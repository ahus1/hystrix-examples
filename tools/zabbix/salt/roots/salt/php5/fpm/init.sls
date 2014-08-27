{% from "php5/map.jinja" import php5 with context %}


php5-fpm:
  pkg:
    - installed
    - name: {{ php5.fpm.pkg }}
    {% if php5.fpm.version is defined %}
    - version: {{ php5.fpm.version }}
    {% endif %}
  service:
    - running
    - name: {{ php5.fpm.service }}
    - enable: True
    - require:
      - pkg: php5-fpm
