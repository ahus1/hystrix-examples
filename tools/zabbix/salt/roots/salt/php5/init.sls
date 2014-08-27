{% from "php5/map.jinja" import php5 with context %}


php5:
  pkg:
    - installed
    - name: {{ php5.pkg }}
    {% if php5.version is defined %}
    - version: {{ php5.version }}
    {% endif %}
