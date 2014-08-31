riemann-dash:
  gem.installed:
    - user: rvm
    - require:
      - gem: riemann-client
      - gem: riemann-tools

riemann-client:
  gem.installed:
    - user: rvm

riemann-tools:
  gem.installed:
    - user: rvm
 