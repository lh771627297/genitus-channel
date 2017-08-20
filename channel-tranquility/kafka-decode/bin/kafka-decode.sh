#!/usr/bin/env bash
java -Dlogback.configurationFile=logback.xml -cp ./lib/*:./channel-tranquility-kafka-decode-1.0-SNAPSHOT.jar \
    com.metamx.tranquility.kafka.KafkaMain -f rdq.json
