#!/bin/bash

# Exit codes:
# 0 normal shutdown
# 2 reboot attempt

while :; do
	[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	[ -f log/stdout.log ] && mv log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_stdout.log"
	java -XX:+UseConcMarkSweepGC -Xmx256m -cp ./libs/*:GameOne_Server.jar server.Startup > log/stdout.log 2>&1
	[ $? -ne 2 ] && break
	sleep 10
done
