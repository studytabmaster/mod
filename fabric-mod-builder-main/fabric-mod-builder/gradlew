#!/bin/sh
# Gradle wrapper launcher. Requires gradle/wrapper/gradle-wrapper.jar
APP_HOME=$(cd "$(dirname "$0")" && pwd -P)
JAVACMD=${JAVA_HOME:+$JAVA_HOME/bin/}java
exec "$JAVACMD" -Xmx64m -Xms64m \
  -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
  org.gradle.wrapper.GradleWrapperMain "$@"
