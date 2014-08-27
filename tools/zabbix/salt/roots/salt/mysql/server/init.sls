{% from "mysql/map.jinja" import mysql with context %}


# This included  state is needed for the salt mysql modules to work, so we
# assume that we always want it
include:
  - mysql.python


mysql-server:
  pkg:
    - installed
    - name: {{ mysql.server.pkg }}
    {% if mysql.server.version is defined %}
    - version: {{ mysql.server.version }}
    {% endif %}
  service:
    - running
    - name: {{ mysql.server.service }}
    - enable: True
    - require:
      - pkg: mysql-server
