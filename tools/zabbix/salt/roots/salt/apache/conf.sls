{% from "apache/map.jinja" import apache with context %}


include:
  - apache


# This is a state file to configure apache. As there is a high variety of needs
# I just choose a way to organize the confs. This state file is prone to be
# forked to suit each one needs. Hopefully, as it is, should be enough for most
# needs.


{% set files_switch = salt['pillar.get']('apache:files_switch', ['id']) %}


{{ apache.config }}:
  file:
    - managed
    - template: jinja
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://apache/files/{{ salt['grains.get'](grain) }}/etc/apache2/apache2.conf.jinja
      {% endfor -%}
      - salt://apache/files/default/etc/apache2/apache2.conf.jinja
    - require:
      - pkg: apache
    - watch_in:
      - service: apache

{%- if salt['grains.get']('os_family') == 'Debian' %}
/etc/apache2/envvars:
  file:
    - managed
    - template: jinja
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://apache/files/{{ salt['grains.get'](grain) }}/etc/apache2/envvars.jinja
      {% endfor -%}
      - salt://apache/files/default/etc/apache2/envvars.jinja
    - require:
      - pkg: apache
    - watch_in:
      - service: apache
{% endif %}


{% for site in salt['pillar.get']('apache:sites', []) %}

  {% set site_attr = salt['pillar.get']('apache:sites:' ~ site) %}

  {% if site_attr['conf_filename'] is defined %}
    {% set conf_filename = site_attr['conf_filename'] %}
  {% else %}
    {% set conf_filename = site ~ '.conf' %}
  {% endif %}

  {% if site_attr['template'] is defined %}
    {% set template = site_attr['template'] %}
  {% else %}
    {% set template = 'minimal' %}
  {% endif %}


  {% if site_attr['state'] is not defined or
        site_attr['state'] == 'enabled' %}
/etc/apache2/sites-available/{{ conf_filename }}:
  file:
    - managed
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://apache/files/{{ salt['grains.get'](grain) }}/etc/apache2/sites-available/{{ template }}.jinja
      {% endfor -%}
      - salt://apache/files/default/etc/apache2/sites-available/{{ template }}.jinja
    - template: jinja
    - context:
        site: {{ site }}
    - require:
      - pkg: apache
    - watch_in:
      - service: apache


/etc/apache2/sites-enabled/{{ conf_filename }}:
  file:
    - symlink
    - target: /etc/apache2/sites-available/{{ conf_filename }}
    - require:
      - pkg: apache
    - watch_in:
      - service: apache


    {% if site_attr['create_dirs'] is defined and site_attr['create_dirs'] %}
      {% if site_attr['document_root'] is defined %}
{{ site_attr['document_root'] }}:
  file:
    - directory
    - makedirs: true
    - user: {{ site_attr['user'] | d('www-data') }}
    - group: {{ site_attr['group'] | d('www-data') }}
    - mode: 2755
    - require:
      - user: {{ site_attr['user'] | d('www-data') }}
      - group: {{ site_attr['group'] | d('www-data') }}
    - require_in:
      - service: apache
      {% endif %}


      {% if site_attr['log_dir'] is defined %}
{{ site_attr['log_dir'] }}:
  file:
    - directory
    - makedirs: true
    - user: {{ site_attr['user'] | d('www-data') }}
    - group: {{ site_attr['group'] | d('www-data') }}
    - mode: 775
    - require:
      - user: {{ site_attr['user'] | d('www-data') }}
      - group: {{ site_attr['group'] | d('www-data') }}
    - require_in:
      - service: apache
      {% endif %}
    {% endif %}


  {% elif site_attr['state'] == "disabled" %}
/etc/apache2/sites-enabled/{{ conf_filename }}:
  file:
    - absent
    - require:
      - pkg: apache
    - watch_in:
      - service: apache


  {% elif site_attr['state'] == 'absent' %}
/etc/apache2/sites-enabled/{{ conf_filename }}:
  file:
    - absent
    - require:
      - pkg: apache
    - watch_in:
      - service: apache


/etc/apache2/sites-available/{{ conf_filename }}:
  file:
    - absent
    - require:
      - pkg: apache
    - watch_in:
      - service: apache


  {% endif %}
{% endfor %}
