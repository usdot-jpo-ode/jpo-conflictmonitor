#!/bin/bash

echo "Running mongo_initialize.sh"

connect_string="$1"
echo "connect_string = ${connect_string}"
cd /scripts
ls

mongosh "${connect_string}" create_ttl_indexes.js

$SHELL