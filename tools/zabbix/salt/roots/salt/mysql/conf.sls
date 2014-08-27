{% from "mysql/map.jinja" import mysql with context %}

{% set files_switch = salt['pillar.get']('mysql:files_switch', ['id']) %}
{% set cnf_template = salt['pillar.get']('mysql:cnf_template', 'only_pillar') %}

{{ mysql.config }}:
  file:
    - managed
    - template: jinja
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://mysql/files/{{ salt['grains.get'](grain) }}/etc/mysql/{{ cnf_template }}.cnf.jinja
      {% endfor -%}
      - salt://mysql/files/default/etc/mysql/{{ cnf_template }}.cnf.jinja
