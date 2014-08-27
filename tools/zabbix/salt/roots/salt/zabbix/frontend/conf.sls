{% from "zabbix/map.jinja" import zabbix with context %}


include:
  - zabbix.frontend
  - zabbix.frontend.repo


{% set files_switch = salt['pillar.get']('zabbix-frontend:files_switch', ['id']) %}


{{ zabbix.frontend.config }}:
  file:
    - managed
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://zabbix/files/{{ salt['grains.get'](grain) }}/etc/zabbix/web/zabbix.conf.php.jinja
      {% endfor -%}
      - salt://zabbix/files/default/etc/zabbix/web/zabbix.conf.php.jinja
    - template: jinja
    - require:
      - pkg: zabbix-web-mysql

/etc/php.d/zabbix.ini:
  file:
    - managed
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://zabbix/files/{{ salt['grains.get'](grain) }}/etc/php.d/zabbix.ini
      {% endfor -%}
      - salt://zabbix/files/default/etc/php.d/zabbix.ini
    - watch_in:
      - service: apache
      - service: php5-fpm
    - require:
      - pkg: php5-fpm
      - pkg: apache
