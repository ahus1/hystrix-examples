{% from "php5/map.jinja" import php5 with context %}


include:
  - php5.fpm


{% set files_switch = salt['pillar.get']('php5-fpm:files_switch', ['id']) %}



{% for pool in salt['pillar.get']('php5-fpm:pools', []) %}

  {% set pool_attr = salt['pillar.get']('php5-fpm:pools:' ~ pool) %}

 {% if pool_attr['conf_filename'] is defined %}
    {% set conf_filename = pool_attr['conf_filename'] %}
  {% else %}
    {% set conf_filename = pool ~ '.conf' %}
  {% endif %}

  {% if pool_attr['template'] is defined %}
    {% set template = pool_attr['template'] %}
  {% else %}
    {% set template = 'minimal' %}
  {% endif %}

  {% if pool_attr['state'] is not defined or
        pool_attr['state'] == 'enabled' %}
/etc/php5/fpm/pool.d/{{ pool }}.conf:
  file:
    - managed
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://php5/files/{{ salt['grains.get'](grain) }}/etc/php5/fpm/pool.d/{{ template }}.jinja
      {% endfor -%}
      - salt://php5/files/default/etc/php5/fpm/pool.d/{{ template }}.jinja
    - template: jinja
    - context:
        pool: {{ pool }}
    - require:
      - pkg: php5-fpm
    - watch_in:
      - service: php5-fpm


    {% if pool_attr['create_dirs'] is defined and pool_attr['create_dirs'] %}
      {% if pool_attr['log_dir'] is defined %}
{{ pool_attr['log_dir'] }}:
  file:
    - directory
    - makedirs: true
    - user: {{ pool_attr['user'] | d('www-data') }}
    - group: {{ pool_attr['group'] | d('www-data') }}
    - mode: 775
    - require:
      - user: {{ pool_attr['user'] | d('www-data') }}
      - group: {{ pool_attr['group'] | d('www-data') }}
    - require_in:
      - service: php5-fpm
      {% endif %}
    {% endif %}

  {% elif pool_attr['state'] == 'absent' %}
/etc/php5/fpm/pool.d/{{ pool }}.conf:
  file:
    - absent
    - require:
      - pkg: php5-fpm
    - watch_in:
      - service: php5-fpm
  {% endif %}
{% endfor %}
