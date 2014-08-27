{% from "apache/map.jinja" import apache with context %}


# This configures the logrotate needed configurations for the log files. In
# order to be useful, you need logrotate installed and minimally configured.


{% set files_switch = salt['pillar.get']('apache:files_switch', ['id']) %}


apache_logrotate:
  file:
    - directory
    - name: /etc/logrotate.d


{% for site in salt['pillar.get']('apache:sites', []) %}

  {% set site_attr = salt['pillar.get']('apache:sites:' ~ site) %}

  {% set logrotate_filename = 'apache2-' ~ site %}

  {% if site_attr['logrotate_template'] is defined %}
    {% set template = site_attr['logrotate_template'] %}
  {% else %}
    {% set template = 'minimal' %}
  {% endif %}

  {% if site_attr['state'] is not defined or
        site_attr['state'] == 'enabled' %}
/etc/logrotate.d/{{ logrotate_filename }}:
  file:
    - managed
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://apache/files/{{ salt['grains.get'](grain) }}/etc/logrotate.d/{{ template }}.jinja
      {% endfor -%}
      - salt://apache/files/default/etc/logrotate.d/{{ template }}.jinja
    - template: jinja
    - context:
        site: {{ site }}
    - require:
      - pkg: apache


  {% else %}
/etc/logrotate.d/{{ logrotate_filename }}:
  file:
    - absent
    - require:
      - pkg: apache


  {% endif %}
{% endfor %}
