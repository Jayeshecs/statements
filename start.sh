#!/bin/sh
echo copying standalong statements.war file...
cp webapp/target/statements-webapp-*-jetty*.war ./statements.war
echo Start statements application ...
if [ "$1" == "headless" ] 
then
	nohup java -jar statements.war --headless 2>&1 > logs/console.log &
else
	nohup java -jar statements.war 2>&1 > logs/console.log &
fi
echo To see progress make use of below command
echo tail -f logs/console.log


