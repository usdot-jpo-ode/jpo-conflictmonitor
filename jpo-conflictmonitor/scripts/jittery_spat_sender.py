import socket
import time
import os
import random

# Currently set to oim-dev environment's ODE
UDP_IP = os.getenv('DOCKER_HOST_IP')
UDP_PORT = 44910
MESSAGE = "001338000817a780000089680500204642b342b34802021a15a955a940181190acd0acd20100868555c555c00104342aae2aae002821a155715570"

print("UDP target IP:", UDP_IP)
print("UDP target port:", UDP_PORT)
#print("message:", MESSAGE)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM) # UDP

interval = 0.1

while True:
  rand = random.randint(0, 1)
  jitter = -0.09 if rand == 0 else 0.07
  sleepTime = interval + jitter
  time.sleep(sleepTime)
  print(f"sending SPaT every {sleepTime} seconds")
  sock.sendto(bytes.fromhex(MESSAGE), (UDP_IP, UDP_PORT))
