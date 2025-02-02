## JPO Conflict Monitor Release Notes

## Version 1.3.0

### **Summary**
The forth release for the jpo-conflictmonitor, version 1.3.0

- Added Checks for SPaT and Map Broadcast rate when broadcast rate is 0
- Switched SPaT Index to use UtcTimestamps
- Updated Intersection Reference Alignment events to key by RSU
- Switched Conflict Monitor to Amazon Corretto Build images to retain packages required by Kafka
- Updated CmBsmEvents to include intersection Ids
- Added additional data sources to Kafka Connect and Index creation scripts
- Added Configuration to Manage MongoDB volumes and collection sizes
- Removed Deduplicator and moved it to a new repository
- Bug Fixes


## Version 1.2.0

### **Summary**
The third release for the jpo-conflictmonitor, version 1.2.0.

- Moved Configuration Parameters to Kafka Topics
- Added REST API to enable easy configuration of data on configuration topics
- Added new scripts to automatically create mongoDB indexes
- Added new scripts to automatically configure mongoDB as a replica set
- Updated Kafka Connecto to add expiry times to Ode Data
- Bug Fixes
- Updated to Java 21



## Version 1.1.0

### **Summary**

The second release for the jpo-conflictmonitor, version 1.1.0. The conflict monitor provides functionallity for performing near realtime analysis of SPaT, Map and BSM messages generated by the ODE. This analysis is used to generate Events, Assessments and Notifications about performance of intersections feeding into the Conflict Monitor.  Read more about the jpo-conflictvisualizer in the [main README](../README.md).

Enhancements in this release:

- Updates to Stop Line Stop Event and Stop Line Passage Event logic to better detect vehicle ingress and egress pairs
- Improved CIMMS data partitioning to properly scale with available brokers and intersections
- Improved System Assessments to aggregate multiple events for a given intersection
- Notifications have been adjusted to only trigger once for each failing case in an assessment
- Updated BSM parititoning to spatially link intersections with BSM's
- updated BSM path building to properly truncate vehicle events when a Vehicle leaves the intersection
- Added ability to store algorithm configuration in mongoDB
- Updated Assessment Aggregators to pair Assessments with the events that generated them
- Corrected Time Change Details Event logic not matching logic outlined in CIMMS system requirements document

## Version 1.0.0

### **Summary**

The first release for the jpo-conflictmonitor, version 1.0.0. The conflict monitor provides functionallity for performing near realtime analysis of SPaT, Map and BSM messages generated by the ODE. This analysis is used to generate Events, Assessments and Notifications about performance of intersections feeding into the Conflict Monitor.  Read more about the jpo-conflictvisualizer in the [main README](../README.md).

Enhancements in this release:

- Ability to detect OBU's passing through an intersection and create events
- Ability to create the following events: SpatBroadcastRate, MapBroadcastRate, SpatMinimumdata, MapMinimumData, ConnectionOfTravel, IntersectionReferenceAlignmentEvents, LaneDirectionOfTravelEvents, SignalGroupAlignmentEvents, SignalStateConflictEvents, SigtnalStateStopEvents, TimeChangeDetailsEvents
- Ability to create the following assessments: ConnectionOfTravelAssessment, LaneDirectionOfTravelAssessments, SignalStateAssessmentGroup, SignalStateEventAssessments
- Ability to create the following Notifications: ConnectionOfTravelNotification, IntersectionReferenceAlignmentNotification, LaneDirectionOfTravelNotification, SignalGroupAlignmentNotification, SignalStateConflictNotification, TimeChangeDetailsNotification
- Topic configuration system build upon mongoDB for configuration state store
- Initial Commit: Adding Codebase for ingesting data, generating events, assessments and notifications.
