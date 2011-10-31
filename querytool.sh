#!/bin/sh

# This file must be placed in the same as QueryTool jar file directory.

PRG=$0

while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '^.*-> \(.*\)$' 2>/dev/null`
    if expr "$link" : '^/' 2> /dev/null >/dev/null; then
        PRG="$link"
    else
        PRG="`dirname "$PRG"`/$link"
    fi
done

progdir=`dirname "$PRG"`

if [ -f "$progdir"/QueryTool.jar ] ; then
    java -jar "$progdir"/QueryTool.jar $*
fi
