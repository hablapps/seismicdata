#!/bin/sh

show_help() { echo "Usage: $0 -u <username> -h <hostname> -s <seedlink_host>" 1>&2; exit 1; }

# A POSIX variable
OPTIND=1         # Reset in case getopts has been used previously in the shell.

# Initialize our own variables:

seedhost=193.144.251.102
user=jserrano
host=pct-empresas-122.uc3m.es

while getopts "?u:h:s:" opt; do
    case "$opt" in
    \?) show_help
		;;
    u)  user=$OPTARG
        ;;
    h)  host=$OPTARG
        ;;
    s)  seedhost=$OPTARG
        ;;
    esac
done

shift $((OPTIND-1))

[ "${1:-}" = "--" ] && shift

if [ -z "$user" ]; then
  show_help
fi

if [ -z "$host" ]; then
  show_help
fi

if [ -z "$seedhost" ]; then
  show_help
fi

ssh -L 18000:$seedhost:18000 $user@$host

