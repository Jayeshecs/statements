#!/bin/bash
mvn clean install -Dskip.isis-validate -Dskip.isis-swagger -DskipTests -T1C $*
