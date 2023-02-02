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

To use the script-runner CLI, find the executable `script-runner-cli.jar` under the `test-message-sender/script-runner/target` folder.

Script runner usage:
```
Command line script runner usage:

Executable jar:
$ java -jar target/script-runner-cli.jar [filename]

Maven:
$ mvn spring-boot:run -Dspring-boot.run.arguments=[filename]
```

## Script Format

The scripts are hybrid CSV/JSON files with the following format:
```
message-type,relative-timestamp,json-template
message-type,relative-timestamp,json-template
...
```

`message-type` can be `BSM`, `SPAT`, or `MAP`.

`relative-timestamp` is the number of milliseconds relative to the start time.

`json-template` is a OdeSpatJson, OdeMapJson, or OdeBsmJson object on a single line, with timestamps replaced with the following placeholders:

* `@ISO_DATE_TIME@` - Date/Time string.
* `@TEMP_ID@` - Temporary ID for BSMs, a string of 8 hex digits.
* `"@MINUTE_OF_YEAR@"` - Numeric minute of the year.
* `"@MILLI_OF_MINUTE@"` - Numeric millisecond of the minute.


The script runner converts the timestamps to times relative to the script start time, inserts them into the JSON templates, and schedules the messages to be sent to the appropriate topics in real time.