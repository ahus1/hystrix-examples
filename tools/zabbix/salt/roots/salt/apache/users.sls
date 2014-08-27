{% from "apache/map.jinja" import apache with context %}


apache_user:
  user:
    - present
    - name: {{ salt['pillar.get']('apache:user', 'www-data') }}


apache_group:
  group:
    - present
    - name: {{ salt['pillar.get']('apache:group', 'www-data') }}
