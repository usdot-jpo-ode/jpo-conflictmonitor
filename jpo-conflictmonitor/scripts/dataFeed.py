

from kafka import KafkaProducer
import json

class MessageProducer:
    broker = ""
    topic = ""
    producer = None

    def __init__(self, broker, topic):
        self.broker = broker
        self.topic = topic
        self.producer = KafkaProducer(bootstrap_servers=self.broker,
        key_serializer = lambda v: '172.19.0.1:12111'.encode('utf-8'),
        value_serializer=lambda v: json.dumps(v).encode('utf-8'),
        acks='all',
        retries = 3)

    def send_msg(self, msg):
        print("sending message...")
        try:
            future = self.producer.send(self.topic, msg)
            self.producer.flush()
            future.get(timeout=60)
            print("message sent successfully...")
            return {'status_code':200, 'error':None}
        except Exception as ex:
            return ex


broker = 'localhost:9092'
topic = 'topic.OdeBsmJson'
message_producer = MessageProducer(broker,topic)

#data = {'name':'abc', 'email':'abc@example.com'}
#resp = message_producer.send_msg(data)
with open('10.11.81.12_BSMlist.csv') as bsms:
    lines = bsms.readlines()
    for line in lines:
        bsm = json.loads(line)

        resp = message_producer.send_msg(bsm)

print(resp)