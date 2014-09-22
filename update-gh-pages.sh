# Take from and modified http://sleepycoders.blogspot.de/2013/03/sharing-travis-ci-generated-files.html?_escaped_fragment_#!
if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo -e "Starting to update gh-pages\n"
  echo -e "Done publishing to gh-pages.\n"
fi
