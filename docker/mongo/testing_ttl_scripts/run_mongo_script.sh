#!/bin/bash

#
# Utility to run a mongosh script
#
# Prerequesites:
#   - A Bash shell (Linux, or on Windows use Cygwin or Git Bash, for example)
#   - Install the MongoDB shell: https://docs.mongodb.com/mongodb-shell/
#

usage()
{
    echo ""
    echo "Usage: ./run_mongo_script.sh script.js"
    echo "    script.js"
    echo "      - Path to the script to run."
    echo ""
}

if [ $# -lt 1 ]
then
    usage
    exit
fi

connect_string="mongodb://localhost:27017/ConflictMonitor"
mongosh "${connect_string}" "$1"

$SHELL