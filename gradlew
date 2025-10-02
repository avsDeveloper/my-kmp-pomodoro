#!/usr/bin/env sh

# Gradle wrapper script for Unix/Linux/Mac

# Set the location of the Gradle distribution
GRADLE_HOME=./gradle

# Set the path to the Gradle executable
GRADLE_EXECUTABLE="$GRADLE_HOME/bin/gradle"

# Check if the Gradle executable exists
if [ ! -f "$GRADLE_EXECUTABLE" ]; then
  echo "Gradle executable not found!"
  exit 1
fi

# Execute Gradle with the provided arguments
exec "$GRADLE_EXECUTABLE" "$@"