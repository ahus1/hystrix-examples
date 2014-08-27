{% from "php5/map.jinja" import php5 with context %}


include:
  - php5

php5-mysql:
  pkg:
    - installed
    - name: {{ php5.mysql.pkg }}
