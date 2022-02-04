#!/bin/bash
chmod +x ./gradlew
./gradlew fatJar
cp build/libs/gp-test-fat-1.0-SNAPSHOT.jar ./executable.jar
echo "Run with 'java -jar executable.jar'"
