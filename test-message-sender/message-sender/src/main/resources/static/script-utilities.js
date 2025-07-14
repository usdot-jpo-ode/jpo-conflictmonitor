//
// Utilities for creating scripts for the script runner from JSON MAPs, SPATs and BSMs
//
class ScriptUtilities {


    recordStartTime;
    useOdeReceivedAt;

    //
    // Construct a ScriptUtilities object.
    //
    // Parameters:
    //
    // recordStartTime (number):
    //     Time to start recording in milliseconds
    //     For real-time recording, should be at or shortly before to the current clock time.
    //     For "useOdeReceivedAt" mode, should be at or shortly before the earliest OdeReceivedAt.
    //
    // useOdeReceivedAt (boolean):
    //     Whether to get timestamps from the OdeReceivedAt properties of the messages
    //     or to use the current time.
    //     Set to 'true' for a real-time recording, desregarding the OdeReceivedAt times in the messages.
    //     Set to 'false' to base the timestamps in the script relative to OdeReceivedAt
    //
    constructor(recordStartTime, useOdeReceivedAt) {
        this.recordStartTime = recordStartTime;
        this.useOdeReceivedAt = useOdeReceivedAt;
    }

    ISO_DATE_TIME = '@ISO_DATE_TIME@';
    MINUTE_OF_YEAR = '@MINUTE_OF_YEAR@';
    MILLI_OF_MINUTE = '@MILLI_OF_MINUTE@';
    TEMP_ID = '@TEMP_ID@';
    EPOCH_SECONDS = '@EPOCH_SECONDS@';
    OFFSET_SECONDS = '@OFFSET_SECONDS_#@'

    //
    // Generates a line of text for a script MAP message.
    //
    // MAP,[relative timestamp],[ODE JSON MAP message]
    //
    buildScriptFromMap(odeMap) {
        const scriptMap = JSON.parse(JSON.stringify(odeMap));
        const time = this.relativeTimestamp(scriptMap.metadata.odeReceivedAt);
        scriptMap.metadata.odeReceivedAt = this.ISO_DATE_TIME;
        const strMap = JSON.stringify(scriptMap);

        return "MAP," + time + "," + strMap;
    }

    //
    // Returns a line of text for a script from a ProcessedMap object with the format:
    //
    // ProcessedMap;[RSU ID];[Intersection ID],[relative timestamp],[ProcessedMap]
    //
    buildScriptFromProcessedMap(processedMap, key) {
        const scriptMap = JSON.parse(JSON.stringify(processedMap));
        const time = this.relativeTimestamp(scriptMap.properties.odeReceivedAt);
        scriptMap.properties.odeReceivedAt = this.ISO_DATE_TIME;
        scriptMap.properties.timeStamp = this.ISO_DATE_TIME;
        const strMap = JSON.stringify(scriptMap);

        return "ProcessedMap;" + key.rsuId + ";" + key.intersectionId + "," + time + "," + strMap;
    }

    //
    // Returns a line of text for a script from an ODE JSON SPAT object with the format:
    //
    // SPAT,[relative timestamp],[ODE JSON SPAT Message]
    //
    buildScriptFromSpat(odeSpat) {
        const scriptSpat = JSON.parse(JSON.stringify(odeSpat));
        const time = this.relativeTimestamp(scriptSpat.metadata.odeReceivedAt);
        scriptSpat.metadata.odeReceivedAt = this.ISO_DATE_TIME;
        scriptSpat.payload.data.timeStamp = this.MINUTE_OF_YEAR;
        for (const intersection of scriptSpat.payload.data.intersectionStateList.intersectionStatelist) {
            intersection.moy = this.MINUTE_OF_YEAR;
            intersection.timeStamp = this.MILLI_OF_MINUTE;
        }
        const strSpat = JSON.stringify(scriptSpat);

        return "SPAT," + time + "," + strSpat;
    }

