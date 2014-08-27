{% from "mysql/map.jinja" import mysql with context %}

include:
  - mysql.server
  - mysql.conf

extend:
  {{ mysql.config }}:
    file:
      - require:
        - pkg: mysql-server
      - watch_in:
        - service: mysql-server
