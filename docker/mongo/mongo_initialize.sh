#!/bin/bash

connect_string="$1"
mongosh "${connect_string}" create_ttl_indexes.js

$SHELL