    //
    // Generates a line of text for a script ProcessedSpat message.
    //
    // ProcessedSpat;[RSU ID];[Intersection ID],[relative timestamp],[ProcessedSpat message]
    //
    buildScriptFromProcessedSpat(processedSpat, key) {
        console.log(processedSpat);
        const scriptSpat = JSON.parse(JSON.stringify(processedSpat));
        const time = this.relativeTimestamp(scriptSpat.odeReceivedAt);
        let timestampMillis;
        if (processedSpat.utcTimeStamp != null) {
            timestampMillis = Date.parse(processedSpat.utcTimeStamp);
        } else if (processedSpat.odeReceivedAt != null) {
            timestampMillis = Date.parse(processedSpat.odeReceivedAt);
        }
        //console.log("timestampMillis: " + timestampMillis);
        scriptSpat.odeReceivedAt = this.ISO_DATE_TIME;
        scriptSpat.utcTimeStamp = this.ISO_DATE_TIME;
        for (const state of scriptSpat.states) {
            for (const event of state.stateTimeSpeed) {
                if (event.timing != null && event.timing.minEndTime != null) {
                    const minEndTimeMillis = Date.parse(event.timing.minEndTime);
                    //console.log("minEndTimeMillis: " + minEndTimeMillis);
                    const minEndTimeOffsetSeconds = (minEndTimeMillis - timestampMillis) / 1000;
                    //console.log("minEndTimeOffsetSeconds: " + minEndTimeOffsetSeconds);
                    event.timing.minEndTime = this.OFFSET_SECONDS.replace('#', minEndTimeOffsetSeconds.toString());
                }
                if (event.timing != null && event.timing.maxEndTime != null) {
                    const maxEndTimeMillis = Date.parse(event.timing.maxEndTime);
                    const maxEndTimeOffsetSeconds = (maxEndTimeMillis - timestampMillis) / 1000;
                    event.timing.maxEndTime = this.OFFSET_SECONDS.replace('#', maxEndTimeOffsetSeconds.toString());
                }
            }
        }
        const strSpat = JSON.stringify(scriptSpat);

        return "ProcessedSpat;" + key.rsuId + ";" + key.intersectionId + "," + time  + "," + strSpat;
    }

    //
    // Generates a line of text for a script BSM message.
    //
    // BSM,[relative timestamp],[ODE JSON BSM message]
    //
    buildScriptFromBsm(odeBsm) {
        const scriptBsm = JSON.parse(JSON.stringify(odeBsm))
        const time = this.relativeTimestamp(scriptBsm.metadata.odeReceivedAt);
        scriptBsm.metadata.odeReceivedAt = this.ISO_DATE_TIME;
        scriptBsm.payload.data.coreData.secMark = this.MILLI_OF_MINUTE;
        scriptBsm.payload.data.coreData.id = this.TEMP_ID;
        const strBsm = JSON.stringify(scriptBsm);
        return "BSM," + time + "," + strBsm;
    }

    //
    // Generates a line of text for a script ProcessedBsm message.
    //
    // BSM,[relative timestamp],[ODE JSON BSM message]
    // ProcessedBsm;[RSU ID];[Log ID];[],[relative timestamp],[ProcessedSpat message]
    //
    buildScriptFromProcessedBsm(processedBsm) {
        const scriptBsm = JSON.parse(JSON.stringify(processedBsm));
        const time = this.relativeTimestamp(scriptBsm.properties.odeReceivedAt);
        scriptBsm.properties.odeReceivedAt = this.ISO_DATE_TIME;
        scriptBsm.properties.secMark = this.MILLI_OF_MINUTE;
        scriptBsm.properties.id = this.TEMP_ID;
        const strBsm = JSON.stringify(scriptBsm);
        return "ProcessedBsm;"
            + scriptBsm.properties.originIp + ";"
            + scriptBsm.properties.logName + ","
            + time + ","
            + strBsm;
    }

    //
    // Combine an array of lines into a script file
    //
    // Input: An array of strings
    // Output: A line-delimited string containing the entire script
    //
    buildScriptFromLines(script) {
        let lines = '';
        for (const line of script) {
            lines += line + '\n';
        }
        return lines;
    }

    //
    // Gets a relative timestamp in milliseconds based on the recordStartTime and useOdeReceivedAt
    // settings.
    //
    // Input:
    //     odeReceivedAt: Timestamp in ISO8601 string format
    //
    relativeTimestamp(odeReceivedAt) {
        const dtReceivedAtMillis = Date.parse(odeReceivedAt);
        if (this.useOdeReceivedAt) {
            return dtReceivedAtMillis - this.recordStartTime;
        } else {
            return Date.now() - this.recordStartTime;
        }
    }
}



