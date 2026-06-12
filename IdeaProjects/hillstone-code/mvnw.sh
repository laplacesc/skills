#!/bin/bash
MAVEN_HOME="/Users/jxwu/Library/Application Support/JetBrains/IntelliJIdea2026.1/plugins/maven/lib/maven3"
PROJECT_DIR="/Users/jxwu/IdeaProjects/hillstone-code"
CP="${MAVEN_HOME}/boot/plexus-classworlds-2.9.0.jar"
exec java \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  --add-opens java.base/java.util=ALL-UNNAMED \
  --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens java.base/java.text=ALL-UNNAMED \
  --add-opens java.base/java.io=ALL-UNNAMED \
  --add-opens java.base/java.net=ALL-UNNAMED \
  --add-opens java.base/java.nio=ALL-UNNAMED \
  --enable-native-access=ALL-UNNAMED \
  -cp "${CP}" \
  "-Dclassworlds.conf=${MAVEN_HOME}/bin/m2.conf" \
  "-Dmaven.home=${MAVEN_HOME}" \
  "-Dmaven.multiModuleProjectDirectory=${PROJECT_DIR}" \
  "-Dlibrary.jansi.path=${MAVEN_HOME}/lib/jansi-native" \
  org.codehaus.plexus.classworlds.launcher.Launcher \
  "$@"
