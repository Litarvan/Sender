#!/usr/bin/env bash

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        echo "Sorry but your JAVA_HOME variable is not correctly set. Sender can't launch."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || echo "Sorry but you need to set the JAVA_HOME variable to your Java installation path. Sender can't launch"
fi

SENDER_LOCATION="`pwd -P`"

exec "$JAVACMD" -jar $SENDER_LOCATION/sender.jar "$@"
