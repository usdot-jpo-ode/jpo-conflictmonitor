# Mongo Data Definitions
The following document provides additional information about database collections created and managed by CIMMS.

| Collection Name | Source | Definition |
| ----------- | ----------- |----------- |
|BsmGeoJson| Other | [BsmGeoJson](#bsmgeojson)  |
|CVCounts| Other | [CVCounts](#cvcounts)  |
|CmAppHealthNotifications| Conflict Monitor | [CmAppHealthNotifications](#cmapphealthnotifications)  |
|CmBsmEvents| Conflict Monitor | [CmBsmEvents](#cmbsmevents)  |
|CmConnectionOfTravelAssessment|Conflict Monitor| [CmConnectionOfTravelAssessment](#cmconnectionoftravelassessment)  |
|CmConnectionOfTravelEvent|Conflict Monitor| [CmConnectionOfTravelEvent](#cmconnectionoftravelevent)  |
|CmConnectionOfTravelNotification|Conflict Monitor| [CmConnectionOfTravelNotification](#cmconnectionoftravelnotification)  |
|CmIntersectionReferenceAlignmentEvents|Conflict Monitor| [CmIntersectionReferenceAlignmentEvents](#cmintersectionreferencealignmentevents)  |
|CmLaneDirectionOfTravelAssessment|Conflict Monitor| [CmLaneDirectionOfTravelAssessment](#cmlanedirectionoftravelassessment)  |
|CmLaneDirectionOfTravelEvent|Conflict Monitor| [CmLaneDirectionOfTravelEvent](#cmlanedirectionoftravelevent)  |
|CmLaneDirectionOfTravelNotification|Conflict Monitor| [CmLaneDirectionOfTravelNotification](#cmlanedirectionoftravelnotification)  |
|CmMapBroadcastRateEvents|Conflict Monitor| [CmMapBroadcastRateEvents](#cmmapbroadcastrateevents)  |
|CmMapMinimumDataEvents|Conflict Monitor| [CmMapMinimumDataEvents](#cmmapminimumdataevents)  |
|CmNotification|Conflict Monitor| [CmNotification](#cmnotification)  |
|CmReport|Conflict Monitor| [CmReport](#cmreport)  |
|CmSignalGroupAlignmentEvents|Conflict Monitor| [CmSignalGroupAlignmentEvents](#cmsignalgroupalignmentevents)  |
|CmSignalGroupAlignmentNotification|Conflict Monitor| [CmSignalGroupAlignmentNotification](#cmsignalgroupalignmentnotification)  |
|CmSignalStateConflictEvents|Conflict Monitor| [CmSignalStateConflictEvents](#cmsignalstateconflictevents)  |
|CmSignalStateConflictNotification|Conflict Monitor| [CmSignalStateConflictNotification](#cmsignalstateconflictnotification)  |
|CmSignalStateEventAssessment|Conflict Monitor| [CmSignalStateEventAssessment](#cmsignalstateeventassessment)  |
|CmSpatBroadcastRateEvents|Conflict Monitor| [CmSpatBroadcastRateEvents](#cmspatbroadcastrateevents)  |
|CmSpatMinimumDataEvents|Conflict Monitor| [CmSpatMinimumDataEvents](#cmspatminimumdataevents)  |
|CmSpatTimeChangeDetailsEvent|Conflict Monitor| [CmSpatTimeChangeDetailsEvent](#cmspattimechangedetailsevent)  |
|CmSpatTimeChangeDetailsNotification|Conflict Monitor| [CmSpatTimeChangeDetailsNotification](#cmspattimechangedetailsnotification)  |
|CmStopLinePassageEvent|Conflict Monitor| [CmStopLinePassageEvent](#cmstoplinepassageevent)  |
|CmStopLinePassageNotification|Conflict Monitor| [CmStopLinePassageNotification](#cmstoplinepassagenotification)  |
|CmStopLineStopAssessment|Conflict Monitor| [CmStopLineStopAssessment](#cmstoplinestopassessment)  |
|CmStopLineStopEvent|Conflict Monitor| [CmStopLineStopEvent](#cmstoplinestopevent)  |
|CmStopLineStopNotification|Conflict Monitor| [CmStopLineStopNotification](#cmstoplinestopnotification)  |
|OdeBsmJson| ODE | [OdeBsmJson](#odebsmjson)  |
|OdeDriverAlertJson| ODE | [OdeDriverAlertJson](#odedriveralertjson)  |
|OdeMapJson| ODE | [OdeMapJson](#odemapjson)  |
|OdeRawEncodedBSMJson| ODE | [OdeRawEncodedBSMJson](#oderawencodedbsmjson)  |
|OdeRawEncodedMAPJson| ODE | [OdeRawEncodedMAPJson](#oderawencodedmapjson)  |
|OdeRawEncodedSPATJson| ODE | [OdeRawEncodedSPATJson](#oderawencodedspatjson)  |
|OdeRawEncodedSRMJson| ODE | [OdeRawEncodedSRMJson](#oderawencodedsrmjson)  |
|OdeRawEncodedSSMJson| ODE | [OdeRawEncodedSSMJson](#oderawencodedssmjson)  |
|OdeRawEncodedTIMJson| ODE | [OdeRawEncodedTIMJson](#oderawencodedtimjson)  |
|OdeSpatJson| ODE | [OdeSpatJson](#odespatjson)  |
|OdeSpatRxJson| ODE | [OdeSpatRxJson](#odespatrxjson)  |
|OdeSrmJson| ODE | [OdeSrmJson](#odesrmjson)  |
|OdeSsmJson| ODE | [OdeSsmJson](#odessmjson)  |
|OdeTIMCertExpirationTimeJson| ODE | [OdeTIMCertExpirationTimeJson](#odetimcertexpirationtimejson)  |
|OdeTimBroadcastJson| ODE | [OdeTimBroadcastJson](#odetimbroadcastjson)  |
|OdeTimJson| ODE | [OdeTimJson](#odetimjson)  |
|ProcessedMap| GeoJson Converter | [ProcessedMap](#processedmap)  |
|ProcessedSpat| GeoJson Converter | [ProcessedSpat](#processedspat)  |
|V2XGeoJson| Other | [V2XGeoJson](#v2xgeojson)  |

## BsmGeoJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { 'properties.timestamp': -1, geometry: '2dsphere' },
    name: 'timestamp_geosphere_index',
    '2dsphereIndexVersion': 3
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('662135f509cf681c9ac12fe7'),
    type: 'Feature',
    geometry: { type: 'Point', coordinates: [ -104.879926, 39.4135458 ] },
    properties: {
      id: '192.168.0.1',
      timestamp: ISODate('2024-04-18T15:02:13.387Z')
    }
  }
]
```
## CmBsmEvents
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
No Sample Record Available Yet.
```
## CVCounts
### Indexes
```javascript
[ { v: 2, key: { _id: 1 }, name: '_id_' } ]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('6639c2131ac072c9ac7ed54d'),
    messageType: 'Map',
    rsuIp: '192.168.0.1',
    timestamp: ISODate('2024-05-06T05:00:00.000Z'),
    count: 24
  }
]
```
## CmAppHealthNotifications
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { notificationGeneratedAt: 1 },
    name: 'notificationGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, notificationGeneratedAt: -1 },
    name: 'intersectionID_-1_notificationGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('6634324dd6ab450d2c6cfd80'),
    notificationGeneratedAt: ISODate('2024-05-03T00:39:41.543Z'),
    notificationText: 'Error encountered committing offsets via consumer',
    notificationHeading: 'Streams error on notification',
    roadRegulatorID: Long('0'),
    intersectionID: Long('0'),
    notificationType: 'AppHealthNotification',
    uniqueId: 'KafkaStreamsUnhandledExceptionEvent_jpoode-conflictmonitor-745bb64bf6-qjgfm_notification',
    exceptionEvent: {
      topology: 'notification',
      eventGeneratedAt: Long('1714696781515'),
      appId: 'jpoode-conflictmonitor-745bb64bf6-qjgfm',
      roadRegulatorID: Long('0'),
      intersectionID: Long('0'),
      eventType: 'KafkaStreamsUnhandledExceptionEvent'
    }
  }
]
```
## CmConnectionOfTravelAssessment
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { assessmentGeneratedAt: 1 },
    name: 'assessmentGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, assessmentGeneratedAt: -1 },
    name: 'intersectionID_-1_assessmentGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660b9799d6ab450d2c99ab7a'),
    assessmentGeneratedAt: ISODate('2024-04-02T05:28:57.703Z'),
    assessmentType: 'ConnectionOfTravel',
    roadRegulatorID: Long('-1'),
    intersectionID: Long('1234'),
    connectionOfTravelAssessmentGroups: [
      {
        egressLaneID: Long('12'),
        ingressLaneID: Long('6'),
        connectionID: Long('0'),
        eventCount: Long('1')
      }
    ],
    source: '{"rsuId":"192.168.0.1","intersectionId":1234,"region":0}',
    timestamp: Long('1712035737703')
  }
]
```
## CmConnectionOfTravelEvent
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660b9799374b1e1a4f304112'),
    egressLaneID: Long('12'),
    ingressLaneID: Long('6'),
    eventGeneratedAt: ISODate('2024-04-02T05:28:57.570Z'),
    roadRegulatorID: Long('-1'),
    intersectionID: Long('1234'),
    connectionID: Long('0'),
    eventType: 'ConnectionOfTravel',
    source: "{ rsuId='192.168.0.1', intersectionId='1234', region='0'}",
    timestamp: Long('1712035716700')
  }
]
```
## CmConnectionOfTravelNotification
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { notificationGeneratedAt: 1 },
    name: 'notificationGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, notificationGeneratedAt: -1 },
    name: 'intersectionID_-1_notificationGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId("65ef20c480484e7e5c9c9e8b"),
    notificationGeneratedAt: ISODate("2024-03-08T22:30:06.786Z"),
    assessment: {
      assessmentGeneratedAt: Long("1709937006785"),
      assessmentType: 'ConnectionOfTravel',
      roadRegulatorID: Long("-1"),
      intersectionID: Long("6311"),
      connectionOfTravelAssessmentGroups: [
        {
          egressLaneID: Long("32"),
          ingressLaneID: Long("9"),
          connectionID: Long("5"),
          eventCount: Long("32")
        },
        {
          egressLaneID: Long("23"),
          ingressLaneID: Long("1"),
          connectionID: Long("-1"),
          eventCount: Long("10")
        },
      ],
      source: '{"rsuId":"192.168.0.1","intersectionId":6311,"region":0}',
      timestamp: Long("1709937006785")
    },
    egressLane: Long("23"),
    ingressLane: Long("1"),
    notificationText: 'Connection of Travel Notification, Unknown Lane connection between ingress lane: 1 and egress lane: 23.',
    notificationHeading: 'Connection of Travel Notification',
    roadRegulatorID: Long("-1"),
    intersectionID: Long("6311"),
    notificationType: 'ConnectionOfTravelNotification',
    key: 'ConnectionOfTravelNotification_6311_-1_0_0'
  }
]
```
## CmIntersectionReferenceAlignmentEvents
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
No Sample Record Available Yet.
```
## CmLaneDirectionOfTravelAssessment
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { assessmentGeneratedAt: 1 },
    name: 'assessmentGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, assessmentGeneratedAt: -1 },
    name: 'intersectionID_-1_assessmentGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660b97ae374b1e1a4f309299'),
    assessmentGeneratedAt: ISODate('2024-04-02T05:29:17.984Z'),
    assessmentType: 'LaneDirectionOfTravel',
    roadRegulatorID: Long('-1'),
    intersectionID: Long('1234'),
    source: '{"rsuId":"192.168.0.1","intersectionId":1234,"region":0}',
    laneDirectionOfTravelAssessmentGroup: [
      {
        medianInToleranceCenterlineDistance: 182.00575810752986,
        laneID: Long('2'),
        medianInToleranceHeading: 20.1,
        medianCenterlineDistance: 182.00575810752986,
        expectedHeading: 19.972609493735604,
        segmentID: Long('1'),
        inToleranceEvents: Long('1'),
        distanceFromCenterlineTolerance: 100,
        medianHeading: 20.1,
        tolerance: 20,
        outOfToleranceEvents: Long('0')
      }
    ],
    timestamp: Long('1712035757984')
  }
]
```
## CmLaneDirectionOfTravelEvent
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660b97add6ab450d2c99dead'),
    laneSegmentFinalLatitude: 39.554397227163044,
    laneSegmentFinalLongitude: -105.08463739662102,
    roadRegulatorID: Long('-1'),
    eventType: 'LaneDirectionOfTravel',
    source: "{ rsuId='192.168.0.1', intersectionId='1234', region='0'}",
    medianDistanceFromCenterline: 182.00575810752986,
    laneID: Long('2'),
    laneSegmentInitialLatitude: 39.55494989020813,
    laneSegmentNumber: Long('1'),
    expectedHeading: 19.972609493735604,
    eventGeneratedAt: ISODate('2024-04-02T05:29:17.935Z'),
    intersectionID: Long('1234'),
    aggregateBSMCount: Long('8'),
    medianVehicleHeading: 20.1,
    timestamp: Long('1712035738951'),
    laneSegmentInitialLongitude: -105.08437793184365
  }
]
```






## CmLaneDirectionOfTravelNotification
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { notificationGeneratedAt: 1 },
    name: 'notificationGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, notificationGeneratedAt: -1 },
    name: 'intersectionID_-1_notificationGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId("65ef20c480484e7e5c9c9b4c"),
    notificationGeneratedAt: ISODate("2024-03-08T21:57:27.161Z"),
    assessment: {
      assessmentGeneratedAt: Long("1709935047160"),
      assessmentType: 'LaneDirectionOfTravel',
      roadRegulatorID: Long("-1"),
      intersectionID: Long("6324"),
      source: '{"rsuId":"192.168.0.1","intersectionId":6324,"region":0}',
      laneDirectionOfTravelAssessmentGroup: [
        {
          medianInToleranceCenterlineDistance: 111.2083246771293,
          laneID: Long("5"),
          medianInToleranceHeading: 267.9,
          medianCenterlineDistance: 111.2083246771293,
          expectedHeading: 268.615617588506,
          segmentID: Long("1"),
          inToleranceEvents: Long("2"),
          distanceFromCenterlineTolerance: 100,
          medianHeading: 267.9,
          tolerance: 20,
          outOfToleranceEvents: Long("0")
        },
        {
          medianInToleranceCenterlineDistance: 0,
          laneID: Long("38"),
          medianInToleranceHeading: 0,
          medianCenterlineDistance: 45.16404434827813,
          expectedHeading: 340.7754196795537,
          segmentID: Long("1"),
          inToleranceEvents: Long("0"),
          distanceFromCenterlineTolerance: 100,
          medianHeading: 34,
          tolerance: 20,
          outOfToleranceEvents: Long("1")
        },
        {
          medianInToleranceCenterlineDistance: 79.96109435475853,
          laneID: Long("12"),
          medianInToleranceHeading: 341.65,
          medianCenterlineDistance: 80.61589410318548,
          expectedHeading: 341.89251341782625,
          segmentID: Long("1"),
          inToleranceEvents: Long("40"),
          distanceFromCenterlineTolerance: 100,
          medianHeading: 341.6,
          tolerance: 20,
          outOfToleranceEvents: Long("1")
        },
        
      ],
      timestamp: Long("1709935047161")
    },
    laneId: Long("12"),
    notificationText: 'Lane Direction of Travel Assessment Notification. The median distance from centerline: 103 cm for segment 2 of lane 12 is not within the allowed tolerance 100.0 cm of the center of the lane.',
    notificationHeading: 'Lane Direction of Travel Assessment',
    segmentId: Long("2"),
    roadRegulatorID: Long("-1"),
    intersectionID: Long("6324"),
    notificationType: 'LaneDirectionOfTravelAssessmentNotification',
    key: 'LaneDirectionOfTravelAssessmentNotification_6324_-1_0_0'
  }
]
```
## CmMapBroadcastRateEvents
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('66088728374b1e1a4f29e7a3'),
    eventGeneratedAt: ISODate('2024-03-30T21:42:00.285Z'),
    roadRegulatorID: Long('-1'),
    timePeriod: {
      beginTimestamp: Long('1711834905000'),
      endTimestamp: Long('1711834915000')
    },
    intersectionID: Long('1234'),
    topicName: 'topic.ProcessedMap',
    eventType: 'MapBroadcastRate',
    source: "{ rsuId='192.168.0.1', intersectionId='1234', region='0'}",
    numberOfMessages: Long('8')
  }
]
```
## CmMapMinimumDataEvents
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('6608791e374b1e1a4ff305a3'),
    missingDataElements: [
      '$.metadata.recordGeneratedBy: is missing but it is required (#/$defs/OdeMapMetadata/required)',
      '$.metadata.receivedMessageDetails.locationData: is missing but it is required (#/$defs/OdeMapMetadata/properties/receivedMessageDetails/required)',
      '$.payload.data.intersections.intersectionGeometry[0].id.region: is missing but it is required (#/$defs/J2735IntersectionReferenceID/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[5].connectsTo.connectsTo[0].signalGroup: is missing but it is required (#/$defs/J2735Connection/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[5].connectsTo.connectsTo[1].signalGroup: is missing but it is required (#/$defs/J2735Connection/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[6].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[6].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[7].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[7].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[8].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[8].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[9].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[9].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[10].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[10].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[11].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[11].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[12].connectsTo.connectsTo[0].signalGroup: is missing but it is required (#/$defs/J2735Connection/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[12].connectsTo.connectsTo[1].signalGroup: is missing but it is required (#/$defs/J2735Connection/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[17].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[17].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[18].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[18].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[19].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[19].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[20].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[20].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[22].connectsTo.connectsTo[0].signalGroup: is missing but it is required (#/$defs/J2735Connection/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[22].connectsTo.connectsTo[2].signalGroup: is missing but it is required (#/$defs/J2735Connection/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[25].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[25].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[26].maneuvers: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[26].connectsTo: is missing but it is required (#/$defs/J2735GenericLane/required)',
      '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[31].connectsTo.connectsTo[0].signalGroup: is missing but it is required (#/$defs/J2735Connection/required)',
      '$.payload.data.intersections.intersectionGeometry[0].speedLimits: is missing but it is required (#/$defs/J2735IntersectionGeometry/required)'
    ],
    eventGeneratedAt: ISODate('2024-03-30T20:42:06.413Z'),
    roadRegulatorID: Long('-1'),
    timePeriod: {
      beginTimestamp: Long('1711831320000'),
      endTimestamp: Long('1711831330000')
    },
    intersectionID: Long('1234'),
    eventType: 'MapMinimumData',
    source: "{ rsuId='192.168.0.1', intersectionId='1234', region='0'}"
  }
]
```
## CmNotification
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { notificationGeneratedAt: 1 },
    name: 'notificationGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, notificationGeneratedAt: -1 },
    name: 'intersectionID_-1_notificationGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: { key: 'SignalGroupAlignmentNotification_0_1234' },
    notificationGeneratedAt: ISODate('2024-05-29T20:42:12.782Z'),
    notificationText: 'Signal Group Alignment Notification, generated because corresponding signal group alignment event was generated.',
    notificationHeading: 'Signal Group Alignment',
    roadRegulatorID: Long('0'),
    intersectionID: Long('1234'),
    notificationType: 'SignalGroupAlignmentNotification',
    event: {
      eventGeneratedAt: Long('1717015332782'),
      roadRegulatorID: Long('0'),
      intersectionID: Long('1234'),
      eventType: 'SignalGroupAlignment',
      source: '{"rsuId":"192.168.0.1","intersectionId":1234}',
      spatSignalGroupIds: [
        Long('1'), Long('2'),
        Long('3'), Long('4'),
        Long('5'), Long('6'),
        Long('7'), Long('8')
      ],
      mapSignalGroupIds: [
        null,      Long('1'),
        Long('2'), Long('3'),
        Long('4'), Long('5'),
        Long('6'), Long('7'),
        Long('8')
      ],
      timestamp: Long('1717015332628')
    },
    key: 'SignalGroupAlignmentNotification_0_1234'
  }
]
```
## CmReport
### Indexes
```javascript
[ { v: 2, key: { _id: 1 }, name: '_id_' } ]
```
### Sample Record
```javascript
[
  {
    _id: 'CmReport_1234_-1_1707523200000_1707609600000',
    intersectionID: 1234,
    roadRegulatorID: '-1',
    reportGeneratedAt: Long('1707627061403'),
    reportStartTime: Long('1707523200000'),
    reportStopTime: Long('1707609600000'),
    reportContents: Binary.createFromBase64('', 0),
    _class: 'us.dot.its.jpo.ode.api.models.ReportDocument'
  }
]
```
## CmSignalGroupAlignmentEvents
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660878f0d6ab450d2cf9250b'),
    eventGeneratedAt: ISODate('2024-03-30T20:41:20.928Z'),
    roadRegulatorID: Long('0'),
    intersectionID: Long('1234'),
    eventType: 'SignalGroupAlignment',
    source: '{"rsuId":"192.168.0.1","intersectionId":1234}',
    spatSignalGroupIds: [
      Long('1'), Long('2'),
      Long('3'), Long('4'),
      Long('5'), Long('6'),
      Long('7'), Long('8')
    ],
    mapSignalGroupIds: [
      null,      Long('1'),
      Long('2'), Long('3'),
      Long('4'), Long('5'),
      Long('6'), Long('7'),
      Long('8')
    ],
    timestamp: Long('1711831280798')
  }
]
```
## CmSignalGroupAlignmentNotification
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { notificationGeneratedAt: 1 },
    name: 'notificationGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, notificationGeneratedAt: -1 },
    name: 'intersectionID_-1_notificationGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660878ee374b1e1a4ff24be4'),
    notificationGeneratedAt: ISODate('2024-03-30T20:41:18.220Z'),
    notificationText: 'Signal Group Alignment Notification, generated because corresponding signal group alignment event was generated.',
    notificationHeading: 'Signal Group Alignment',
    roadRegulatorID: Long('0'),
    intersectionID: Long('1234'),
    notificationType: 'SignalGroupAlignmentNotification',
    event: {
      eventGeneratedAt: Long('1711831278219'),
      roadRegulatorID: Long('0'),
      intersectionID: Long('1234'),
      eventType: 'SignalGroupAlignment',
      source: '{"rsuId":"192.168.0.1","intersectionId":1234}',
      spatSignalGroupIds: [
        Long('1'), Long('2'),
        Long('3'), Long('4'),
        Long('5'), Long('6'),
        Long('7'), Long('8')
      ],
      mapSignalGroupIds: [
        null,      Long('1'),
        Long('2'), Long('3'),
        Long('4'), Long('5'),
        Long('6'), Long('7'),
        Long('8')
      ],
      timestamp: Long('1711831278045')
    },
    key: 'SignalGroupAlignmentNotification_0_12113'
  }
]
```
## CmSignalStateConflictEvents
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('66087919374b1e1a4ff2f3d3'),
    secondConflictingSignalGroup: Long('6'),
    eventGeneratedAt: ISODate('2024-03-30T20:42:01.882Z'),
    roadRegulatorID: Long('-1'),
    intersectionID: Long('1234'),
    conflictType: 'PROTECTED_MOVEMENT_ALLOWED',
    firstConflictingSignalGroup: Long('2'),
    eventType: 'SignalStateConflict',
    source: '{"rsuId":"192.168.0.1","intersectionId":1234}',
    firstConflictingSignalState: 'PROTECTED_MOVEMENT_ALLOWED',
    timestamp: Long('1711831321615'),
    secondConflictingSignalState: 'PROTECTED_MOVEMENT_ALLOWED'
  }
]
```
## CmSignalStateConflictNotification
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { notificationGeneratedAt: 1 },
    name: 'notificationGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, notificationGeneratedAt: -1 },
    name: 'intersectionID_-1_notificationGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('66087918d6ab450d2cf978ee'),
    notificationGeneratedAt: ISODate('2024-03-30T20:42:00.697Z'),
    notificationText: 'Signal State Conflict Notification, generated because corresponding signal state conflict event was generated.',
    notificationHeading: 'Signal State Conflict',
    roadRegulatorID: Long('-1'),
    intersectionID: Long('1234'),
    notificationType: 'SignalStateConflictNotification',
    event: {
      secondConflictingSignalGroup: Long('6'),
      eventGeneratedAt: Long('1711831320697'),
      roadRegulatorID: Long('-1'),
      intersectionID: Long('1234'),
      conflictType: 'PROTECTED_MOVEMENT_ALLOWED',
      firstConflictingSignalGroup: Long('2'),
      eventType: 'SignalStateConflict',
      source: '{"rsuId":"192.168.0.1","intersectionId":1234}',
      firstConflictingSignalState: 'PROTECTED_MOVEMENT_ALLOWED',
      timestamp: Long('1711831320515'),
      secondConflictingSignalState: 'PROTECTED_MOVEMENT_ALLOWED'
    },
    key: 'SignalStateConflictNotification_2_6_1234_-1'
  }
]
```
## CmSignalStateEventAssessment
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { assessmentGeneratedAt: 1 },
    name: 'assessmentGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, assessmentGeneratedAt: -1 },
    name: 'intersectionID_-1_assessmentGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('66230740d6ab450d2cc0c23f'),
    assessmentGeneratedAt: ISODate('2024-04-20T00:07:28.665Z'),
    assessmentType: 'SignalStateEvent',
    roadRegulatorID: Long('-1'),
    intersectionID: Long('1234'),
    source: '{"rsuId":"192.168.0.1","intersectionId":1234,"region":0}',
    signalStateEventAssessmentGroup: [
      {
        darkEvents: Long('0'),
        greenEvents: Long('1'),
        redEvents: Long('0'),
        signalGroup: Long('4'),
        yellowEvents: Long('0')
      }
    ],
    timestamp: Long('1713571648673')
  }
]
```
## CmSpatBroadcastRateEvents
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('66088728374b1e1a4f29e75f'),
    eventGeneratedAt: ISODate('2024-03-30T21:42:00.228Z'),
    roadRegulatorID: Long('-1'),
    timePeriod: {
      beginTimestamp: Long('1711834905000'),
      endTimestamp: Long('1711834915000')
    },
    intersectionID: Long('1234'),
    topicName: 'topic.ProcessedSpat',
    eventType: 'SpatBroadcastRate',
    source: "{ rsuId='192.168.0.1', intersectionId='1234', region='0'}",
    numberOfMessages: Long('83')
  }
]
```
## CmSpatMinimumDataEvents
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('664e586104d4653aaf0d444c'),
    missingDataElements: [
      '$.metadata.receivedMessageDetails.locationData: is missing but it is required (#/$defs/OdeSpatMetadata/properties/receivedMessageDetails/required)',
      '$.metadata.encodings: is missing but it is required (#/$defs/OdeSpatMetadata/required)',
      '$.metadata.recordGeneratedBy: string found, null expected (#/$defs/OdeSpatMetadata/properties/recordGeneratedBy/type)',
      '$.payload.data.timeStamp: is missing but it is required (#/$defs/J2735SPAT/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].id.region: is missing but it is required (#/$defs/J2735IntersectionReferenceID/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[3].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[3].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[4].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[4].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[5].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)',
      '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[5].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required (#/$defs/J2735TimeChangeDetails/required)'
    ],
    eventGeneratedAt: ISODate('2024-05-22T20:41:05.676Z'),
    roadRegulatorID: Long('-1'),
    timePeriod: {
      beginTimestamp: Long('1716410460000'),
      endTimestamp: Long('1716410470000')
    },
    intersectionID: Long('1234'),
    eventType: 'SpatMinimumData',
    source: "{ rsuId='192.168.0.1', intersectionId='1234', region='0'}"
  }
]
```
## CmSpatTimeChangeDetailsEvent
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('6608791d374b1e1a4ff3029a'),
    firstState: 'STOP_AND_REMAIN',
    secondSpatTimestamp: Long('1711831324667'),
    firstTimeMarkType: 'maxEndTime',
    roadRegulatorID: Long('-1'),
    eventType: 'TimeChangeDetails',
    source: '{"rsuId":"192.168.0.1","intersectionId":1234}',
    secondTimeMarkType: 'maxEndTime',
    firstConflictingTimemark: Long('25245'),
    secondState: 'STOP_AND_REMAIN',
    secondConflictingUtcTimestamp: Long('1711831409900'),
    eventGeneratedAt: ISODate('2024-03-30T20:42:05.588Z'),
    firstConflictingUtcTimestamp: Long('1711831324500'),
    firstSpatTimestamp: Long('1711831324566'),
    intersectionID: Long('1234'),
    signalGroup: Long('3'),
    secondConflictingTimemark: Long('26099')
  }
]
```
## CmSpatTimeChangeDetailsNotification
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { notificationGeneratedAt: 1 },
    name: 'notificationGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, notificationGeneratedAt: -1 },
    name: 'intersectionID_-1_notificationGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
No Sample Record Available Yet.
```
## CmStopLinePassageEvent
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660b9799374b1e1a4f304111'),
    heading: 93.6,
    latitude: 39.5530782,
    roadRegulatorID: Long('-1'),
    eventType: 'StopLinePassage',
    source: "{ rsuId='192.168.0.1', intersectionId='1234', region='0'}",
    speed: 3.7,
    egressLane: Long('12'),
    ingressLane: Long('6'),
    eventState: 'PROTECTED_MOVEMENT_ALLOWED',
    eventGeneratedAt: ISODate('2024-04-02T05:28:57.578Z'),
    intersectionID: Long('1234'),
    connectionID: Long('4'),
    vehicleID: 'BE82EAEF',
    signalGroup: Long('4'),
    key: '-1_12115_BE82EAEF',
    timestamp: Long('1712035716700'),
    longitude: -105.0856841
  }
]
```
## CmStopLinePassageNotification
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { notificationGeneratedAt: 1 },
    name: 'notificationGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, notificationGeneratedAt: -1 },
    name: 'intersectionID_-1_notificationGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
No Sample Record Available Yet.
```
## CmStopLineStopAssessment
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { assessmentGeneratedAt: 1 },
    name: 'assessmentGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, assessmentGeneratedAt: -1 },
    name: 'intersectionID_-1_assessmentGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660bb1c2374b1e1a4f97b815'),
    assessmentGeneratedAt: ISODate('2024-04-02T07:20:34.130Z'),
    assessmentType: 'StopLineStop',
    stopLineStopAssessmentGroup: [
      {
        timeStoppedOnYellow: 0,
        timeStoppedOnRed: 12.703,
        timeStoppedOnGreen: 1.898,
        timeStoppedOnDark: 0,
        signalGroup: Long('4'),
        numberOfEvents: Long('1')
      }
    ],
    roadRegulatorID: Long('-1'),
    intersectionID: Long('1234'),
    source: '{"rsuId":"192.168.0.1","intersectionId":1234,"region":0}',
    timestamp: Long('1712042434130')
  }
]
```
## CmStopLineStopEvent
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { eventGeneratedAt: 1 },
    name: 'eventGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, eventGeneratedAt: -1 },
    name: 'intersectionID_-1_eventGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660bb1c2d6ab450d2cd6551f'),
    heading: 95.2,
    latitude: 39.5530612,
    roadRegulatorID: Long('-1'),
    timeStoppedDuringRed: 12.703,
    timeStoppedDuringGreen: 1.898,
    eventType: 'StopLineStop',
    source: "{ rsuId='192.168.0.1', intersectionId='1234', region='0'}",
    initialEventState: 'STOP_AND_REMAIN',
    finalTimestamp: Long('1712042408900'),
    egressLane: Long('-1'),
    ingressLane: Long('5'),
    eventGeneratedAt: ISODate('2024-04-02T07:20:34.052Z'),
    intersectionID: Long('1234'),
    connectionID: Long('-1'),
    vehicleID: 'EF5386DB',
    signalGroup: Long('4'),
    finalEventState: 'PROTECTED_MOVEMENT_ALLOWED',
    timeStoppedDuringYellow: 0,
    key: '-1_1234_EF5386DB',
    initialTimestamp: Long('1712042394300'),
    longitude: -105.0857825
  }
]
```
## CmStopLineStopNotification
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { notificationGeneratedAt: 1 },
    name: 'notificationGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionID: -1, notificationGeneratedAt: -1 },
    name: 'intersectionID_-1_notificationGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: { key: 'StopLineStopNotification_6311_-1_2' },
    notificationGeneratedAt: ISODate("2024-05-13T22:53:52.005Z"),
    assessment: {
      assessmentGeneratedAt: Long("1715640831999"),
      assessmentType: 'StopLineStop',
      stopLineStopAssessmentGroup: [
        {
          timeStoppedOnYellow: 0,
          timeStoppedOnRed: 39.295,
          timeStoppedOnGreen: 14.699000000000002,
          timeStoppedOnDark: 0,
          signalGroup: Long("8"),
          numberOfEvents: Long("3")
        },
        {
          timeStoppedOnYellow: 0,
          timeStoppedOnRed: 157.50400000000002,
          timeStoppedOnGreen: 69.581,
          timeStoppedOnDark: 0,
          signalGroup: Long("4"),
          numberOfEvents: Long("12")
        },
        {
          timeStoppedOnYellow: 0,
          timeStoppedOnRed: 36.099999999999994,
          timeStoppedOnGreen: 3.3019999999999996,
          timeStoppedOnDark: 0,
          signalGroup: Long("7"),
          numberOfEvents: Long("2")
        },
        {
          timeStoppedOnYellow: 0,
          timeStoppedOnRed: 107.10499999999999,
          timeStoppedOnGreen: 24.397000000000002,
          timeStoppedOnDark: 0,
          signalGroup: Long("2"),
          numberOfEvents: Long("10")
        },
        {
          timeStoppedOnYellow: 0,
          timeStoppedOnRed: 27.301000000000002,
          timeStoppedOnGreen: 24.599,
          timeStoppedOnDark: 0,
          signalGroup: Long("6"),
          numberOfEvents: Long("3")
        },
        {
          timeStoppedOnYellow: 1.199,
          timeStoppedOnRed: 37,
          timeStoppedOnGreen: 12.001000000000001,
          timeStoppedOnDark: 0,
          signalGroup: Long("3"),
          numberOfEvents: Long("2")
        }
      ],
      roadRegulatorID: Long("-1"),
      intersectionID: Long("1234"),
      source: '{"rsuId":"192.168.0.1","intersectionId":1234,"region":0}',
      timestamp: Long("1715640831999")
    },
    notificationText: 'Stop Line Stop Notification, percent time stopped on green: 19% for signal group: 2 exceeds maximum allowable percent.',
    notificationHeading: 'Stop Line Stop Notification',
    roadRegulatorID: Long("-1"),
    intersectionID: Long("1234"),
    notificationType: 'StopLineStopNotification',
    signalGroup: Long("2"),
    key: 'StopLineStopNotification_1234_-1_2'
  }
]
```
## OdeBsmJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'metadata.originIp': -1, 'metadata.odeReceivedAt': -1 },
    name: 'metadata.originIp_-1_metadata.odeReceivedAt_-1'
  },
  {
    v: 2,
    key: { 'metadata.odeReceivedAt': -1 },
    name: 'metadata.odeReceivedAt_-1'
  },
  {
    v: 2,
    key: { 'metadata.originIp': 1, recordGeneratedAt: -1 },
    name: 'metadata.originIp_1_recordGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('66096bb3d6ab450d2cfc0a34'),
    metadata: {
      securityResultCode: 'success',
      bsmSource: 'RV',
      schemaVersion: Long('6'),
      odePacketID: '',
      sanitized: false,
      recordType: 'bsmTx',
      recordGeneratedAt: '',
      maxDurationTime: Long('0'),
      odeTimStartDateTime: '',
      receivedMessageDetails: {
        locationData: {
          elevation: '',
          heading: '',
          latitude: '',
          speed: '',
          longitude: ''
        },
        rxSource: 'RV'
      },
      payloadType: 'us.dot.its.jpo.ode.model.OdeBsmPayload',
      serialId: {
        recordId: Long('0'),
        serialNumber: Long('0'),
        streamId: '0c806845-f728-4b3b-894d-68613e2b4daa',
        bundleSize: Long('1'),
        bundleId: Long('0')
      },
      logFileName: '',
      odeReceivedAt: '2024-03-31T13:57:07.910602182Z',
      originIp: '192.168.0.1'
    },
    payload: {
      data: {
        partII: [],
        coreData: {
          secMark: Long('7775'),
          transmission: 'UNAVAILABLE',
          size: { width: Long('180'), length: Long('480') },
          heading: 0,
          accelSet: {
            accelVert: Long('-127'),
            accelLat: Long('2001'),
            accelYaw: 15.06,
            accelLong: 0
          },
          brakes: {
            scs: 'unavailable',
            wheelBrakes: {
              leftFront: false,
              rightFront: false,
              unavailable: true,
              leftRear: false,
              rightRear: false
            },
            abs: 'off',
            traction: 'unavailable',
            brakeBoost: 'unavailable',
            auxBrakes: 'unavailable'
          },
          accuracy: {
            semiMinor: 11.35,
            orientation: 11.1732662286,
            semiMajor: 12.7
          },
          id: '37AAC5C3',
          position: {
            elevation: 1712,
            latitude: 39.5567655,
            longitude: -105.0816261
          },
          msgCnt: Long('81'),
          speed: 0
        }
      },
      dataType: 'us.dot.its.jpo.ode.plugin.j2735.J2735Bsm'
    },
    recordGeneratedAt: ISODate('2024-03-31T13:57:07.974Z')
  }
]
```
## OdeDriverAlertJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'metadata.odeReceivedAt': -1, 'metadata.originIp': -1 },
    name: 'metadata.odeReceivedAt_-1_metadata.originIp_-1'
  },
  {
    v: 2,
    key: { 'metadata.originIp': -1, 'metadata.odeReceivedAt': -1 },
    name: 'metadata.originIp_-1_metadata.odeReceivedAt_-1'
  }
]
```
### Sample Record
```javascript
No Sample Record Available Yet.
```
## OdeMapJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'metadata.originIp': -1, 'metadata.odeReceivedAt': -1 },
    name: 'metadata.originIp_-1_metadata.odeReceivedAt_-1'
  },
  {
    v: 2,
    key: { 'metadata.originIp': 1, recordGeneratedAt: -1 },
    name: 'metadata.originIp_1_recordGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('664e5c68669ddf32bd30d677'),
    metadata: {
      securityResultCode: 'success',
      recordGeneratedBy: 'RSU',
      schemaVersion: Long('6'),
      odePacketID: '',
      sanitized: false,
      recordType: 'mapTx',
      recordGeneratedAt: '',
      maxDurationTime: Long('0'),
      odeTimStartDateTime: '',
      receivedMessageDetails: { rxSource: 'NA' },
      mapSource: 'RSU',
      payloadType: 'us.dot.its.jpo.ode.model.OdeMapPayload',
      serialId: {
        recordId: Long('0'),
        serialNumber: Long('0'),
        streamId: '2cd15e43-7d22-4d44-be56-fc692f06df57',
        bundleSize: Long('1'),
        bundleId: Long('0')
      },
      logFileName: '',
      odeReceivedAt: '2024-05-22T20:58:16.177Z',
      originIp: '192.168.0.1'
    },
    payload: {
      data: {
        layerType: 'intersectionData',
        layerID: Long('0'),
        msgIssueRevision: Long('2'),
        intersections: {
          intersectionGeometry: [
            {
              laneSet: {
                GenericLane: [
                  {
                    connectsTo: {
                      connectsTo: [
                        {
                          connectionID: Long('1'),
                          signalGroup: Long('2'),
                          connectingLane: {
                            lane: Long('15'),
                            maneuver: {
                              maneuverStraightAllowed: true,
                              maneuverNoStoppingAllowed: false,
                              goWithHalt: false,
                              maneuverLeftAllowed: false,
                              maneuverUTurnAllowed: false,
                              maneuverLeftTurnOnRedAllowed: false,
                              reserved1: false,
                              maneuverRightAllowed: false,
                              maneuverLaneChangeAllowed: false,
                              yieldAllwaysRequired: false,
                              maneuverRightTurnOnRedAllowed: false,
                              caution: false
                            }
                          }
                        }
                      ]
                    },
                    laneID: Long('1'),
                    laneAttributes: {
                      directionalUse: { ingressPath: true, egressPath: false },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    ingressApproach: Long('1'),
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY3: { x: Long('1511'), y: Long('-1514') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('723'), y: Long('-3116') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('892'), y: Long('-3818') }
                            },
                            attributes: { dElevation: Long('20') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('702'), y: Long('-2507') }
                            },
                            attributes: { dElevation: Long('20') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('681'), y: Long('-2412') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('827'), y: Long('-2798') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('1176'), y: Long('-3614') }
                            },
                            attributes: { dElevation: Long('20') }
                          },
                          {
                            delta: {
                              nodeXY3: { x: Long('683'), y: Long('-1992') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('1000'), y: Long('-2818') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY3: { x: Long('466'), y: Long('-1295') }
                            },
                            attributes: { dElevation: Long('20') }
                          },
                          {
                            delta: {
                              nodeXY5: { x: Long('1725'), y: Long('-4397') }
                            },
                            attributes: { dElevation: Long('10') }
                          }
                        ]
                      }
                    },
                    maneuvers: {
                      maneuverStraightAllowed: true,
                      maneuverNoStoppingAllowed: false,
                      goWithHalt: false,
                      maneuverLeftAllowed: false,
                      maneuverUTurnAllowed: false,
                      maneuverLeftTurnOnRedAllowed: false,
                      reserved1: false,
                      maneuverRightAllowed: false,
                      maneuverLaneChangeAllowed: false,
                      yieldAllwaysRequired: false,
                      maneuverRightTurnOnRedAllowed: false,
                      caution: false
                    }
                  },
                  {
                    connectsTo: {
                      connectsTo: [
                        {
                          connectionID: Long('1'),
                          signalGroup: Long('2'),
                          connectingLane: {
                            lane: Long('14'),
                            maneuver: {
                              maneuverStraightAllowed: true,
                              maneuverNoStoppingAllowed: false,
                              goWithHalt: false,
                              maneuverLeftAllowed: false,
                              maneuverUTurnAllowed: false,
                              maneuverLeftTurnOnRedAllowed: false,
                              reserved1: false,
                              maneuverRightAllowed: false,
                              maneuverLaneChangeAllowed: false,
                              yieldAllwaysRequired: false,
                              maneuverRightTurnOnRedAllowed: false,
                              caution: false
                            }
                          }
                        }
                      ]
                    },
                    laneID: Long('2'),
                    laneAttributes: {
                      directionalUse: { ingressPath: true, egressPath: false },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    ingressApproach: Long('1'),
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY3: { x: Long('1192'), y: Long('-1619') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('807'), y: Long('-3733') }
                            },
                            attributes: { dElevation: Long('30') }
                          },
                          {
                            delta: {
                              nodeXY5: { x: Long('1010'), y: Long('-4226') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY5: { x: Long('1612'), y: Long('-5477') }
                            },
                            attributes: { dElevation: Long('30') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('1142'), y: Long('-3648') }
                            },
                            attributes: { dElevation: Long('20') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('1131'), y: Long('-3343') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY5: { x: Long('2259'), y: Long('-6170') }
                            },
                            attributes: { dElevation: Long('30') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('897'), y: Long('-2168') }
                            }
                          }
                        ]
                      }
                    },
                    maneuvers: {
                      maneuverStraightAllowed: true,
                      maneuverNoStoppingAllowed: false,
                      goWithHalt: false,
                      maneuverLeftAllowed: false,
                      maneuverUTurnAllowed: false,
                      maneuverLeftTurnOnRedAllowed: false,
                      reserved1: false,
                      maneuverRightAllowed: false,
                      maneuverLaneChangeAllowed: false,
                      yieldAllwaysRequired: false,
                      maneuverRightTurnOnRedAllowed: false,
                      caution: false
                    }
                  },
                  {
                    connectsTo: {
                      connectsTo: [
                        {
                          connectionID: Long('1'),
                          signalGroup: Long('2'),
                          connectingLane: {
                            lane: Long('10'),
                            maneuver: {
                              maneuverStraightAllowed: false,
                              maneuverNoStoppingAllowed: false,
                              goWithHalt: false,
                              maneuverLeftAllowed: true,
                              maneuverUTurnAllowed: false,
                              maneuverLeftTurnOnRedAllowed: false,
                              reserved1: false,
                              maneuverRightAllowed: false,
                              maneuverLaneChangeAllowed: false,
                              yieldAllwaysRequired: false,
                              maneuverRightTurnOnRedAllowed: false,
                              caution: false
                            }
                          }
                        }
                      ]
                    },
                    laneID: Long('3'),
                    laneAttributes: {
                      directionalUse: { ingressPath: true, egressPath: false },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    ingressApproach: Long('1'),
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY3: { x: Long('805'), y: Long('-1704') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY3: { x: Long('380'), y: Long('-1813') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('832'), y: Long('-3451') }
                            },
                            attributes: { dElevation: Long('30') }
                          },
                          {
                            delta: {
                              nodeXY2: { x: Long('202'), y: Long('-872') }
                            }
                          },
                          {
                            delta: {
                              nodeXY1: { x: Long('340'), y: Long('-482') }
                            },
                            attributes: { dElevation: Long('-10') }
                          }
                        ]
                      }
                    },
                    maneuvers: {
                      maneuverStraightAllowed: false,
                      maneuverNoStoppingAllowed: false,
                      goWithHalt: false,
                      maneuverLeftAllowed: true,
                      maneuverUTurnAllowed: false,
                      maneuverLeftTurnOnRedAllowed: false,
                      reserved1: false,
                      maneuverRightAllowed: false,
                      maneuverLaneChangeAllowed: false,
                      yieldAllwaysRequired: false,
                      maneuverRightTurnOnRedAllowed: false,
                      caution: false
                    }
                  },
                  {
                    laneID: Long('6'),
                    egressApproach: Long('2'),
                    laneAttributes: {
                      directionalUse: { ingressPath: false, egressPath: true },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY4: { x: Long('-988'), y: Long('-2151') }
                            },
                            attributes: { dElevation: Long('20') }
                          },
                          {
                            delta: {
                              nodeXY1: { x: Long('69'), y: Long('-329') }
                            }
                          }
                        ]
                      }
                    }
                  },
                  {
                    laneID: Long('5'),
                    egressApproach: Long('2'),
                    laneAttributes: {
                      directionalUse: { ingressPath: false, egressPath: true },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY4: { x: Long('-630'), y: Long('-2062') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY1: { x: Long('76'), y: Long('-377') }
                            },
                            attributes: { dElevation: Long('10') }
                          }
                        ]
                      }
                    }
                  },
                  {
                    laneID: Long('4'),
                    egressApproach: Long('2'),
                    laneAttributes: {
                      directionalUse: { ingressPath: false, egressPath: true },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY3: { x: Long('-245'), y: Long('-2001') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY1: { x: Long('76'), y: Long('-349') }
                            }
                          }
                        ]
                      }
                    }
                  },
                  {
                    laneID: Long('10'),
                    egressApproach: Long('4'),
                    laneAttributes: {
                      directionalUse: { ingressPath: false, egressPath: true },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY4: { x: Long('-2374'), y: Long('232') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY1: { x: Long('-357'), y: Long('-96') }
                            }
                          }
                        ]
                      }
                    }
                  },
                  {
                    connectsTo: {
                      connectsTo: [
                        {
                          connectionID: Long('1'),
                          signalGroup: Long('4'),
                          connectingLane: {
                            lane: Long('15'),
                            maneuver: {
                              maneuverStraightAllowed: false,
                              maneuverNoStoppingAllowed: false,
                              goWithHalt: false,
                              maneuverLeftAllowed: true,
                              maneuverUTurnAllowed: false,
                              maneuverLeftTurnOnRedAllowed: false,
                              reserved1: false,
                              maneuverRightAllowed: false,
                              maneuverLaneChangeAllowed: false,
                              yieldAllwaysRequired: false,
                              maneuverRightTurnOnRedAllowed: false,
                              caution: false
                            }
                          }
                        }
                      ]
                    },
                    laneID: Long('8'),
                    laneAttributes: {
                      directionalUse: { ingressPath: true, egressPath: false },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    ingressApproach: Long('3'),
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY4: { x: Long('-2246'), y: Long('-514') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-2644'), y: Long('-581') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-2887'), y: Long('-442') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-3583'), y: Long('-339') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-3757'), y: Long('27') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-3249'), y: Long('361') }
                            },
                            attributes: { dElevation: Long('-10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-2050'), y: Long('478') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-3893'), y: Long('1184') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-2625'), y: Long('766') }
                            },
                            attributes: { dElevation: Long('-10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-2524'), y: Long('607') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-2280'), y: Long('325') }
                            },
                            attributes: { dElevation: Long('10') }
                          }
                        ]
                      }
                    },
                    maneuvers: {
                      maneuverStraightAllowed: false,
                      maneuverNoStoppingAllowed: false,
                      goWithHalt: false,
                      maneuverLeftAllowed: true,
                      maneuverUTurnAllowed: false,
                      maneuverLeftTurnOnRedAllowed: false,
                      reserved1: false,
                      maneuverRightAllowed: false,
                      maneuverLaneChangeAllowed: false,
                      yieldAllwaysRequired: false,
                      maneuverRightTurnOnRedAllowed: false,
                      caution: false
                    }
                  },
                  {
                    connectsTo: {
                      connectsTo: [
                        {
                          connectionID: Long('1'),
                          connectingLane: {
                            lane: Long('6'),
                            maneuver: {
                              maneuverStraightAllowed: false,
                              maneuverNoStoppingAllowed: false,
                              goWithHalt: false,
                              maneuverLeftAllowed: false,
                              maneuverUTurnAllowed: false,
                              maneuverLeftTurnOnRedAllowed: false,
                              reserved1: false,
                              maneuverRightAllowed: true,
                              maneuverLaneChangeAllowed: false,
                              yieldAllwaysRequired: false,
                              maneuverRightTurnOnRedAllowed: false,
                              caution: false
                            }
                          }
                        }
                      ]
                    },
                    laneID: Long('7'),
                    laneAttributes: {
                      directionalUse: { ingressPath: true, egressPath: false },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    ingressApproach: Long('3'),
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY4: { x: Long('-2216'), y: Long('-915') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-2322'), y: Long('-471') }
                            }
                          },
                          {
                            delta: {
                              nodeXY3: { x: Long('-1877'), y: Long('-349') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY3: { x: Long('-1787'), y: Long('-235') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-2666'), y: Long('-102') }
                            },
                            attributes: { dElevation: Long('10') }
                          }
                        ]
                      }
                    },
                    maneuvers: {
                      maneuverStraightAllowed: false,
                      maneuverNoStoppingAllowed: false,
                      goWithHalt: false,
                      maneuverLeftAllowed: false,
                      maneuverUTurnAllowed: false,
                      maneuverLeftTurnOnRedAllowed: false,
                      reserved1: false,
                      maneuverRightAllowed: true,
                      maneuverLaneChangeAllowed: false,
                      yieldAllwaysRequired: false,
                      maneuverRightTurnOnRedAllowed: false,
                      caution: false
                    }
                  },
                  {
                    connectsTo: {
                      connectsTo: [
                        {
                          connectionID: Long('1'),
                          signalGroup: Long('4'),
                          connectingLane: {
                            lane: Long('14'),
                            maneuver: {
                              maneuverStraightAllowed: false,
                              maneuverNoStoppingAllowed: false,
                              goWithHalt: false,
                              maneuverLeftAllowed: true,
                              maneuverUTurnAllowed: false,
                              maneuverLeftTurnOnRedAllowed: false,
                              reserved1: false,
                              maneuverRightAllowed: false,
                              maneuverLaneChangeAllowed: false,
                              yieldAllwaysRequired: false,
                              maneuverRightTurnOnRedAllowed: false,
                              caution: false
                            }
                          }
                        }
                      ]
                    },
                    laneID: Long('9'),
                    laneAttributes: {
                      directionalUse: { ingressPath: true, egressPath: false },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    ingressApproach: Long('3'),
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY4: { x: Long('-2295'), y: Long('-169') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-2420'), y: Long('-499') }
                            }
                          },
                          {
                            delta: {
                              nodeXY3: { x: Long('-1968'), y: Long('-339') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY3: { x: Long('-1843'), y: Long('-256') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-2121'), y: Long('-339') }
                            }
                          }
                        ]
                      }
                    },
                    maneuvers: {
                      maneuverStraightAllowed: false,
                      maneuverNoStoppingAllowed: false,
                      goWithHalt: false,
                      maneuverLeftAllowed: true,
                      maneuverUTurnAllowed: false,
                      maneuverLeftTurnOnRedAllowed: false,
                      reserved1: false,
                      maneuverRightAllowed: false,
                      maneuverLaneChangeAllowed: false,
                      yieldAllwaysRequired: false,
                      maneuverRightTurnOnRedAllowed: false,
                      caution: false
                    }
                  },
                  {
                    connectsTo: {
                      connectsTo: [
                        {
                          connectionID: Long('1'),
                          signalGroup: Long('6'),
                          connectingLane: {
                            lane: Long('5'),
                            maneuver: {
                              maneuverStraightAllowed: true,
                              maneuverNoStoppingAllowed: false,
                              goWithHalt: false,
                              maneuverLeftAllowed: false,
                              maneuverUTurnAllowed: false,
                              maneuverLeftTurnOnRedAllowed: false,
                              reserved1: false,
                              maneuverRightAllowed: false,
                              maneuverLaneChangeAllowed: false,
                              yieldAllwaysRequired: false,
                              maneuverRightTurnOnRedAllowed: false,
                              caution: false
                            }
                          }
                        }
                      ]
                    },
                    laneID: Long('12'),
                    laneAttributes: {
                      directionalUse: { ingressPath: true, egressPath: false },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    ingressApproach: Long('5'),
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY3: { x: Long('-1364'), y: Long('1705') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY5: { x: Long('-885'), y: Long('4854') }
                            },
                            attributes: { dElevation: Long('-30') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-410'), y: Long('2559') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY3: { x: Long('-333'), y: Long('1847') }
                            },
                            attributes: { dElevation: Long('-10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-319'), y: Long('3244') }
                            },
                            attributes: { dElevation: Long('-20') }
                          },
                          {
                            delta: {
                              nodeXY5: { x: Long('-500'), y: Long('4510') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-333'), y: Long('3403') }
                            },
                            attributes: { dElevation: Long('-30') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-268'), y: Long('2982') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-174'), y: Long('2213') }
                            },
                            attributes: { dElevation: Long('-10') }
                          }
                        ]
                      }
                    },
                    maneuvers: {
                      maneuverStraightAllowed: true,
                      maneuverNoStoppingAllowed: false,
                      goWithHalt: false,
                      maneuverLeftAllowed: false,
                      maneuverUTurnAllowed: false,
                      maneuverLeftTurnOnRedAllowed: false,
                      reserved1: false,
                      maneuverRightAllowed: false,
                      maneuverLaneChangeAllowed: false,
                      yieldAllwaysRequired: false,
                      maneuverRightTurnOnRedAllowed: false,
                      caution: false
                    }
                  },
                  {
                    connectsTo: {
                      connectsTo: [
                        {
                          connectionID: Long('1'),
                          signalGroup: Long('6'),
                          connectingLane: {
                            lane: Long('4'),
                            maneuver: {
                              maneuverStraightAllowed: true,
                              maneuverNoStoppingAllowed: false,
                              goWithHalt: false,
                              maneuverLeftAllowed: false,
                              maneuverUTurnAllowed: false,
                              maneuverLeftTurnOnRedAllowed: false,
                              reserved1: false,
                              maneuverRightAllowed: false,
                              maneuverLaneChangeAllowed: false,
                              yieldAllwaysRequired: false,
                              maneuverRightTurnOnRedAllowed: false,
                              caution: false
                            }
                          }
                        }
                      ]
                    },
                    laneID: Long('13'),
                    laneAttributes: {
                      directionalUse: { ingressPath: true, egressPath: false },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    ingressApproach: Long('5'),
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY3: { x: Long('-992'), y: Long('1735') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY5: { x: Long('-896'), y: Long('4816') }
                            },
                            attributes: { dElevation: Long('-30') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-326'), y: Long('2227') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-333'), y: Long('2366') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-410'), y: Long('3030') }
                            },
                            attributes: { dElevation: Long('-20') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-326'), y: Long('3009') }
                            },
                            attributes: { dElevation: Long('-10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-271'), y: Long('2518') }
                            },
                            attributes: { dElevation: Long('-10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-264'), y: Long('2857') }
                            },
                            attributes: { dElevation: Long('-20') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-243'), y: Long('2504') }
                            }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-153'), y: Long('2289') }
                            },
                            attributes: { dElevation: Long('-10') }
                          }
                        ]
                      }
                    },
                    maneuvers: {
                      maneuverStraightAllowed: true,
                      maneuverNoStoppingAllowed: false,
                      goWithHalt: false,
                      maneuverLeftAllowed: false,
                      maneuverUTurnAllowed: false,
                      maneuverLeftTurnOnRedAllowed: false,
                      reserved1: false,
                      maneuverRightAllowed: false,
                      maneuverLaneChangeAllowed: false,
                      yieldAllwaysRequired: false,
                      maneuverRightTurnOnRedAllowed: false,
                      caution: false
                    }
                  },
                  {
                    connectsTo: {
                      connectsTo: [
                        {
                          connectionID: Long('1'),
                          connectingLane: {
                            lane: Long('10'),
                            maneuver: {
                              maneuverStraightAllowed: false,
                              maneuverNoStoppingAllowed: false,
                              goWithHalt: false,
                              maneuverLeftAllowed: false,
                              maneuverUTurnAllowed: false,
                              maneuverLeftTurnOnRedAllowed: false,
                              reserved1: false,
                              maneuverRightAllowed: true,
                              maneuverLaneChangeAllowed: false,
                              yieldAllwaysRequired: false,
                              maneuverRightTurnOnRedAllowed: false,
                              caution: false
                            }
                          }
                        }
                      ]
                    },
                    laneID: Long('11'),
                    laneAttributes: {
                      directionalUse: { ingressPath: true, egressPath: false },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    ingressApproach: Long('5'),
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY3: { x: Long('-1744'), y: Long('1607') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-563'), y: Long('3136') }
                            },
                            attributes: { dElevation: Long('-20') }
                          },
                          {
                            delta: {
                              nodeXY4: { x: Long('-352'), y: Long('2336') }
                            },
                            attributes: { dElevation: Long('-10') }
                          },
                          {
                            delta: {
                              nodeXY3: { x: Long('-223'), y: Long('1407') }
                            },
                            attributes: { dElevation: Long('10') }
                          },
                          {
                            delta: {
                              nodeXY3: { x: Long('-155'), y: Long('1778') }
                            }
                          }
                        ]
                      }
                    },
                    maneuvers: {
                      maneuverStraightAllowed: false,
                      maneuverNoStoppingAllowed: false,
                      goWithHalt: false,
                      maneuverLeftAllowed: false,
                      maneuverUTurnAllowed: false,
                      maneuverLeftTurnOnRedAllowed: false,
                      reserved1: false,
                      maneuverRightAllowed: true,
                      maneuverLaneChangeAllowed: false,
                      yieldAllwaysRequired: false,
                      maneuverRightTurnOnRedAllowed: false,
                      caution: false
                    }
                  },
                  {
                    laneID: Long('14'),
                    egressApproach: Long('6'),
                    laneAttributes: {
                      directionalUse: { ingressPath: false, egressPath: true },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY3: { x: Long('398'), y: Long('1931') }
                            },
                            attributes: { dElevation: Long('-10') }
                          },
                          {
                            delta: {
                              nodeXY1: { x: Long('-76'), y: Long('356') }
                            }
                          }
                        ]
                      }
                    }
                  },
                  {
                    laneID: Long('15'),
                    egressApproach: Long('6'),
                    laneAttributes: {
                      directionalUse: { ingressPath: false, egressPath: true },
                      shareWith: {
                        busVehicleTraffic: false,
                        trackedVehicleTraffic: false,
                        individualMotorizedVehicleTraffic: false,
                        taxiVehicleTraffic: false,
                        overlappingLaneDescriptionProvided: false,
                        cyclistVehicleTraffic: false,
                        otherNonMotorizedTrafficTypes: false,
                        multipleLanesTreatedAsOneLane: false,
                        pedestrianTraffic: false,
                        pedestriansTraffic: false
                      },
                      laneType: {
                        vehicle: {
                          isVehicleRevocableLane: false,
                          isVehicleFlyOverLane: false,
                          permissionOnRequest: false,
                          hasIRbeaconCoverage: false,
                          restrictedToBusUse: false,
                          restrictedToTaxiUse: false,
                          restrictedFromPublicUse: false,
                          hovLaneUseOnly: false
                        }
                      }
                    },
                    nodeList: {
                      nodes: {
                        NodeXY: [
                          {
                            delta: {
                              nodeXY3: { x: Long('838'), y: Long('1985') }
                            },
                            attributes: { dElevation: Long('-20') }
                          },
                          {
                            delta: {
                              nodeXY1: { x: Long('-89'), y: Long('349') }
                            }
                          }
                        ]
                      }
                    }
                  }
                ]
              },
              refPoint: {
                elevation: 1691,
                latitude: 39.5880413,
                longitude: -105.0908854
              },
              laneWidth: Long('366'),
              id: { id: Long('12109') },
              revision: Long('2')
            }
          ]
        }
      },
      dataType: 'us.dot.its.jpo.ode.plugin.j2735.J2735MAP'
    },
    recordGeneratedAt: ISODate('2024-05-22T20:58:16.216Z')
  }
]
```
## OdeRawEncodedBSMJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: {
      'BsmMessageContent.metadata.originRsu': -1,
      'BsmMessageContent.metadata.utctimestamp': -1
    },
    name: 'BsmMessageContent.metadata.originRsu_-1_BsmMessageContent.metadata.utctimestamp_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('66096bb3d6ab450d2cfc0a13'),
    recordGeneratedAt: ISODate('2024-03-31T13:57:07.913Z'),
    BsmMessageContent: [
      {
        metadata: {
          originRsu: '192.168.0.1',
          utctimestamp: '2024-03-31T13:57:07.910602182Z'
        },
        payload: '001435544deab170c797e69c65939653d17d29707f7183f970000000fd7d0fa10085e180805a0f000038c0010000400010000012fffec8002b00006f20ffb33f53f0121106100573ee85012144e0ffb5bee410101aca0ffbbbedd9001fffc10646bf095029fffc10bb2bf05f085fffcfffec80416c0f741ec40ffb341736f5e20a60ffa7c1706f4623d80ff91c14b4f4e2b820ffb640cb6f4e3082100a6408f0f3c36d6fffec803f172f99fffcfffec8002000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000'
      }
    ]
  }
]
```
## OdeRawEncodedMAPJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: {
      'MapMessageContent.metadata.originRsu': -1,
      'MapMessageContent.metadata.utctimestamp': -1
    },
    name: 'MapMessageContent.metadata.originRsu_-1_MapMessageContent.metadata.utctimestamp_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('6608791bd6ab450d2cf97e07'),
    MapMessageContent: [
      {
        metadata: {
          originRsu: '192.168.0.1',
          utctimestamp: '2024-03-30T20:42:03.152112329Z',
          source: 'RSU'
        },
        payload: '0012834c380330002044d80d4d3e9d432cd21a6e52b802dc34580228000004000035e115e20009ec69af0402a813ec0b1880002012c021400000200005af064f3d604f6349d0a022c09f6693f04035813d80b1780002012c031400000200005af0b6f6b004fb34a0b6029009ec693784012813d80b1680002012c041400000200005af08ef99404fb349c8601e809ec6940f4027813d80b1580002012c051400000200005af062fc5204fb34994601e009ec69489c020013d80b14800020110082200000000af1050d8c57e3c800010072200000000af0eb0ac0d7e3c7ff4027d840188800000002bc37c1e135f952001809f62c0d440000010001dafee3155805051600561a54b002b18d45800089c82bfeec388b5ff7617000a0a2bfa6423095fad211d8afd57098a0b1540004012c0c4400000300001afc03155e050516005a3850562c8000802b1a80004022c0e4400000100011b01b3154205051601968201affc70c82050515fe36122cafdfb0a6e57d9d874b058a20002009607aa00000100002d894f868802851bb15d0058054137663dff2c0a320584400030096082a00000100002d893f851902851bb401007005413760f1fe8c0a320583c0003009608aa00000100002d895283c502851bb1e7005005413764adff2c0a320583400030096092a00000080000d893f825402851bd59effe6054602c690000404b04d5000000400006c4a9c05d01428dcb868010029901632a0002022028c400000003626f1f9080a142c0d2bffd080ab100000000d899d7cab02850b033effe8202cc400000003625ddecb00a142c0e5bffd080bb100000000d896579d902850b03d2ffe82030c400000003625ade1cc0a142c0f4c000080d3900000000d7f47745f027d8b0000fbf42032e400000003602d5d19409f62c0033f0010049a00000008d6e138c2d027d9b08d3081c050516105a1221b08630dc405051609ee174cb03310cf04014680000003b5b3d2365409f66c297c2558141458333838f6c1e94307014145828885db2c0d4c3ae96024a1e59aff1d0d18050535f36a296809f6200b340000001dad81d1d9804fb3612f610f80a0a2c18441b4160f2217c9b04dd0b7a05051607ae1bd8b01150f0e57f5f866c2be78c51c100dc200000002d6aaa740902788af74b04ccd7a7c8195028280540000020000db05b909de04fb365c8610c40a0a2c710c0ee1625da03c1b1c5f01f6050502c160001804b0415000000800032c19041bf1637fe0aacb24ff05a658e2181806c714c07c814140b04800060110147200000001b411efe4405051603ca005c00000080011b13f6edb804f115fa6dfc10afc94fc6a57ee07dce2bfa7bed3b5ffcd72e80a1e018c9000082da5018006028281442020200000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000'
      }
    ],
    recordGeneratedAt: ISODate('2024-03-30T20:42:03.152Z')
  }
]
```
## OdeRawEncodedSPATJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: {
      'SpatMessageContent.metadata.originRsu': -1,
      'SpatMessageContent.metadata.utctimestamp': -1
    },
    name: 'SpatMessageContent.metadata.originRsu_-1_SpatMessageContent.metadata.utctimestamp_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('664e586b04d4653aaf0d6dba'),
    metadata: {
      securityResultCode: 'success',
      recordGeneratedBy: 'RSU',
      schemaVersion: Long('6'),
      spatSource: 'RSU',
      payloadType: 'us.dot.its.jpo.ode.model.OdeAsn1Payload',
      serialId: {
        recordId: Long('0'),
        serialNumber: Long('0'),
        streamId: '478fe598-4024-44a2-816a-71fa70192962',
        bundleSize: Long('1'),
        bundleId: Long('0')
      },
      sanitized: false,
      recordType: 'spatTx',
      maxDurationTime: Long('0'),
      isCertPresent: false,
      odeReceivedAt: '2024-05-22T20:41:15.810Z',
      originIp: '192.168.0.1'
    },
    payload: {
      data: {
        bytes: '001347000811318000003D3B07001043432B332B3001023218AE58AE400C10D0C5ECC5EC008086864226422005043432893289003023218AE58AE401C10D0CACCCACC010086863FA63FA000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000'
      },
      dataType: 'us.dot.its.jpo.ode.model.OdeHexByteArray'
    },
    recordGeneratedAt: ISODate('2024-05-22T20:41:15.810Z')
  }
]
```
## OdeRawEncodedSRMJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: {
      'SpatMessageContent.metadata.originRsu': -1,
      'SrmMessageContent.metadata.utctimestamp': -1
    },
    name: 'SpatMessageContent.metadata.originRsu_-1_SrmMessageContent.metadata.utctimestamp_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660b8f50374b1e1a4f0f7965'),
    recordGeneratedAt: ISODate('2024-04-02T04:53:36.138Z'),
    SrmMessageContent: [
      {
        metadata: {
          originRsu: '192.168.0.1',
          utctimestamp: '2024-04-02T04:53:36.135064864Z',
          source: 'RSU'
        },
        payload: '001d267103528000010090bd480480c00fb318c0b9c1ff83800b534e3bd1cb29d9fd946522a470990076f1a521b8481c15609dc7ed4871c1470ff8a79c81519a05c1c5e631575395040e587097000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000'
      }
    ]
  }
]
```
## OdeRawEncodedSSMJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: {
      'SpatMessageContent.metadata.originRsu': -1,
      'SsmMessageContent.metadata.utctimestamp': -1
    },
    name: 'SpatMessageContent.metadata.originRsu_-1_SsmMessageContent.metadata.utctimestamp_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660b8f50d6ab450d2c876f88'),
    SsmMessageContent: [
      {
        metadata: {
          originRsu: '192.168.0.1',
          utctimestamp: '2024-04-02T04:53:36.462641087Z',
          source: 'RSU'
        },
        payload: '001e1862053d8ca04a03b8bd480c95ce0ffc1c0408203003ecc61025791b30a66a02080b0106efa8f236614cb684102a01d29650e200589de02b22a7808020345af280d8d242f46481006064b9290000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000'
      }
    ],
    recordGeneratedAt: ISODate('2024-04-02T04:53:36.467Z')
  }
]
```
## OdeRawEncodedTIMJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: {
      'SpatMessageContent.metadata.originRsu': -1,
      'TimMessageContent.metadata.utctimestamp': -1
    },
    name: 'SpatMessageContent.metadata.originRsu_-1_TimMessageContent.metadata.utctimestamp_-1'
  }
]
```
### Sample Record
```javascript
No Sample Record Available Yet.
```
## OdeSpatJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'metadata.originIp': -1, 'metadata.odeReceivedAt': -1 },
    name: 'metadata.originIp_-1_metadata.odeReceivedAt_-1'
  },
  {
    v: 2,
    key: { 'metadata.originIp': 1, recordGeneratedAt: -1 },
    name: 'metadata.originIp_1_recordGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('664e5865669ddf32bd27eb34'),
    metadata: {
      securityResultCode: 'success',
      recordGeneratedBy: 'RSU',
      schemaVersion: Long('6'),
      spatSource: 'RSU',
      odePacketID: '',
      sanitized: false,
      recordType: 'spatTx',
      recordGeneratedAt: '',
      maxDurationTime: Long('0'),
      odeTimStartDateTime: '',
      isCertPresent: false,
      receivedMessageDetails: { rxSource: 'NA' },
      payloadType: 'us.dot.its.jpo.ode.model.OdeSpatPayload',
      serialId: {
        recordId: Long('0'),
        serialNumber: Long('0'),
        streamId: 'fb9125e4-d610-4fd9-b9f7-26fd51648ac2',
        bundleSize: Long('1'),
        bundleId: Long('0')
      },
      logFileName: '',
      odeReceivedAt: '2024-05-22T20:41:09.907Z',
      originIp: '192.168.0.1'
    },
    payload: {
      data: {
        intersectionStateList: {
          intersectionStatelist: [
            {
              timeStamp: Long('9900'),
              id: { id: Long('12102') },
              revision: Long('0'),
              status: {
                failureFlash: false,
                noValidSPATisAvailableAtThisTime: false,
                fixedTimeOperation: false,
                standbyOperation: false,
                trafficDependentOperation: false,
                manualControlIsEnabled: false,
                off: false,
                stopTimeIsActivated: false,
                recentChangeInMAPassignedLanesIDsUsed: false,
                recentMAPmessageUpdate: false,
                failureMode: false,
                noValidMAPisAvailableAtThisTime: false,
                signalPriorityIsActive: false,
                preemptIsActive: false
              },
              states: {
                movementList: [
                  {
                    state_time_speed: {
                      movementEventList: [
                        {
                          eventState: 'STOP_AND_REMAIN',
                          timing: {
                            minEndTime: Long('24942'),
                            maxEndTime: Long('24942')
                          }
                        }
                      ]
                    },
                    signalGroup: Long('1')
                  },
                  {
                    state_time_speed: {
                      movementEventList: [
                        {
                          eventState: 'STOP_AND_REMAIN',
                          timing: {
                            minEndTime: Long('25092'),
                            maxEndTime: Long('25092')
                          }
                        }
                      ]
                    },
                    signalGroup: Long('2')
                  },
                  {
                    state_time_speed: {
                      movementEventList: [
                        {
                          eventState: 'STOP_AND_REMAIN',
                          timing: {
                            minEndTime: Long('24699'),
                            maxEndTime: Long('24699')
                          }
                        }
                      ]
                    },
                    signalGroup: Long('3')
                  },
                  {
                    state_time_speed: {
                      movementEventList: [
                        {
                          eventState: 'PROTECTED_MOVEMENT_ALLOWED',
                          timing: {
                            minEndTime: Long('24699'),
                            maxEndTime: Long('24712')
                          }
                        }
                      ]
                    },
                    signalGroup: Long('4')
                  },
                  {
                    state_time_speed: {
                      movementEventList: [
                        {
                          eventState: 'STOP_AND_REMAIN',
                          timing: {
                            minEndTime: Long('24699'),
                            maxEndTime: Long('24699')
                          }
                        }
                      ]
                    },
                    signalGroup: Long('5')
                  },
                  {
                    state_time_speed: {
                      movementEventList: [
                        {
                          eventState: 'STOP_AND_REMAIN',
                          timing: {
                            minEndTime: Long('24942'),
                            maxEndTime: Long('24942')
                          }
                        }
                      ]
                    },
                    signalGroup: Long('6')
                  },
                  {
                    state_time_speed: {
                      movementEventList: [
                        {
                          eventState: 'PROTECTED_MOVEMENT_ALLOWED',
                          timing: {
                            minEndTime: Long('24706'),
                            maxEndTime: Long('24712')
                          }
                        }
                      ]
                    },
                    signalGroup: Long('7')
                  },
                  {
                    state_time_speed: {
                      movementEventList: [
                        {
                          eventState: 'STOP_AND_REMAIN',
                          timing: {
                            minEndTime: Long('24762'),
                            maxEndTime: Long('24762')
                          }
                        }
                      ]
                    },
                    signalGroup: Long('8')
                  }
                ]
              }
            }
          ]
        }
      },
      dataType: 'us.dot.its.jpo.ode.plugin.j2735.J2735SPAT'
    },
    recordGeneratedAt: ISODate('2024-05-22T20:41:09.923Z')
  }
]
```
## OdeSpatRxJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'metadata.originIp': -1, 'metadata.odeReceivedAt': -1 },
    name: 'metadata.originIp_-1_metadata.odeReceivedAt_-1'
  }
]
```
### Sample Record
```javascript
No Sample Record Available Yet.
```
## OdeSrmJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'metadata.originIp': -1, 'metadata.odeReceivedAt': -1 },
    name: 'metadata.originIp_-1_metadata.odeReceivedAt_-1'
  },
  {
    v: 2,
    key: { 'metadata.originIp': 1, recordGeneratedAt: -1 },
    name: 'metadata.originIp_1_recordGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660b8f50374b1e1a4f0f7ab3'),
    metadata: {
      schemaVersion: Long('6'),
      odePacketID: '',
      sanitized: false,
      recordType: 'srmTx',
      recordGeneratedAt: '',
      srmSource: 'RSU',
      maxDurationTime: Long('0'),
      odeTimStartDateTime: '',
      receivedMessageDetails: { rxSource: 'NA' },
      payloadType: 'us.dot.its.jpo.ode.model.OdeSrmPayload',
      serialId: {
        recordId: Long('0'),
        serialNumber: Long('0'),
        streamId: '3c660a70-a9ce-4a10-8bf1-c38771bce42b',
        bundleSize: Long('1'),
        bundleId: Long('0')
      },
      logFileName: '',
      odeReceivedAt: '2024-04-02T04:53:36.144067650Z',
      originIp: '192.168.0.1'
    },
    payload: {
      data: {
        timeStamp: Long('132773'),
        sequenceNumber: Long('1'),
        requests: {
          signalRequestPackage: [
            {
              duration: Long('55692'),
              request: {
                outBoundLane: { lane: Long('7') },
                inBoundLane: { lane: Long('12') },
                requestType: 'priorityRequest',
                requestID: Long('1'),
                id: { id: Long('12114') }
              }
            }
          ]
        },
        requestor: {
          id: { stationID: Long('1938030343') },
          position: {
            heading: 221.7,
            position: {
              elevation: 1678.8,
              latitude: 39.5576903,
              longitude: -105.0831369
            },
            speed: { transmisson: 'UNAVAILABLE', speed: 6.12 }
          },
          type: { role: 'publicTransport' }
        },
        second: Long('0')
      },
      dataType: 'us.dot.its.jpo.ode.plugin.j2735.J2735SRM'
    },
    recordGeneratedAt: ISODate('2024-04-02T04:53:36.506Z')
  }
]
```
## OdeSsmJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'metadata.originIp': -1, 'metadata.odeReceivedAt': -1 },
    name: 'metadata.originIp_-1_metadata.odeReceivedAt_-1'
  },
  {
    v: 2,
    key: { 'metadata.originIp': 1, recordGeneratedAt: -1 },
    name: 'metadata.originIp_1_recordGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('660b8f50d6ab450d2c877072'),
    metadata: {
      schemaVersion: Long('6'),
      odePacketID: '',
      sanitized: false,
      recordType: 'ssmTx',
      recordGeneratedAt: '',
      ssmSource: 'RSU',
      maxDurationTime: Long('0'),
      odeTimStartDateTime: '',
      receivedMessageDetails: { rxSource: 'NA' },
      payloadType: 'us.dot.its.jpo.ode.model.OdeSsmPayload',
      serialId: {
        recordId: Long('0'),
        serialNumber: Long('0'),
        streamId: '1d69d5ec-0184-4ef7-b63e-fcbeada12dcc',
        bundleSize: Long('1'),
        bundleId: Long('0')
      },
      logFileName: '',
      odeReceivedAt: '2024-04-02T04:53:36.462641087Z',
      originIp: '192.168.0.1'
    },
    payload: {
      data: {
        timeStamp: Long('132413'),
        second: Long('37'),
        status: {
          signalStatus: [
            {
              sequenceNumber: Long('119'),
              id: { id: Long('12114') },
              sigStatus: {
                signalStatusPackage: [
                  {
                    requester: {
                      request: Long('1'),
                      sequenceNumber: Long('1'),
                      role: 'publicTransport',
                      id: { stationID: Long('1938030343') }
                    },
                    duration: Long('55692'),
                    outboundOn: { lane: Long('7') },
                    inboundOn: { lane: Long('12') },
                    status: 'processing'
                  }
                ]
              }
            }
          ]
        }
      },
      dataType: 'us.dot.its.jpo.ode.plugin.j2735.J2735SSM'
    },
    recordGeneratedAt: ISODate('2024-04-02T04:53:36.873Z')
  }
]
```
## OdeTIMCertExpirationTimeJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'metadata.originIp': -1, 'metadata.odeReceivedAt': -1 },
    name: 'metadata.originIp_-1_metadata.odeReceivedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('664e1e84669ddf32bda67e77'),
    packetID: 'C374D0AFCBFFB80529',
    startDateTime: '2024-05-22T16:30:05.101Z',
    requiredExpirationDate: '2024-05-22T17:00:05.101Z',
    recordGeneratedAt: ISODate('2024-05-22T16:34:12.297Z'),
    expirationDate: '2024-05-22T17:04:12.000Z'
  }
]
```
## OdeTimBroadcastJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'metadata.originIp': -1, 'metadata.odeReceivedAt': -1 },
    name: 'metadata.originIp_-1_metadata.odeReceivedAt_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('663d46dad6ab450d2cbaf32e'),
    metadata: {
      request: {
        ode: { verb: 'POST', version: Long('3') },
        sdw: {
          recordId: '80E9DC93',
          serviceRegion: {
            nwCorner: {
              latitude: 38.85754518300007,
              longitude: -102.33163179999997
            },
            seCorner: {
              latitude: 38.822593493000056,
              longitude: -102.04549316299995
            }
          },
          ttl: 'oneday'
        }
      },
      recordGeneratedBy: 'TMC',
      schemaVersion: Long('6'),
      payloadType: 'us.dot.its.jpo.ode.model.OdeMsgPayload',
      odePacketID: '832A109532B87D62AE',
      serialId: {
        recordId: Long('0'),
        serialNumber: Long('0'),
        streamId: '731d03c0-b68e-459f-8def-40e045bd38bf',
        bundleSize: Long('1'),
        bundleId: Long('0')
      },
      sanitized: false,
      recordGeneratedAt: '2024-04-18T19:49:53Z',
      maxDurationTime: Long('30'),
      odeTimStartDateTime: '2024-05-09T21:55:39.437Z',
      odeReceivedAt: '2024-05-09T21:57:46.329720827Z'
    },
    payload: {
      data: {
        timeStamp: '2024-04-18T19:49:53Z',
        packetID: '832A109532B87D62AE',
        urlB: 'null',
        dataframes: [
          {
            durationTime: Long('30'),
            regions: [
              {
                path: {
                  nodes: [
                    {
                      nodeLong: 0.00016269356480336228,
                      delta: 'node-LL',
                      nodeLat: 0.000046164625572941986
                    },
                    {
                      nodeLong: 0.017511640000009265,
                      delta: 'node-LL',
                      nodeLat: 0.0049689630000102625
                    },
                    {
                      nodeLong: 0.017418851000002178,
                      delta: 'node-LL',
                      nodeLat: 0.004939288000002762
                    },
                    {
                      nodeLong: 0.015340179000020271,
                      delta: 'node-LL',
                      nodeLat: 0.0043470969999930276
                    },
                    {
                      nodeLong: 0.004237180999950851,
                      delta: 'node-LL',
                      nodeLat: 0.0012002759999631962
                    },
                    {
                      nodeLong: 0.01534466399999701,
                      delta: 'node-LL',
                      nodeLat: 0.004345064000006005
                    },
                    {
                      nodeLong: 0.017759050000051957,
                      delta: 'node-LL',
                      nodeLat: 0.005025501000034183
                    },
                    {
                      nodeLong: 0.016983448999951634,
                      delta: 'node-LL',
                      nodeLat: 0.004802777000008973
                    },
                    {
                      nodeLong: 0.0018142850000231192,
                      delta: 'node-LL',
                      nodeLat: 0.000463851999995768
                    },
                    {
                      nodeLong: 0.0018380239999942205,
                      delta: 'node-LL',
                      nodeLat: 0.0003181269999572578
                    },
                    {
                      nodeLong: 0.0020866699999828597,
                      delta: 'node-LL',
                      nodeLat: 0.00019217000004800866
                    },
                    {
                      nodeLong: 0.0012960820000103013,
                      delta: 'node-LL',
                      nodeLat: 0.00004340799995361522
                    },
                    {
                      nodeLong: 0.0021935380000286386,
                      delta: 'node-LL',
                      nodeLat: -0.000076109999952223
                    },
                    {
                      nodeLong: 0.015307414000005792,
                      delta: 'node-LL',
                      nodeLat: -0.0008608050000020739
                    },
                    {
                      nodeLong: 0.0035078979999525473,
                      delta: 'node-LL',
                      nodeLat: -0.00024463400001195623
                    },
                    {
                      nodeLong: 0.005054336000000603,
                      delta: 'node-LL',
                      nodeLat: -0.0010492939999835471
                    },
                    {
                      nodeLong: 0.0032423530000187384,
                      delta: 'node-LL',
                      nodeLat: -0.0005972350000433835
                    },
                    {
                      nodeLong: 0.0015670029999910184,
                      delta: 'node-LL',
                      nodeLat: -0.00015915599999516417
                    },
                    {
                      nodeLong: 0.007773738000025787,
                      delta: 'node-LL',
                      nodeLat: -0.0003375719999780813
                    },
                    {
                      nodeLong: 0.009397003000003679,
                      delta: 'node-LL',
                      nodeLat: -0.0004087570000024243
                    },
                    {
                      nodeLong: 0.007445076999999856,
                      delta: 'node-LL',
                      nodeLat: -0.0003243900000029498
                    },
                    {
                      nodeLong: 0.010103495999999268,
                      delta: 'node-LL',
                      nodeLat: -0.00044992099998353297
                    },
                    {
                      nodeLong: 0.0006344089999856806,
                      delta: 'node-LL',
                      nodeLat: -0.00004847500002824745
                    },
                    {
                      nodeLong: 0.002521849000004295,
                      delta: 'node-LL',
                      nodeLat: -0.00035625700002128724
                    },
                    {
                      nodeLong: 0.015634675999990577,
                      delta: 'node-LL',
                      nodeLat: -0.0031093929999883585
                    },
                    {
                      nodeLong: 0.0013025740000216501,
                      delta: 'node-LL',
                      nodeLat: -0.0001733400000034635
                    },
                    {
                      nodeLong: 0.0013427819999947133,
                      delta: 'node-LL',
                      nodeLat: -0.000030895999998392654
                    },
                    {
                      nodeLong: 0.0016772399999922527,
                      delta: 'node-LL',
                      nodeLat: 0.00014649100000951876
                    },
                    {
                      nodeLong: 0.014220474000012473,
                      delta: 'node-LL',
                      nodeLat: 0.0020336810000003425
                    },
                    {
                      nodeLong: 0.0183881309999947,
                      delta: 'node-LL',
                      nodeLat: 0.00262693299998773
                    },
                    {
                      nodeLong: 0.018091684000012265,
                      delta: 'node-LL',
                      nodeLat: 0.0025815370000259463
                    },
                    {
                      nodeLong: 0.027961987999958637,
                      delta: 'node-LL',
                      nodeLat: 0.00398400900002116
                    },
                    {
                      nodeLong: 0.002973539000038272,
                      delta: 'node-LL',
                      nodeLat: 0.00042610599996351084
                    },
                    {
                      nodeLong: 0.004167359999996734,
                      delta: 'node-LL',
                      nodeLat: 0.0007326450000277873
                    }
                  ],
                  scale: Long('0'),
                  type: 'll'
                },
                closedPath: false,
                segmentID: Long('0'),
                name: 'I_US-40_SAT_80E9DC93',
                laneWidth: Long('50'),
                directionality: '3',
                description: 'path',
                regulatorID: Long('0'),
                anchorPosition: {
                  latitude: 38.82254732837448,
                  longitude: -102.33179449356477
                },
                direction: '0001100000000000'
              }
            ],
            notUsed2: Long('0'),
            msgId: {
              roadSignID: {
                viewAngle: '1111111111111111',
                mutcdCode: 'warning',
                position: {
                  latitude: 38.82254732837448,
                  longitude: -102.33179449356477
                }
              }
            },
            notUsed3: Long('0'),
            notUsed1: Long('0'),
            priority: Long('5'),
            content: 'workZone',
            url: 'null',
            notUsed: Long('0'),
            startDateTime: '2024-05-09T21:55:39.437Z',
            frameType: 'advisory',
            items: [ '1025' ]
          },
          {
            durationTime: Long('30'),
            regions: [
              {
                path: {
                  nodes: [
                    {
                      nodeLong: 0.00016269356480336228,
                      delta: 'node-LL',
                      nodeLat: 0.000046164625572941986
                    },
                    {
                      nodeLong: 0.017511640000009265,
                      delta: 'node-LL',
                      nodeLat: 0.0049689630000102625
                    },
                    {
                      nodeLong: 0.017418851000002178,
                      delta: 'node-LL',
                      nodeLat: 0.004939288000002762
                    },
                    {
                      nodeLong: 0.015340179000020271,
                      delta: 'node-LL',
                      nodeLat: 0.0043470969999930276
                    },
                    {
                      nodeLong: 0.004237180999950851,
                      delta: 'node-LL',
                      nodeLat: 0.0012002759999631962
                    },
                    {
                      nodeLong: 0.01534466399999701,
                      delta: 'node-LL',
                      nodeLat: 0.004345064000006005
                    },
                    {
                      nodeLong: 0.017759050000051957,
                      delta: 'node-LL',
                      nodeLat: 0.005025501000034183
                    },
                    {
                      nodeLong: 0.016983448999951634,
                      delta: 'node-LL',
                      nodeLat: 0.004802777000008973
                    },
                    {
                      nodeLong: 0.0018142850000231192,
                      delta: 'node-LL',
                      nodeLat: 0.000463851999995768
                    },
                    {
                      nodeLong: 0.0018380239999942205,
                      delta: 'node-LL',
                      nodeLat: 0.0003181269999572578
                    },
                    {
                      nodeLong: 0.0020866699999828597,
                      delta: 'node-LL',
                      nodeLat: 0.00019217000004800866
                    },
                    {
                      nodeLong: 0.0012960820000103013,
                      delta: 'node-LL',
                      nodeLat: 0.00004340799995361522
                    },
                    {
                      nodeLong: 0.0021935380000286386,
                      delta: 'node-LL',
                      nodeLat: -0.000076109999952223
                    },
                    {
                      nodeLong: 0.015307414000005792,
                      delta: 'node-LL',
                      nodeLat: -0.0008608050000020739
                    },
                    {
                      nodeLong: 0.0035078979999525473,
                      delta: 'node-LL',
                      nodeLat: -0.00024463400001195623
                    },
                    {
                      nodeLong: 0.005054336000000603,
                      delta: 'node-LL',
                      nodeLat: -0.0010492939999835471
                    },
                    {
                      nodeLong: 0.0032423530000187384,
                      delta: 'node-LL',
                      nodeLat: -0.0005972350000433835
                    },
                    {
                      nodeLong: 0.0015670029999910184,
                      delta: 'node-LL',
                      nodeLat: -0.00015915599999516417
                    },
                    {
                      nodeLong: 0.007773738000025787,
                      delta: 'node-LL',
                      nodeLat: -0.0003375719999780813
                    },
                    {
                      nodeLong: 0.009397003000003679,
                      delta: 'node-LL',
                      nodeLat: -0.0004087570000024243
                    },
                    {
                      nodeLong: 0.007445076999999856,
                      delta: 'node-LL',
                      nodeLat: -0.0003243900000029498
                    },
                    {
                      nodeLong: 0.010103495999999268,
                      delta: 'node-LL',
                      nodeLat: -0.00044992099998353297
                    },
                    {
                      nodeLong: 0.0006344089999856806,
                      delta: 'node-LL',
                      nodeLat: -0.00004847500002824745
                    },
                    {
                      nodeLong: 0.002521849000004295,
                      delta: 'node-LL',
                      nodeLat: -0.00035625700002128724
                    },
                    {
                      nodeLong: 0.015634675999990577,
                      delta: 'node-LL',
                      nodeLat: -0.0031093929999883585
                    },
                    {
                      nodeLong: 0.0013025740000216501,
                      delta: 'node-LL',
                      nodeLat: -0.0001733400000034635
                    },
                    {
                      nodeLong: 0.0013427819999947133,
                      delta: 'node-LL',
                      nodeLat: -0.000030895999998392654
                    },
                    {
                      nodeLong: 0.0016772399999922527,
                      delta: 'node-LL',
                      nodeLat: 0.00014649100000951876
                    },
                    {
                      nodeLong: 0.014220474000012473,
                      delta: 'node-LL',
                      nodeLat: 0.0020336810000003425
                    },
                    {
                      nodeLong: 0.0183881309999947,
                      delta: 'node-LL',
                      nodeLat: 0.00262693299998773
                    },
                    {
                      nodeLong: 0.018091684000012265,
                      delta: 'node-LL',
                      nodeLat: 0.0025815370000259463
                    },
                    {
                      nodeLong: 0.027961987999958637,
                      delta: 'node-LL',
                      nodeLat: 0.00398400900002116
                    },
                    {
                      nodeLong: 0.002973539000038272,
                      delta: 'node-LL',
                      nodeLat: 0.00042610599996351084
                    },
                    {
                      nodeLong: 0.004167359999996734,
                      delta: 'node-LL',
                      nodeLat: 0.0007326450000277873
                    }
                  ],
                  scale: Long('0'),
                  type: 'll'
                },
                closedPath: false,
                segmentID: Long('0'),
                name: 'I_US-40_SAT_80E9DC93',
                laneWidth: Long('50'),
                directionality: '3',
                description: 'path',
                regulatorID: Long('0'),
                anchorPosition: {
                  latitude: 38.82254732837448,
                  longitude: -102.33179449356477
                },
                direction: '0001100000000000'
              }
            ],
            notUsed2: Long('0'),
            msgId: {
              roadSignID: {
                viewAngle: '1111111111111111',
                mutcdCode: 'warning',
                position: {
                  latitude: 38.82254732837448,
                  longitude: -102.33179449356477
                }
              }
            },
            notUsed3: Long('0'),
            notUsed1: Long('0'),
            priority: Long('5'),
            content: 'advisory',
            url: 'null',
            notUsed: Long('0'),
            startDateTime: '2024-05-09T21:55:39.437Z',
            frameType: 'advisory',
            items: [ '770' ]
          }
        ],
        msgCnt: Long('1')
      },
      dataType: 'us.dot.its.jpo.ode.plugin.j2735.OdeTravelerInformationMessage'
    },
    recordGeneratedAt: ISODate('2024-05-09T21:57:46.737Z')
  }
]
```
## OdeTimJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'metadata.originIp': -1, 'metadata.odeReceivedAt': -1 },
    name: 'metadata.originIp_-1_metadata.odeReceivedAt_-1'
  },
  {
    v: 2,
    key: { 'metadata.originIp': 1, recordGeneratedAt: -1 },
    name: 'metadata.originIp_1_recordGeneratedAt_-1'
  }
]
```
### Sample Record
```javascript
No Sample Record Available Yet.
```
## ProcessedMap
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { 'properties.intersectionId': -1, 'properties.timeStamp': -1 },
    name: 'properties.intersectionId_-1_properties.timeStamp_-1'
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('664e5c6804d4653aaf1da560'),
    connectingLanesFeatureCollection: {
      features: [
        {
          geometry: {
            coordinates: [ [ -105.0907089, 39.587905 ], [ -105.0907875, 39.58822 ] ],
            type: 'LineString'
          },
          id: '1-15',
          type: 'Feature',
          properties: {
            egressLaneId: Long('15'),
            ingressLaneId: Long('1'),
            signalGroupId: Long('2')
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0907462, 39.5878956 ],
              [ -105.0908389, 39.5882151 ]
            ],
            type: 'LineString'
          },
          id: '2-14',
          type: 'Feature',
          properties: {
            egressLaneId: Long('14'),
            ingressLaneId: Long('2'),
            signalGroupId: Long('2')
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0907914, 39.5878879 ],
              [ -105.0911626, 39.5880622 ]
            ],
            type: 'LineString'
          },
          id: '3-10',
          type: 'Feature',
          properties: {
            egressLaneId: Long('10'),
            ingressLaneId: Long('3'),
            signalGroupId: Long('2')
          }
        },
        {
          geometry: {
            coordinates: [ [ -105.0911477, 39.587995 ], [ -105.0907875, 39.58822 ] ],
            type: 'LineString'
          },
          id: '8-15',
          type: 'Feature',
          properties: {
            egressLaneId: Long('15'),
            ingressLaneId: Long('8'),
            signalGroupId: Long('4')
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0911442, 39.5879589 ],
              [ -105.0910008, 39.5878477 ]
            ],
            type: 'LineString'
          },
          id: '7-6',
          type: 'Feature',
          properties: { egressLaneId: Long('6'), ingressLaneId: Long('7') }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0911534, 39.5880261 ],
              [ -105.0908389, 39.5882151 ]
            ],
            type: 'LineString'
          },
          id: '9-14',
          type: 'Feature',
          properties: {
            egressLaneId: Long('14'),
            ingressLaneId: Long('9'),
            signalGroupId: Long('4')
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0910447, 39.5881948 ],
              [ -105.090959, 39.5878557 ]
            ],
            type: 'LineString'
          },
          id: '12-5',
          type: 'Feature',
          properties: {
            egressLaneId: Long('5'),
            ingressLaneId: Long('12'),
            signalGroupId: Long('6')
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0910013, 39.5881975 ],
              [ -105.090914, 39.5878612 ]
            ],
            type: 'LineString'
          },
          id: '13-4',
          type: 'Feature',
          properties: {
            egressLaneId: Long('4'),
            ingressLaneId: Long('13'),
            signalGroupId: Long('6')
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0910891, 39.5881859 ],
              [ -105.0911626, 39.5880622 ]
            ],
            type: 'LineString'
          },
          id: '11-10',
          type: 'Feature',
          properties: { egressLaneId: Long('10'), ingressLaneId: Long('11') }
        }
      ],
      type: 'FeatureCollection'
    },
    mapFeatureCollection: {
      features: [
        {
          geometry: {
            coordinates: [
              [ -105.0907089, 39.587905 ],
              [ -105.0906245, 39.5876246 ],
              [ -105.0905203, 39.587281 ],
              [ -105.0904383, 39.5870554 ],
              [ -105.0903588, 39.5868383 ],
              [ -105.0902622, 39.5865865 ],
              [ -105.0901249, 39.5862612 ],
              [ -105.0900451, 39.5860819 ],
              [ -105.0899283, 39.5858283 ],
              [ -105.0898739, 39.5857117 ],
              [ -105.0896725, 39.585316 ]
            ],
            type: 'LineString'
          },
          id: Long('1'),
          type: 'Feature',
          properties: {
            connectsTo: [
              {
                connectionID: Long('1'),
                signalGroup: Long('2'),
                connectingLane: {
                  lane: Long('15'),
                  maneuver: {
                    maneuverStraightAllowed: true,
                    maneuverNoStoppingAllowed: false,
                    goWithHalt: false,
                    maneuverLeftAllowed: false,
                    maneuverUTurnAllowed: false,
                    maneuverLeftTurnOnRedAllowed: false,
                    reserved1: false,
                    maneuverRightAllowed: false,
                    maneuverLaneChangeAllowed: false,
                    yieldAllwaysRequired: false,
                    maneuverRightTurnOnRedAllowed: false,
                    caution: false
                  }
                }
              }
            ],
            laneId: Long('1'),
            nodes: [
              { delta: [ Long('1511'), Long('-1514') ] },
              {
                delevation: Long('10'),
                delta: [ Long('723'), Long('-3116') ]
              },
              {
                delevation: Long('20'),
                delta: [ Long('892'), Long('-3818') ]
              },
              {
                delevation: Long('20'),
                delta: [ Long('702'), Long('-2507') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('681'), Long('-2412') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('827'), Long('-2798') ]
              },
              {
                delevation: Long('20'),
                delta: [ Long('1176'), Long('-3614') ]
              },
              { delta: [ Long('683'), Long('-1992') ] },
              {
                delevation: Long('10'),
                delta: [ Long('1000'), Long('-2818') ]
              },
              {
                delevation: Long('20'),
                delta: [ Long('466'), Long('-1295') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('1725'), Long('-4397') ]
              }
            ],
            egressApproach: Long('0'),
            ingressApproach: Long('1'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: true,
            maneuvers: {
              maneuverStraightAllowed: true,
              maneuverNoStoppingAllowed: false,
              goWithHalt: false,
              maneuverLeftAllowed: false,
              maneuverUTurnAllowed: false,
              maneuverLeftTurnOnRedAllowed: false,
              reserved1: false,
              maneuverRightAllowed: false,
              maneuverLaneChangeAllowed: false,
              yieldAllwaysRequired: false,
              maneuverRightTurnOnRedAllowed: false,
              caution: false
            },
            egressPath: false
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0907462, 39.5878956 ],
              [ -105.090652, 39.5875596 ],
              [ -105.090534, 39.5871793 ],
              [ -105.0903457, 39.5866864 ],
              [ -105.0902123, 39.5863581 ],
              [ -105.0900802, 39.5860572 ],
              [ -105.0898164, 39.5855019 ],
              [ -105.0897116, 39.5853068 ]
            ],
            type: 'LineString'
          },
          id: Long('2'),
          type: 'Feature',
          properties: {
            connectsTo: [
              {
                connectionID: Long('1'),
                signalGroup: Long('2'),
                connectingLane: {
                  lane: Long('14'),
                  maneuver: {
                    maneuverStraightAllowed: true,
                    maneuverNoStoppingAllowed: false,
                    goWithHalt: false,
                    maneuverLeftAllowed: false,
                    maneuverUTurnAllowed: false,
                    maneuverLeftTurnOnRedAllowed: false,
                    reserved1: false,
                    maneuverRightAllowed: false,
                    maneuverLaneChangeAllowed: false,
                    yieldAllwaysRequired: false,
                    maneuverRightTurnOnRedAllowed: false,
                    caution: false
                  }
                }
              }
            ],
            laneId: Long('2'),
            nodes: [
              { delta: [ Long('1192'), Long('-1619') ] },
              {
                delevation: Long('30'),
                delta: [ Long('807'), Long('-3733') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('1010'), Long('-4226') ]
              },
              {
                delevation: Long('30'),
                delta: [ Long('1612'), Long('-5477') ]
              },
              {
                delevation: Long('20'),
                delta: [ Long('1142'), Long('-3648') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('1131'), Long('-3343') ]
              },
              {
                delevation: Long('30'),
                delta: [ Long('2259'), Long('-6170') ]
              },
              { delta: [ Long('897'), Long('-2168') ] }
            ],
            egressApproach: Long('0'),
            ingressApproach: Long('1'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: true,
            maneuvers: {
              maneuverStraightAllowed: true,
              maneuverNoStoppingAllowed: false,
              goWithHalt: false,
              maneuverLeftAllowed: false,
              maneuverUTurnAllowed: false,
              maneuverLeftTurnOnRedAllowed: false,
              reserved1: false,
              maneuverRightAllowed: false,
              maneuverLaneChangeAllowed: false,
              yieldAllwaysRequired: false,
              maneuverRightTurnOnRedAllowed: false,
              caution: false
            },
            egressPath: false
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0907914, 39.5878879 ],
              [ -105.090747, 39.5877247 ],
              [ -105.0906498, 39.5874141 ],
              [ -105.0906262, 39.5873356 ],
              [ -105.0905865, 39.5872922 ]
            ],
            type: 'LineString'
          },
          id: Long('3'),
          type: 'Feature',
          properties: {
            connectsTo: [
              {
                connectionID: Long('1'),
                signalGroup: Long('2'),
                connectingLane: {
                  lane: Long('10'),
                  maneuver: {
                    maneuverStraightAllowed: false,
                    maneuverNoStoppingAllowed: false,
                    goWithHalt: false,
                    maneuverLeftAllowed: true,
                    maneuverUTurnAllowed: false,
                    maneuverLeftTurnOnRedAllowed: false,
                    reserved1: false,
                    maneuverRightAllowed: false,
                    maneuverLaneChangeAllowed: false,
                    yieldAllwaysRequired: false,
                    maneuverRightTurnOnRedAllowed: false,
                    caution: false
                  }
                }
              }
            ],
            laneId: Long('3'),
            nodes: [
              {
                delevation: Long('10'),
                delta: [ Long('805'), Long('-1704') ]
              },
              { delta: [ Long('380'), Long('-1813') ] },
              {
                delevation: Long('30'),
                delta: [ Long('832'), Long('-3451') ]
              },
              { delta: [ Long('202'), Long('-872') ] },
              {
                delevation: Long('-10'),
                delta: [ Long('340'), Long('-482') ]
              }
            ],
            egressApproach: Long('0'),
            ingressApproach: Long('1'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: true,
            maneuvers: {
              maneuverStraightAllowed: false,
              maneuverNoStoppingAllowed: false,
              goWithHalt: false,
              maneuverLeftAllowed: true,
              maneuverUTurnAllowed: false,
              maneuverLeftTurnOnRedAllowed: false,
              reserved1: false,
              maneuverRightAllowed: false,
              maneuverLaneChangeAllowed: false,
              yieldAllwaysRequired: false,
              maneuverRightTurnOnRedAllowed: false,
              caution: false
            },
            egressPath: false
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0910008, 39.5878477 ],
              [ -105.0909927, 39.5878181 ]
            ],
            type: 'LineString'
          },
          id: Long('6'),
          type: 'Feature',
          properties: {
            laneId: Long('6'),
            nodes: [
              {
                delevation: Long('20'),
                delta: [ Long('-988'), Long('-2151') ]
              },
              { delta: [ Long('69'), Long('-329') ] }
            ],
            egressApproach: Long('2'),
            ingressApproach: Long('0'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: false,
            egressPath: true
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.090959, 39.5878557 ],
              [ -105.0909501, 39.5878218 ]
            ],
            type: 'LineString'
          },
          id: Long('5'),
          type: 'Feature',
          properties: {
            laneId: Long('5'),
            nodes: [
              {
                delevation: Long('10'),
                delta: [ Long('-630'), Long('-2062') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('76'), Long('-377') ]
              }
            ],
            egressApproach: Long('2'),
            ingressApproach: Long('0'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: false,
            egressPath: true
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.090914, 39.5878612 ],
              [ -105.0909051, 39.5878298 ]
            ],
            type: 'LineString'
          },
          id: Long('4'),
          type: 'Feature',
          properties: {
            laneId: Long('4'),
            nodes: [
              {
                delevation: Long('10'),
                delta: [ Long('-245'), Long('-2001') ]
              },
              { delta: [ Long('76'), Long('-349') ] }
            ],
            egressApproach: Long('2'),
            ingressApproach: Long('0'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: false,
            egressPath: true
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0911626, 39.5880622 ],
              [ -105.0912043, 39.5880536 ]
            ],
            type: 'LineString'
          },
          id: Long('10'),
          type: 'Feature',
          properties: {
            laneId: Long('10'),
            nodes: [
              {
                delevation: Long('10'),
                delta: [ Long('-2374'), Long('232') ]
              },
              { delta: [ Long('-357'), Long('-96') ] }
            ],
            egressApproach: Long('4'),
            ingressApproach: Long('0'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: false,
            egressPath: true
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0911477, 39.587995 ],
              [ -105.0914565, 39.5879427 ],
              [ -105.0917937, 39.5879029 ],
              [ -105.0922121, 39.5878724 ],
              [ -105.0926509, 39.5878748 ],
              [ -105.0930303, 39.5879073 ],
              [ -105.0932697, 39.5879503 ],
              [ -105.0937243, 39.5880569 ],
              [ -105.0940309, 39.5881258 ],
              [ -105.0943257, 39.5881804 ],
              [ -105.094592, 39.5882097 ]
            ],
            type: 'LineString'
          },
          id: Long('8'),
          type: 'Feature',
          properties: {
            connectsTo: [
              {
                connectionID: Long('1'),
                signalGroup: Long('4'),
                connectingLane: {
                  lane: Long('15'),
                  maneuver: {
                    maneuverStraightAllowed: false,
                    maneuverNoStoppingAllowed: false,
                    goWithHalt: false,
                    maneuverLeftAllowed: true,
                    maneuverUTurnAllowed: false,
                    maneuverLeftTurnOnRedAllowed: false,
                    reserved1: false,
                    maneuverRightAllowed: false,
                    maneuverLaneChangeAllowed: false,
                    yieldAllwaysRequired: false,
                    maneuverRightTurnOnRedAllowed: false,
                    caution: false
                  }
                }
              }
            ],
            laneId: Long('8'),
            nodes: [
              {
                delevation: Long('10'),
                delta: [ Long('-2246'), Long('-514') ]
              },
              { delta: [ Long('-2644'), Long('-581') ] },
              {
                delevation: Long('10'),
                delta: [ Long('-2887'), Long('-442') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('-3583'), Long('-339') ]
              },
              { delta: [ Long('-3757'), Long('27') ] },
              {
                delevation: Long('-10'),
                delta: [ Long('-3249'), Long('361') ]
              },
              { delta: [ Long('-2050'), Long('478') ] },
              { delta: [ Long('-3893'), Long('1184') ] },
              {
                delevation: Long('-10'),
                delta: [ Long('-2625'), Long('766') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('-2524'), Long('607') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('-2280'), Long('325') ]
              }
            ],
            egressApproach: Long('0'),
            ingressApproach: Long('3'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: true,
            maneuvers: {
              maneuverStraightAllowed: false,
              maneuverNoStoppingAllowed: false,
              goWithHalt: false,
              maneuverLeftAllowed: true,
              maneuverUTurnAllowed: false,
              maneuverLeftTurnOnRedAllowed: false,
              reserved1: false,
              maneuverRightAllowed: false,
              maneuverLaneChangeAllowed: false,
              yieldAllwaysRequired: false,
              maneuverRightTurnOnRedAllowed: false,
              caution: false
            },
            egressPath: false
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0911442, 39.5879589 ],
              [ -105.0914154, 39.5879165 ],
              [ -105.0916346, 39.5878851 ],
              [ -105.0918433, 39.5878639 ],
              [ -105.0921546, 39.5878547 ]
            ],
            type: 'LineString'
          },
          id: Long('7'),
          type: 'Feature',
          properties: {
            connectsTo: [
              {
                connectionID: Long('1'),
                connectingLane: {
                  lane: Long('6'),
                  maneuver: {
                    maneuverStraightAllowed: false,
                    maneuverNoStoppingAllowed: false,
                    goWithHalt: false,
                    maneuverLeftAllowed: false,
                    maneuverUTurnAllowed: false,
                    maneuverLeftTurnOnRedAllowed: false,
                    reserved1: false,
                    maneuverRightAllowed: true,
                    maneuverLaneChangeAllowed: false,
                    yieldAllwaysRequired: false,
                    maneuverRightTurnOnRedAllowed: false,
                    caution: false
                  }
                }
              }
            ],
            laneId: Long('7'),
            nodes: [
              {
                delevation: Long('10'),
                delta: [ Long('-2216'), Long('-915') ]
              },
              { delta: [ Long('-2322'), Long('-471') ] },
              {
                delevation: Long('10'),
                delta: [ Long('-1877'), Long('-349') ]
              },
              { delta: [ Long('-1787'), Long('-235') ] },
              {
                delevation: Long('10'),
                delta: [ Long('-2666'), Long('-102') ]
              }
            ],
            egressApproach: Long('0'),
            ingressApproach: Long('3'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: true,
            maneuvers: {
              maneuverStraightAllowed: false,
              maneuverNoStoppingAllowed: false,
              goWithHalt: false,
              maneuverLeftAllowed: false,
              maneuverUTurnAllowed: false,
              maneuverLeftTurnOnRedAllowed: false,
              reserved1: false,
              maneuverRightAllowed: true,
              maneuverLaneChangeAllowed: false,
              yieldAllwaysRequired: false,
              maneuverRightTurnOnRedAllowed: false,
              caution: false
            },
            egressPath: false
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0911534, 39.5880261 ],
              [ -105.091436, 39.5879812 ],
              [ -105.0916658, 39.5879507 ],
              [ -105.091881, 39.5879277 ],
              [ -105.0921287, 39.5878972 ]
            ],
            type: 'LineString'
          },
          id: Long('9'),
          type: 'Feature',
          properties: {
            connectsTo: [
              {
                connectionID: Long('1'),
                signalGroup: Long('4'),
                connectingLane: {
                  lane: Long('14'),
                  maneuver: {
                    maneuverStraightAllowed: false,
                    maneuverNoStoppingAllowed: false,
                    goWithHalt: false,
                    maneuverLeftAllowed: true,
                    maneuverUTurnAllowed: false,
                    maneuverLeftTurnOnRedAllowed: false,
                    reserved1: false,
                    maneuverRightAllowed: false,
                    maneuverLaneChangeAllowed: false,
                    yieldAllwaysRequired: false,
                    maneuverRightTurnOnRedAllowed: false,
                    caution: false
                  }
                }
              }
            ],
            laneId: Long('9'),
            nodes: [
              {
                delevation: Long('10'),
                delta: [ Long('-2295'), Long('-169') ]
              },
              { delta: [ Long('-2420'), Long('-499') ] },
              {
                delevation: Long('10'),
                delta: [ Long('-1968'), Long('-339') ]
              },
              { delta: [ Long('-1843'), Long('-256') ] },
              { delta: [ Long('-2121'), Long('-339') ] }
            ],
            egressApproach: Long('0'),
            ingressApproach: Long('3'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: true,
            maneuvers: {
              maneuverStraightAllowed: false,
              maneuverNoStoppingAllowed: false,
              goWithHalt: false,
              maneuverLeftAllowed: true,
              maneuverUTurnAllowed: false,
              maneuverLeftTurnOnRedAllowed: false,
              reserved1: false,
              maneuverRightAllowed: false,
              maneuverLaneChangeAllowed: false,
              yieldAllwaysRequired: false,
              maneuverRightTurnOnRedAllowed: false,
              caution: false
            },
            egressPath: false
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0910447, 39.5881948 ],
              [ -105.0911481, 39.5886317 ],
              [ -105.091196, 39.588862 ],
              [ -105.0912349, 39.5890282 ],
              [ -105.0912722, 39.5893202 ],
              [ -105.0913306, 39.5897261 ],
              [ -105.0913695, 39.5900324 ],
              [ -105.0914008, 39.5903008 ],
              [ -105.0914211, 39.5905 ]
            ],
            type: 'LineString'
          },
          id: Long('12'),
          type: 'Feature',
          properties: {
            connectsTo: [
              {
                connectionID: Long('1'),
                signalGroup: Long('6'),
                connectingLane: {
                  lane: Long('5'),
                  maneuver: {
                    maneuverStraightAllowed: true,
                    maneuverNoStoppingAllowed: false,
                    goWithHalt: false,
                    maneuverLeftAllowed: false,
                    maneuverUTurnAllowed: false,
                    maneuverLeftTurnOnRedAllowed: false,
                    reserved1: false,
                    maneuverRightAllowed: false,
                    maneuverLaneChangeAllowed: false,
                    yieldAllwaysRequired: false,
                    maneuverRightTurnOnRedAllowed: false,
                    caution: false
                  }
                }
              }
            ],
            laneId: Long('12'),
            nodes: [
              {
                delevation: Long('10'),
                delta: [ Long('-1364'), Long('1705') ]
              },
              {
                delevation: Long('-30'),
                delta: [ Long('-885'), Long('4854') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('-410'), Long('2559') ]
              },
              {
                delevation: Long('-10'),
                delta: [ Long('-333'), Long('1847') ]
              },
              {
                delevation: Long('-20'),
                delta: [ Long('-319'), Long('3244') ]
              },
              { delta: [ Long('-500'), Long('4510') ] },
              {
                delevation: Long('-30'),
                delta: [ Long('-333'), Long('3403') ]
              },
              { delta: [ Long('-268'), Long('2982') ] },
              {
                delevation: Long('-10'),
                delta: [ Long('-174'), Long('2213') ]
              }
            ],
            egressApproach: Long('0'),
            ingressApproach: Long('5'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: true,
            maneuvers: {
              maneuverStraightAllowed: true,
              maneuverNoStoppingAllowed: false,
              goWithHalt: false,
              maneuverLeftAllowed: false,
              maneuverUTurnAllowed: false,
              maneuverLeftTurnOnRedAllowed: false,
              reserved1: false,
              maneuverRightAllowed: false,
              maneuverLaneChangeAllowed: false,
              yieldAllwaysRequired: false,
              maneuverRightTurnOnRedAllowed: false,
              caution: false
            },
            egressPath: false
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0910013, 39.5881975 ],
              [ -105.0911059, 39.5886309 ],
              [ -105.091144, 39.5888313 ],
              [ -105.0911829, 39.5890442 ],
              [ -105.0912308, 39.5893169 ],
              [ -105.0912689, 39.5895877 ],
              [ -105.0913005, 39.5898143 ],
              [ -105.0913313, 39.5900714 ],
              [ -105.0913597, 39.5902968 ],
              [ -105.0913776, 39.5905028 ]
            ],
            type: 'LineString'
          },
          id: Long('13'),
          type: 'Feature',
          properties: {
            connectsTo: [
              {
                connectionID: Long('1'),
                signalGroup: Long('6'),
                connectingLane: {
                  lane: Long('4'),
                  maneuver: {
                    maneuverStraightAllowed: true,
                    maneuverNoStoppingAllowed: false,
                    goWithHalt: false,
                    maneuverLeftAllowed: false,
                    maneuverUTurnAllowed: false,
                    maneuverLeftTurnOnRedAllowed: false,
                    reserved1: false,
                    maneuverRightAllowed: false,
                    maneuverLaneChangeAllowed: false,
                    yieldAllwaysRequired: false,
                    maneuverRightTurnOnRedAllowed: false,
                    caution: false
                  }
                }
              }
            ],
            laneId: Long('13'),
            nodes: [
              {
                delevation: Long('10'),
                delta: [ Long('-992'), Long('1735') ]
              },
              {
                delevation: Long('-30'),
                delta: [ Long('-896'), Long('4816') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('-326'), Long('2227') ]
              },
              { delta: [ Long('-333'), Long('2366') ] },
              {
                delevation: Long('-20'),
                delta: [ Long('-410'), Long('3030') ]
              },
              {
                delevation: Long('-10'),
                delta: [ Long('-326'), Long('3009') ]
              },
              {
                delevation: Long('-10'),
                delta: [ Long('-271'), Long('2518') ]
              },
              {
                delevation: Long('-20'),
                delta: [ Long('-264'), Long('2857') ]
              },
              { delta: [ Long('-243'), Long('2504') ] },
              {
                delevation: Long('-10'),
                delta: [ Long('-153'), Long('2289') ]
              }
            ],
            egressApproach: Long('0'),
            ingressApproach: Long('5'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: true,
            maneuvers: {
              maneuverStraightAllowed: true,
              maneuverNoStoppingAllowed: false,
              goWithHalt: false,
              maneuverLeftAllowed: false,
              maneuverUTurnAllowed: false,
              maneuverLeftTurnOnRedAllowed: false,
              reserved1: false,
              maneuverRightAllowed: false,
              maneuverLaneChangeAllowed: false,
              yieldAllwaysRequired: false,
              maneuverRightTurnOnRedAllowed: false,
              caution: false
            },
            egressPath: false
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0910891, 39.5881859 ],
              [ -105.0911549, 39.5884681 ],
              [ -105.091196, 39.5886783 ],
              [ -105.091222, 39.5888049 ],
              [ -105.0912401, 39.5889649 ]
            ],
            type: 'LineString'
          },
          id: Long('11'),
          type: 'Feature',
          properties: {
            connectsTo: [
              {
                connectionID: Long('1'),
                connectingLane: {
                  lane: Long('10'),
                  maneuver: {
                    maneuverStraightAllowed: false,
                    maneuverNoStoppingAllowed: false,
                    goWithHalt: false,
                    maneuverLeftAllowed: false,
                    maneuverUTurnAllowed: false,
                    maneuverLeftTurnOnRedAllowed: false,
                    reserved1: false,
                    maneuverRightAllowed: true,
                    maneuverLaneChangeAllowed: false,
                    yieldAllwaysRequired: false,
                    maneuverRightTurnOnRedAllowed: false,
                    caution: false
                  }
                }
              }
            ],
            laneId: Long('11'),
            nodes: [
              {
                delevation: Long('10'),
                delta: [ Long('-1744'), Long('1607') ]
              },
              {
                delevation: Long('-20'),
                delta: [ Long('-563'), Long('3136') ]
              },
              {
                delevation: Long('-10'),
                delta: [ Long('-352'), Long('2336') ]
              },
              {
                delevation: Long('10'),
                delta: [ Long('-223'), Long('1407') ]
              },
              { delta: [ Long('-155'), Long('1778') ] }
            ],
            egressApproach: Long('0'),
            ingressApproach: Long('5'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: true,
            maneuvers: {
              maneuverStraightAllowed: false,
              maneuverNoStoppingAllowed: false,
              goWithHalt: false,
              maneuverLeftAllowed: false,
              maneuverUTurnAllowed: false,
              maneuverLeftTurnOnRedAllowed: false,
              reserved1: false,
              maneuverRightAllowed: true,
              maneuverLaneChangeAllowed: false,
              yieldAllwaysRequired: false,
              maneuverRightTurnOnRedAllowed: false,
              caution: false
            },
            egressPath: false
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0908389, 39.5882151 ],
              [ -105.0908478, 39.5882471 ]
            ],
            type: 'LineString'
          },
          id: Long('14'),
          type: 'Feature',
          properties: {
            laneId: Long('14'),
            nodes: [
              {
                delevation: Long('-10'),
                delta: [ Long('398'), Long('1931') ]
              },
              { delta: [ Long('-76'), Long('356') ] }
            ],
            egressApproach: Long('6'),
            ingressApproach: Long('0'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: false,
            egressPath: true
          }
        },
        {
          geometry: {
            coordinates: [
              [ -105.0907875, 39.58822 ],
              [ -105.0907979, 39.5882514 ]
            ],
            type: 'LineString'
          },
          id: Long('15'),
          type: 'Feature',
          properties: {
            laneId: Long('15'),
            nodes: [
              {
                delevation: Long('-20'),
                delta: [ Long('838'), Long('1985') ]
              },
              { delta: [ Long('-89'), Long('349') ] }
            ],
            egressApproach: Long('6'),
            ingressApproach: Long('0'),
            sharedWith: {
              busVehicleTraffic: false,
              trackedVehicleTraffic: false,
              individualMotorizedVehicleTraffic: false,
              taxiVehicleTraffic: false,
              overlappingLaneDescriptionProvided: false,
              cyclistVehicleTraffic: false,
              otherNonMotorizedTrafficTypes: false,
              multipleLanesTreatedAsOneLane: false,
              pedestrianTraffic: false,
              pedestriansTraffic: false
            },
            ingressPath: false,
            egressPath: true
          }
        }
      ],
      type: 'FeatureCollection'
    },
    properties: {
      timeStamp: '2024-05-22T20:58:16.177Z',
      validationMessages: [
        {
          schemaPath: '#/$defs/OdeMapMetadata/properties/receivedMessageDetails/required',
          jsonPath: '$.metadata.receivedMessageDetails',
          message: '$.metadata.receivedMessageDetails.locationData: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735IntersectionReferenceID/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].id',
          message: '$.payload.data.intersections.intersectionGeometry[0].id.region: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[3]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[3].maneuvers: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[3]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[3].connectsTo: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[4]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[4].maneuvers: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[4]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[4].connectsTo: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[5]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[5].maneuvers: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[5]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[5].connectsTo: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[6]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[6].maneuvers: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[6]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[6].connectsTo: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735Connection/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[8].connectsTo.connectsTo[0]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[8].connectsTo.connectsTo[0].signalGroup: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735Connection/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[12].connectsTo.connectsTo[0]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[12].connectsTo.connectsTo[0].signalGroup: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[13]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[13].maneuvers: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[13]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[13].connectsTo: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[14]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[14].maneuvers: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735GenericLane/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[14]',
          message: '$.payload.data.intersections.intersectionGeometry[0].laneSet.GenericLane[14].connectsTo: is missing but it is required'
        },
        {
          schemaPath: '#/$defs/J2735IntersectionGeometry/required',
          jsonPath: '$.payload.data.intersections.intersectionGeometry[0]',
          message: '$.payload.data.intersections.intersectionGeometry[0].speedLimits: is missing but it is required'
        }
      ],
      mapSource: 'RSU',
      messageType: 'MAP',
      cti4501Conformant: false,
      refPoint: {
        elevation: 1691,
        latitude: 39.5880413,
        longitude: -105.0908854
      },
      laneWidth: Long('366'),
      intersectionId: Long('12109'),
      odeReceivedAt: '2024-05-22T20:58:16.177Z',
      msgIssueRevision: Long('2'),
      originIp: '192.168.0.1',
      revision: Long('2')
    },
    recordGeneratedAt: ISODate('2024-05-22T20:58:16.216Z')
  }
]
```
## ProcessedSpat
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: { recordGeneratedAt: 1 },
    name: 'recordGeneratedAt_1',
    expireAfterSeconds: 5184000
  },
  {
    v: 2,
    key: { intersectionId: -1, odeReceivedAt: -1 },
    name: 'intersectionId_-1_odeReceivedAt_-1'
  },
  {
    v: 2,
    key: { intersectionId: -1, utcTimeStamp: -1 },
    name: 'intersectionId_-1_utcTimeStamp_-1'
  },
  { v: 2, key: { utcTimeStamp: -1 }, name: 'utcTimeStamp_-1' }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('65de77803982ae591433ba53'),
    validationMessages: [
      {
        schemaPath: '#/$defs/OdeSpatMetadata/properties/receivedMessageDetails/required',
        jsonPath: '$.metadata.receivedMessageDetails',
        message: '$.metadata.receivedMessageDetails.locationData: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/OdeSpatMetadata/required',
        jsonPath: '$.metadata',
        message: '$.metadata.encodings: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/OdeSpatMetadata/required',
        jsonPath: '$.metadata',
        message: '$.metadata.recordGeneratedBy: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735SPAT/required',
        jsonPath: '$.payload.data',
        message: '$.payload.data.timeStamp: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735IntersectionReferenceID/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].id',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].id.region: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[0].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[1].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[2].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[3].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[3].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[3].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[3].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[4].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[4].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[4].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[4].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[5].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[5].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[5].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[5].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[6].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[6].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[6].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[6].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[7].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[7].state_time_speed.movementEventList[0].timing.startTime: is missing but it is required'
      },
      {
        schemaPath: '#/$defs/J2735TimeChangeDetails/required',
        jsonPath: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[7].state_time_speed.movementEventList[0].timing',
        message: '$.payload.data.intersectionStateList.intersectionStatelist[0].states.movementList[7].state_time_speed.movementEventList[0].timing.nextTime: is missing but it is required'
      }
    ],
    utcTimeStamp: '2024-02-27T23:59:59.961Z',
    messageType: 'SPAT',
    cti4501Conformant: false,
    intersectionId: Long('12101'),
    odeReceivedAt: '2024-02-27T23:59:59.968274275Z',
    originIp: '192.168.0.1',
    revision: Long('0'),
    status: {
      failureFlash: false,
      noValidSPATisAvailableAtThisTime: false,
      fixedTimeOperation: false,
      standbyOperation: false,
      trafficDependentOperation: false,
      manualControlIsEnabled: false,
      off: false,
      stopTimeIsActivated: false,
      recentChangeInMAPassignedLanesIDsUsed: false,
      recentMAPmessageUpdate: false,
      failureMode: false,
      noValidMAPisAvailableAtThisTime: false,
      signalPriorityIsActive: false,
      preemptIsActive: false
    },
    states: [
      {
        stateTimeSpeed: [
          {
            eventState: 'STOP_AND_REMAIN',
            timing: {
              minEndTime: '2024-02-27T23:00:20.8Z',
              maxEndTime: '2024-02-27T23:00:20.8Z'
            }
          }
        ],
        signalGroup: Long('1')
      },
      {
        stateTimeSpeed: [
          {
            eventState: 'STOP_AND_REMAIN',
            timing: {
              minEndTime: '2024-02-27T23:00:41.8Z',
              maxEndTime: '2024-02-27T23:00:41.8Z'
            }
          }
        ],
        signalGroup: Long('2')
      },
      {
        stateTimeSpeed: [
          {
            eventState: 'STOP_AND_REMAIN',
            timing: {
              minEndTime: '2024-02-27T23:01:48.8Z',
              maxEndTime: '2024-02-27T23:01:48.8Z'
            }
          }
        ],
        signalGroup: Long('3')
      },
      {
        stateTimeSpeed: [
          {
            eventState: 'PROTECTED_MOVEMENT_ALLOWED',
            timing: {
              minEndTime: '2024-02-27T23:00:01.9Z',
              maxEndTime: '2024-02-27T23:00:10.7Z'
            }
          }
        ],
        signalGroup: Long('4')
      },
      {
        stateTimeSpeed: [
          {
            eventState: 'STOP_AND_REMAIN',
            timing: {
              minEndTime: '2024-02-27T23:01:31.8Z',
              maxEndTime: '2024-02-27T23:01:31.8Z'
            }
          }
        ],
        signalGroup: Long('5')
      },
      {
        stateTimeSpeed: [
          {
            eventState: 'STOP_AND_REMAIN',
            timing: {
              minEndTime: '2024-02-27T23:00:20.8Z',
              maxEndTime: '2024-02-27T23:00:20.8Z'
            }
          }
        ],
        signalGroup: Long('6')
      },
      {
        stateTimeSpeed: [
          {
            eventState: 'STOP_AND_REMAIN',
            timing: {
              minEndTime: '2024-02-27T23:01:48.8Z',
              maxEndTime: '2024-02-27T23:01:48.8Z'
            }
          }
        ],
        signalGroup: Long('7')
      },
      {
        stateTimeSpeed: [
          {
            eventState: 'PROTECTED_MOVEMENT_ALLOWED',
            timing: {
              minEndTime: '2024-02-27T23:00:01.9Z',
              maxEndTime: '2024-02-27T23:00:10.7Z'
            }
          }
        ],
        signalGroup: Long('8')
      }
    ]
  }
]
```
## V2XGeoJson
### Indexes
```javascript
[
  { v: 2, key: { _id: 1 }, name: '_id_' },
  {
    v: 2,
    key: {
      'properties.timestamp': -1,
      'properties.msg_type': -1,
      geometry: '2dsphere'
    },
    name: 'timestamp_geosphere_index',
    '2dsphereIndexVersion': 3
  },
  {
    v: 2,
    key: { 'properties.timestamp': -1 },
    name: 'ttl_index',
    expireAfterSeconds: 5184000
  }
]
```
### Sample Record
```javascript
[
  {
    _id: ObjectId('663eaa4147ba23cdec12d05f'),
    type: 'Feature',
    geometry: { type: 'Point', coordinates: [ -104.9405429, 39.6861196 ] },
    properties: {
      id: '192.168.0.1',
      timestamp: ISODate('2024-05-10T21:45:40.926Z'),
      msg_type: 'Bsm'
    }
  }
]