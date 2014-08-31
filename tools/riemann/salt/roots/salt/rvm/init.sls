rvm:
  group:
    - present
  user.present:
    - gid: rvm
    - home: /home/rvm
    - require:
      - group: rvm

rvm-deps:
  pkg.installed:
    - refresh: False
    - names:
      - bash
      - coreutils
      - gzip
      - bzip2
      - gawk
      - sed
      - curl
      - subversion

mri-deps:
  pkg.installed:
    - refresh: False
    - names:
      - openssl
      - curl
      - autoconf
      - automake
      - libtool
      - bison
      - subversion
      - ruby
      - zlib-devel
      - patch
      - openssl-devel
      - libxml2
      - libxml2-devel
      - readline
      - readline-devel

# temporary fix, run as rvm: curl -s https://raw.githubusercontent.com/wayneeseguin/rvm/master/binscripts/rvm-installer | bash -s stable
# https://sourcegraph.com/github.com/saltstack/salt/tree/master/salt/modules/rvm.py
ruby-2.0.0:
  rvm.installed:
    - default: True
    - user: rvm
    - require:
      - pkg: rvm-deps
      - pkg: mri-deps
      - user: rvm
