{% from "php5/map.jinja" import php5 with context %}


include:
  - php5


# This adds a conveniently updated repo for PHP5
{%if salt['grains.get']('os_family') == 'Debian' %}
php5_repo:
  pkgrepo:
    - managed
    - ppa: ondrej/php5
    - require:
      - cmd: php5_repo
  cmd:
    - run
    - name: /usr/bin/apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E5267A6C
    - unless: /usr/bin/apt-key adv --list-key E5267A6C
    - user: root
{% endif %}
