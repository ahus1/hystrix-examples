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


gpg-import-D39DC0E3:
  cmd.run:
    - user: rvm
    - require:
      - user: rvm
    - name: gpg --keyserver hkp://keys.gnupg.net:80 --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3
    - unless: gpg --fingerprint |fgrep 'Key fingerprint = 409B 6B17 96C2 7546 2A17  0311 3804 BB82 D39D C0E3'

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
    - failhard: True
