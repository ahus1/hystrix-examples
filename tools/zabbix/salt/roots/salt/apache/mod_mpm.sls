{% from "apache/map.jinja" import apache with context %}


include:
  - apache


{% set files_switch = salt['pillar.get']('apache:files_switch', ['id']) %}
{% set mpm_module = salt['pillar.get']('apache:mpm:module', 'mpm_worker') %}


{% if grains['os_family']=="Debian" %}
a2enmod {{ mpm_module }}:
  cmd:
    - run
    - unless: ls /etc/apache2/mods-enabled/{{ mpm_module }}.load
    - require:
      - pkg: apache
    - watch_in:
      - module: apache_mpm_restart
  file:
    - managed
    - name: /etc/apache2/mods-available/{{ mpm_module }}.conf
    - template: jinja
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://apache/files/{{ salt['grains.get'](grain) }}/etc/apache2/mods-available/{{ mpm_module }}.conf.jinja
      {% endfor -%}
      - salt://apache/files/default/etc/apache2/mods-available/{{ mpm_module }}.conf.jinja
    - require:
      - pkg: apache
    - watch_in:
      - module: apache_mpm_restart
# Deactivate the other mpm modules as a previous step
{% for mod in ['mpm_prefork', 'mpm_worker', 'mpm_event'] if not mod == mpm_module %}
a2dismod {{ mod }}:
  cmd:
    - run
    - onlyif: test -e /etc/apache2/mods-enabled/{{ mod }}.load
    - require:
      - pkg: apache
    - require_in:
      - cmd: a2enmod {{ mpm_module }}
    - watch_in:
      - module: apache_mpm_restart
{% endfor %}


# MPM change requires restart of apache
apache_mpm_restart:
  module:
    - wait
    - name: service.restart
    - m_name: apache2


{% endif %}
