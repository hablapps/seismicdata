#!/bin/sh

show_help() { echo "Usage: $0" 1>&2; exit 1; }

# A POSIX variable
OPTIND=1         # Reset in case getopts has been used previously in the shell.

# Initialize our own variables:

while getopts "h?" opt; do
    case "$opt" in
    h|\?)
        show_help
        ;;
    esac
done

shift $((OPTIND-1))

[ "${1:-}" = "--" ] && shift

java -Dconfig.file=conf/application.conf \
     -Dlogback.configurationFile=conf/logback.xml \
     -jar lib/seismicstats*.jar start >/dev/null