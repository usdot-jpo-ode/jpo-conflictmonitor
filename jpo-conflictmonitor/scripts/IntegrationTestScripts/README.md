# How to Run Test Scripts
_Prerequesites_:

A test machine with:
* Java 21
* Docker
* [Kafkacat/kcat](https://github.com/edenhill/kcat)

Clone this wiki repository to the test machine.

Run the following applications in Docker:
* [ODE](https://github.com/usdot-jpo-ode/jpo-ode)
* [ODE GeoJSON Converter](https://github.com/usdot-jpo-ode/jpo-geojsonconverter)
* [Conflict Monitor](https://github.com/usdot-jpo-ode/jpo-conflictmonitor)

Kafa must be mapped to localhost:9092.

Follow the output topic, and optionally the input (OdeJson___) topics, for the test being run.  For example for the "BSM Events" test, the following command follows the input and output topics:

```
$ kafkacat -b localhost:9092 -G group topic.OdeBsmJson topic.CMBsmEvents -f '\nTopic: %t\nKey: %k\nOffset: %o\nTimestamp: %T\nValue: %s\n'
```

Run scripts using the command line tool `script-runner-cli.jar` in the `/IntegrationTestScripts` folder.

Example:
```bash
$ cd IntegrationTestScripts
$ java -jar script-runner-cli.jar Script-BSMs.csv
```

View the kcat output to check that the output event was produced.



