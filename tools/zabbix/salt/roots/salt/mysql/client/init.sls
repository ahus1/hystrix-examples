{% from "mysql/map.jinja" import mysql with context %}


# This included  state is needed for the salt mysql modules to work, so we
# assume that we always want it
include:
  - mysql.python


mysql-client:
  pkg:
    - installed
    - name: {{ mysql.client.pkg }}
    {% if mysql.client.version is defined %}
    - version: {{ mysql.client.version }}
    {% endif %}
