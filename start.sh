#!/bin/sh
echo copying standalong statements.war file...
cp webapp/target/statements-webapp-*-jetty*.war ./statements.war
echo Start statements application ...
nohup java -jar statements.war --headless 2>&1 > logs/console.log &
echo To see progress make use of below command
echo tail -f logs/console.log


