#!/bin/sh

show_help() { echo "Usage: $0 -u <username> -p <password>" 1>&2; exit 1; }

# A POSIX variable
OPTIND=1         # Reset in case getopts has been used previously in the shell.

# Initialize our own variables:

while getopts "h?u:p:" opt; do
    case "$opt" in
    h|\?)
        show_help
        ;;
    u)  user=$OPTARG
        ;;
    p)  password=$OPTARG
        ;;
    esac
done

shift $((OPTIND-1))

[ "${1:-}" = "--" ] && shift

if [ -z "$user" ]; then
  show_help
fi

if [ -z "$password" ]; then
  show_help
fi

java -Dconfig.file=conf/application.conf \
     -Dlogback.configurationFile=conf/logback.xml \
     -jar lib/seismicstats*.jar drop-db --user $user --pwd $password