{% from "php5/map.jinja" import php5 with context %}


# This configures the logrotate needed configurations for the log files. In
# order to be useful, you need logrotate installed and minimally configured.


{% set files_switch = salt['pillar.get']('php5-fpm:files_switch', ['id']) %}


php5-fpm_logrotate:
  file:
    - directory
    - name: /etc/logrotate.d


{% for pool in salt['pillar.get']('php5-fpm:pools', []) %}

  {% set pool_attr = salt['pillar.get']('php5-fpm:pools:' ~ pool) %}

  {% set logrotate_filename = 'php5-fpm-' ~ pool %}

  {% if pool_attr['logrotate_template'] is defined %}
    {% set template = pool_attr['logrotate_template'] %}
  {% else %}
    {% set template = 'minimal' %}
  {% endif %}

  {% if pool_attr['state'] is not defined or
        pool_attr['state'] == 'enabled' %}
/etc/logrotate.d/{{ logrotate_filename }}:
  file:
    - managed
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://php5/files/{{ salt['grains.get'](grain) }}/etc/logrotate.d/{{ template }}.jinja
      {% endfor -%}
      - salt://php5/files/default/etc/logrotate.d/{{ template }}.jinja
    - template: jinja
    - context:
        pool: {{ pool }}
    - require:
      - pkg: php5-fpm


  {% else %}
/etc/logrotate.d/{{ logrotate_filename }}:
  file:
    - absent
    - require:
      - pkg: php5-fpm


  {% endif %}
{% endfor %}
