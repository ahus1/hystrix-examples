{% from "mysql/map.jinja" import mysql with context %}


python-mysqldb:
  pkg:
    - installed
    - name: {{ mysql.python.pkg }}
    {% if mysql.python.version is defined %}
    - version: {{ mysql.python.version }}
    {% endif %}
