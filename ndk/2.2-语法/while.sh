#!/bin/bash
i=0
while [[ $i -lt 100 ]]
do
	echo "数字 $i"
	i=`expr $i + 1`
done
