{% from "apache/map.jinja" import apache with context %}


include:
  - apache


{% if grains['os_family']=="Debian" %}
a2enmod proxy:
  cmd:
    - run
    - unless: ls /etc/apache2/mods-enabled/proxy.load
    - require:
      - pkg: apache
    - watch_in:
      - service: apache
{% endif %}
