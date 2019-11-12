#!/bin/bash
for i in `find /root -name "*.sh"`
do
	tar -czf wangi.tgz $i
done
