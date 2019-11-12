#!/bin/bash

NODE=`which node`

nohup $NODE server.js & > log.txt
nohup /usr/local/bin/turnserver --syslog -a -f --min-port=32355 --max-port=65535 --user=dds:123456 -r dds --cert=turn_server_cert.pem --pkey=turn_server_pkey.pem --log-file=stdout -v & > log.txt

