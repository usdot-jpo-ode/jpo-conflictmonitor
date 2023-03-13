# Integration Test Utilities
This directory contains two tools for integration testing:
* message-sender - A GUI map for sending BSM/SPAT/MAP test messages in real time and creating test scripts.
* script-runner - A command line tool for running test scripts.

# Installation
_Prerequesites_:

The tools require the `jpo-ode` and `jpo-geojsonconver` projects be installed in the local Maven repository by running:
```bash
$ mvn clean install
```
from the home directory of both of those projects.

To compile the message-sender and script-runner, from the `test-message-sender` project run:
```
$ mvn clean install
```

## Using the Message Sender/ Script Creator GUI

To run the message-sender GUI, from the `test-message-sender/message-sender` directory, run:
```bash
$ mvn spring-boot:run
```

and navigate to `http:localhost:8088` in a browser.  More details: [message-sender README](message-sender/README.md)

## Using the Script Runner Command Line Tool

The script runner CLI can be used to run either ODE JSON scripts, or JSON ASN1 hex log files.

To use the script-runner CLI, find the executable `script-runner-cli.jar` under the `test-message-sender/script-runner/target` folder.

Script runner usage:
```
Usage:

The tool can run in two modes: either run a script, or send a hex log to the ODE and create a script.

Run Script:

  Executable jar:
    $ java -jar script-runner-cli.jar <filename>

  Maven:
    $ mvn spring-boot:run -Dspring-boot.run.arguments=<filename>

  <filename> : (Required in script run mode) Input ODE JSON script file
               Formatted as line-delimited CSV/JSON like:
               <BSM or MAP or SPAT>,<millisecond offset>,<Templated ODE JSON>

Send hex log to ODE and create script:

  Executable jar:
   $ java -jar script-runner-cli.jar --infile=<filename> --outfile=<filename> --ip=<docker host ip> ...

  Maven:
    $ mvn spring-boot:run -Dspring-boot.run.arguments="--infile=<filename> --outfile=<filename> ..."

  Options:
    --hexfile=<filename> : (Required in hex log mode) Input hex log file
                           formatted as line-delimited JSON like
                           { "timeStamp": <epoch milliseconds>, "dir": <"S" or "R">, "hexMessage": "00142846F..." }

    --outfile=<filename> : (Optional) Output file to save JSON received from the ODE as a script,
                           optionally substituting placeholders for timestamps

    --placeholders       : (Optional) If present substitute placeholders in the output script:

                           @ISO_DATE_TIME@ for 'odeReceivedAt'
                           @MINUTE_OF_YEAR@ for 'timeStamp' and 'intersection.moy' in SPATs
                           @MILLI_OF_MINUTE@ for 'intersection.timeStamp' in SPATs and 'secMark' in BSMs
                                                    @TEMP_ID@ for 'coreData.id' in BSMs

    --mapfile=<filename> : (Optional) Output file to save MAPs as line-delimited JSON

    --spatfile=<filename>: (Optional) Output file to save SPATs as line-delimited JSON

    --bsmfile=<filename> : (Optional) Output file to save BSMs as line-delimited JSON

    --ip=<docker host ip>: (Optional) IP address of docker host to send UDP packets to

                           Uses DOCKER_HOST_IP env variable if not specified.

```

## CSV/JSON Script Format

The scripts are hybrid CSV/JSON files with the following format:
```
message-type,relative-timestamp,json-template
message-type,relative-timestamp,json-template
...
```

`message-type` can be `BSM`, `SPAT`, or `MAP`.

`relative-timestamp` is the number of milliseconds relative to the start time.

`json-template` is a OdeSpatJson, OdeMapJson, or OdeBsmJson object on a single line, with the following placeholders:

* `@ISO_DATE_TIME@` - Date/Time string.
* `@TEMP_ID@` - Temporary ID for BSMs, a string of 8 hex digits.
* `"@MINUTE_OF_YEAR@"` - Numeric minute of the year.
* `"@MILLI_OF_MINUTE@"` - Numeric millisecond of the minute.


The script runner converts the timestamps to times relative to the script start time, inserts them into the JSON templates, and schedules the messages to be sent to the appropriate topics in real time.  It generates a random BSM Temporary ID for each script run.

## Hex Log File Format

Hex log format is line delimited JSON:

```json
{ "timeStamp": <epoch milliseconds>, "dir": <"S" or "R">, "hexMessage": "00142846F..." }
{ "timeStamp": <epoch milliseconds>, "dir": <"S" or "R">, "hexMessage": "00142846F..." }
```

The hex messages are UPER encoded J2735 MessageFrames.

Example command line to schedule sending a hex log through the system and generate ODE JSON files.

```bash
java -jar script-runner-cli.jar --ip=172.25.0.112 --hexfile=et20220818-181134.log --outfile=et20220818-181134.csv --delay=60000 --placeholders --mapfile=map.jsonl --spatfile=spat.jsonl --bsmfile=bsm.jsonl
```
