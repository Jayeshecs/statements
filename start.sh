#!/bin/sh

echo "Copying standalong statements.war file..."

cp webapp/target/statements-webapp-*-jetty*.war ./statements.war

echo "Start statements application ..."

JETTY_CONSOLE_OPTIONS="--port 8080"

if [ "$1" = "headless" ]; then
	JETTY_CONSOLE_OPTIONS="$JETTY_CONSOLE_OPTIONS --headless"
fi
echo "Running command 'nohup java -jar statements.war $JETTY_CONSOLE_OPTIONS 2>&1 > logs/console.log &' ..."
nohup java -jar statements.war $JETTY_CONSOLE_OPTIONS 2>&1 > logs/console.log &

echo "To see progress make use of below command"
echo "tail -f logs/console.log"



