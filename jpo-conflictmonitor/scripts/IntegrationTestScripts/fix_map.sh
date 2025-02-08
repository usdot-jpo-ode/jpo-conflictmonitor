#!/bin/bash

cat ApproachAndStopAtLine-through-BSMfromDifferentRSU.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > ApproachAndStopAtLine-through-BSMfromDifferentRSU_FIXED.csv
cat ApproachAndStopAtLine-through.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > ApproachAndStopAtLine-through_FIXED.csv
cat ApproachAndStopAtLine.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > ApproachAndStopAtLine_FIXED.csv
cat CDOT_Script.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > CDOT_Script_FIXED.csv
cat ConnectionOfTravel-NoSPAT.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > ConnectionOfTravel-NoSPAT_FIXED.csv
cat ConnectionOfTravel-connected.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > ConnectionOfTravel-connected_FIXED.csv
cat ConnectionOfTravel-u-turn.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > ConnectionOfTravel-u-turn_FIXED.csv
cat MAP_MinData.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > MAP_MinData_FIXED.csv
cat MAP_Validation.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > MAP_Validation_FIXED.csv
cat MAPs.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > MAPs_FIXED.csv
cat MultipleMAPs.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > MultipleMAPs_FIXED.csv
cat Script-SPAT-MAP-unaligned.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > Script-SPAT-MAP-unaligned_FIXED.csv
cat Script-SPAT-MAP.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > Script-SPAT-MAP_FIXED.csv
cat Script-SignalStateConflict-Permissive.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > Script-SignalStateConflict-Permissive_FIXED.csv
cat Script-SignalStateConflict-Protected.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > Script-SignalStateConflict-Protected_FIXED.csv
cat SignalGroupAlignment-aligned.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > SignalGroupAlignment-aligned_FIXED.csv
cat SignalGroupAlignment-unaligned.csv | sed 's/":{"NodeXY//g' | sed 's/}]}},"connectsTo/}]},"connectsTo/g' > SignalGroupAlignment-unaligned_FIXED.csv
