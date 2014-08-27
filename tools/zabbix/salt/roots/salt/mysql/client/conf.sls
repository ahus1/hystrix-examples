{% from "mysql/map.jinja" import mysql with context %}

include:
  - mysql.client
  - mysql.conf

extend:
  {{ mysql.config }}:
    file:
      - require:
        - pkg: mysql-client
