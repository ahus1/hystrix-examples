{% from "php5/map.jinja" import php5 with context %}


include:
  - php5.fpm
  - php5.repo


# Here we just add a requisite declaration to ensure correct order
extend:
  php5_repo:
    pkgrepo:
      - require_in:
        - pkg: php5-fpm
