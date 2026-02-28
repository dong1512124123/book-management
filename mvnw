#!/bin/sh
# Maven Wrapper 스크립트 (Linux/Mac용)
# Spring Boot 프로젝트 실행: ./mvnw spring-boot:run

set -e

# 프로젝트 루트 디렉토리 결정
if [ -z "$MAVEN_PROJECTBASEDIR" ]; then
  MAVEN_PROJECTBASEDIR=$(cd "$(dirname "$0")" && pwd)
fi

MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

# wrapper properties에서 Maven 다운로드 URL 읽기
if [ -r "$MAVEN_WRAPPER_PROPERTIES" ]; then
  distributionUrl=$(grep 'distributionUrl' "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2-)
fi

if [ -z "$distributionUrl" ]; then
  distributionUrl="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip"
fi

# Maven 홈 디렉토리
MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-3.9.6"

if [ ! -d "$MAVEN_HOME" ]; then
  echo "Maven을 다운로드합니다..."
  mkdir -p "$MAVEN_HOME"
  TMPFILE=$(mktemp /tmp/maven-XXXXXX.zip)

  if command -v curl > /dev/null 2>&1; then
    curl -fsSL -o "$TMPFILE" "$distributionUrl"
  elif command -v wget > /dev/null 2>&1; then
    wget -q -O "$TMPFILE" "$distributionUrl"
  else
    echo "오류: curl 또는 wget이 필요합니다."
    exit 1
  fi

  unzip -q -o "$TMPFILE" -d "$MAVEN_HOME"
  rm -f "$TMPFILE"
fi

# Maven 실행 파일 찾기
MAVEN_EXEC=$(find "$MAVEN_HOME" -name "mvn" -path "*/bin/mvn" | head -1)

if [ -z "$MAVEN_EXEC" ]; then
  echo "오류: Maven 실행 파일을 찾을 수 없습니다."
  exit 1
fi

chmod +x "$MAVEN_EXEC"
exec "$MAVEN_EXEC" "$@"
