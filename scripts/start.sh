#!/bin/sh

CLASSPATH=.
for file in ../lib/*.jar
do
	echo 'ADD JAVA LIB' $file
	CLASSPATH=$CLASSPATH:$file
done

java -classpath $CLASSPATH com.jpycrgo.gsimgdown.Main