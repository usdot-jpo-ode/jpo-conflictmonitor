from kafka import KafkaConsumer

bootstrap_servers = '172.250.250.130:9092'
#topic = "topic.OdeSpatJson"
#topic = "topic.OdeRawEncodedBSMJson"
#topic = "topic.OdeMapJson"
#topic = "topic.ProcessedMap"
#topic = "topic.DeduplicatedProcessedMap"
#topic = "topic.DeduplicatedOdeMapJson"
#topic = "topic.OdeTimJson"
#topic = "topic.OdeRawEncodedTIMJson"
#topic = "topic.OdeRawEncodedTIMJson"
#topic = "topic.DeduplicatedOdeTimJson"
#topic = "topic.ProcessedSpat"
#topic = "CmBsmEvents"
#topic = "topic.CmBsmEvents"
#topic = "topic.ProcessedMapWKT"
#topic = "topic.DeduplicatedProcessedMapWKT"
#topic = "topic.Asn1DecoderInput"
#topic = "topic.Asn1DecoderOutput"
#topic = "topic.OdeTimJson"
topic = "topic.DeduplicatedOdeBsmJson"
#topic = "topic.OdeTimJson"
#topic = "topic.OdeBsmJson"
#topic = "topic.OdePsmJson"
#topic = "topic.OdeSsmJson"
#topic = "topic.OdeSrmJson"

consumer = KafkaConsumer(bootstrap_servers=bootstrap_servers)
consumer.subscribe(topic)
count = 0
for message in consumer:
    count +=1
    print(count,message)
    print()
