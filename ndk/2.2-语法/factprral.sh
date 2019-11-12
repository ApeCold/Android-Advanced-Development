#!/bin/bash
a=1;
factorial(){
for i in `seq $1`
do
	let a*=$i
done
echo  "$1 的阶乘 $a "
}

factorial $1

