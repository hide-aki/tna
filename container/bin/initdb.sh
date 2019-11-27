#!/bin/bash

if [ "$#" -ne 7 ]; then
        echo "Usage: initdb <mysql|postgresql> <database name> <db User> <dbPassword> <host> <port> <masterDb>"
        exit 1
elif [[  "$1" != "mysql"  &&  "$1" != "postgresql"  ]]; then
        echo "Usage: initdb <mysql|postgresql> <database name> <db User> <dbPassword>"
        exit 1
fi

#elif [ ! -f "$3" ]; then
#        echo "File $3 does not exist"
#        exit 1


if [ "$1" = "postgresql" ]; then
    #echo "postgresql database selected."
    if PGPASSWORD="$4" psql -h "$5" -p "$6" -U "$3" -lqt | cut -d \| -f 1 | grep -qw "$2"; then
        echo "database $2 already exists."
    else
        PGPASSWORD="$4" psql -h "$5" -p "$6" -U "$3" "$7" -c 'CREATE DATABASE '"$2"' OWNER '"$3"''
        if [ "$?" -eq 0 ]; then
                echo "$2 database created successfully"
                #PGPASSWORD="$4" psql -h "$5" -p "$6" -U "$3" $2 < $3
        else
                echo "Failed to create $2 database"
        fi
    fi
else
    #echo "mysql database selected."
    RESULT=`mysqlshow --user=$3 --password=$4 $2| grep "Database: $2"`
    echo $RESULT
    if [ "$RESULT" == "Database: $2" ]; then
        echo "database $2 already exists."
    else
        echo "database $2 does not exists."
        mysql --user=$3 --password=$4 -e "create database $2"
        #mysql --user=$3 --password=$4 $2 < $3
    fi
fi