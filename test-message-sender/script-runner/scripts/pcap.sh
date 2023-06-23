#!/bin/bash

# Extracts SPAT, BSM and MAP messages as hex data from DSRC PCAP files
#
# Prerequisites:
#   - tshark
#   - jq
#
# Usage:
#
# $ ./pcap.sh 6324-RSU_DSRC_RX_2023-05-03_23-09-59.pcap
#
# generates output file:
# generates output file:
#
#  6324-RSU_DSRC_RX_2023-05-03_23-09-59.pcap.jsonl



outfile="$1".jsonl

tshark -r "$1" -T json -x \
| jq -c '
.[] | { 
	timeStamp: (._source.layers.frame."frame.time_epoch") | (tonumber * 1000) | floor, 
	hexMessage: 
		( 	._source.layers.wsmp."Wave Short Message"."ieee1609dot2.Ieee1609Dot2Data_element"?."ieee1609dot2.content_tree"."ieee1609dot2.signedData_element"."ieee1609dot2.tbsData_element"."ieee1609dot2.payload_element"."ieee1609dot2.data_element"."ieee1609dot2.content_tree"."ieee1609dot2.unsecuredData" 
			// ._source.layers.wsmp."Wave Short Message"."ieee1609dot2.Ieee1609Dot2Data_element"?."ieee1609dot2.content_tree"."ieee1609dot2.unsecuredData" 
			// ( (._source.layers.wsmp_raw[0][:-8]) | capture("(?<rest>0012.+)") | .rest ) )
		| gsub(":";"") | ascii_upcase }	
' > "$outfile"
