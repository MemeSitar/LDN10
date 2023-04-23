#!/bin/sh

export JSON_JAVA=/root/RK/LDN7
export CLASSPATH=$CLASSPATH:$JSON_JAVA/json-simple-1.1.1.jar
javac -cp ./json-simple-1.1.1.jar *.java
