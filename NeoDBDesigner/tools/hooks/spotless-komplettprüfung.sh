#!/bin/sh

echo '[git hook] executing gradle spotlessCheck before commit'

# stash any unstaged changes
git stash -q --keep-index

# run the spotlessCheck with the gradle wrapper
./gradlew spotlessCheck --daemon

# store the last exit code in a variable
RESULT=$?

# unstash the unstashed changes
git stash pop -q

# return the './gradlew spotlessCheck' exit code
exit $RESULT