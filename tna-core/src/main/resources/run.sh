#!/bin/bash

cp -Rn $TNA_HOME/conf-default/* $TNA_HOME/conf/
rm -rf $TNA_HOME/conf-default

exec java $JAVA_OPTS -Dlogging.config=$TNA_HOME/conf/logback.xml -Djava.security.egd=file:/dev/./urandom -jar $TNA_HOME/tna-core-@project.version@.war --spring.config.location=$TNA_HOME/conf/