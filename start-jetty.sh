#!/bin/sh
export ISIS_OPTS_SEPARATOR=","
export ISIS_OPTS="isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL=jdbc:hsqldb:file:D:\\backup\\ubuntu\\opt\\data\\statements\\statements-data\\hsql-db\\db;hsqldb.write_delay=false;shutdown=true"

java -jar statements.war
