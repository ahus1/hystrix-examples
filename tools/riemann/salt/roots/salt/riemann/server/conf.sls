include:
  - riemann.server

{% set files_switch = salt['pillar.get']('riemann-server:files_switch', ['id']) %}

/etc/riemann/riemann.config:
  file:
    - managed
    - source:
      {% for grain in files_switch if salt['grains.get'](grain) is defined -%}
      - salt://riemann/files/{{ salt['grains.get'](grain) }}/etc/riemann/riemann.config
      {% endfor -%}
      - salt://riemann/files/default/etc/riemann/riemann.config
    - require:
      - pkg: riemann-server
    - watch_in:
      - service: riemann-server
