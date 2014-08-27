{% from "apache/map.jinja" import apache with context %}


apache:
  pkg:
    - installed
    - name: {{ apache.pkg }}
    {% if apache.version is defined %}
    - version: {{ apache.version }}
    {% endif %}
  service:
    - running
    - name: {{ apache.service }}
    - enable: True
    - reload: True
    - require:
      - pkg: apache
