#!/bin/sh
# Build executable WAR
mvn clean install -Dmavenmixin-jettyconsole $*
