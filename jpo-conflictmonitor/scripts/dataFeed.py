

from kafka import KafkaProducer
from datetime import datetime
import json

intersection_ip = "10.11.81.12"
intersection_id = "12111"
broker = 'localhost:9092'


bsm_topic = 'topic.OdeBsmJson'
spat_topic = 'topic.OdeSpatJson'
map_topic = 'topic.OdeMapJson'

enable_bsms = True
enable_spat = True
enable_map = True
stop_at = 10000

class MessageProducer:
    broker = ""
    topic = ""
    producer = None

    def __init__(self, broker, topic, add_key = False):
        self.broker = broker
        self.topic = topic
        if add_key:
            self.producer = KafkaProducer(bootstrap_servers=self.broker,
            key_serializer = lambda v: f'{intersection_ip}:{intersection_id}'.encode('utf-8'),
            value_serializer=lambda v: json.dumps(v).encode('utf-8'),
            acks='all',
            retries = 3)
        else:
            self.producer = KafkaProducer(bootstrap_servers=self.broker,
            value_serializer=lambda v: json.dumps(v).encode('utf-8'),
            acks='all',
            retries = 3)

    def send_msg(self, msg):
        try:
            future = self.producer.send(self.topic, msg)
            self.producer.flush()
            future.get(timeout=60)
            return {'status_code':200, 'error':None}
        except Exception as ex:
            return ex


def get_lines(filename):
    with open(filename, 'r') as f:
        return f.readlines()

def parse_msg(lines):
    msgs = []
    for line in lines:
        msgs.append(json.loads(line))

    msgs.sort(key= lambda element: element['metadata']['recordGeneratedAt'])
    return msgs


def modify_bsm_time(bsm , mod_time):
    bsm['metadata']['recordGeneratedAt'] = mod_time.isoformat()+"Z"
    return bsm

def modify_spat_time(spat, mod_time):
    # year_start = datetime(mod_time.year,1,1)
    # moy = int((mod_time - year_start)).seconds / 60)

    minute_start = datetime(mod_time.year, mod_time.month, mod_time.day, mod_time.hour, mod_time.minute, 0)
    msom = int((mod_time - minute_start).microseconds / 1000)



    for record in spat['payload']['data']['intersectionStateList']['intersectionStatelist']:
        record['timeStamp'] = msom


    spat['metadata']['odeReceivedAt'] = mod_time.isoformat()+"Z"

    return spat




bsm_producer = MessageProducer(broker, bsm_topic)
spat_producer = MessageProducer(broker, spat_topic)
map_producer = MessageProducer(broker, map_topic)

bsm_lines = get_lines('10.11.81.12_BSMlist.csv')
spat_lines = get_lines('10.11.81.12_SPATlist.csv')
map_lines = get_lines('10.11.81.12_MAPlist.csv')


bsms = parse_msg(bsm_lines)
spats = parse_msg(spat_lines)



count = 0

while count < max(len(spat_lines), len(bsm_lines)):
    now = datetime.utcnow()

    if count < len(bsms) and enable_bsms:
        bsm = bsms[count]
        #bsm = json.loads(bsm_lines[count])
        #bsm = modify_bsm_time(bsms[count], now)
        #if bsm['payload']['data']['coreData']['id'] == 'E6A99808':
        #    print(bsm['metadata']['recordGeneratedAt'],",",bsm['payload']['data']['coreData']['position']['longitude'], ", " , bsm['payload']['data']['coreData']['position']['latitude'])
        print("Sending BSM", bsm['metadata']['odeReceivedAt'])
        resp = bsm_producer.send_msg(bsm)
    
    if count < len(spats) and enable_spat:
        #spat = json.loads(spat_lines[count])
        #spat = modify_spat_time(spats[count], now)
        spat = spats[count]
        resp = spat_producer.send_msg(spat)

    if count < len(map_lines) and enable_map:
        map_msg = json.loads(map_lines[count])
        resp = map_producer.send_msg(map_msg)

    count +=1

    if count >= stop_at:
        break




