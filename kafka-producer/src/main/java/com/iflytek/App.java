package com.iflytek;

import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        new AutoThread().run();
    }

    private static class AutoThread extends Thread {
        @Override
        public void run() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            final ObjectMapper objectMapper = new ObjectMapper();
            Properties props = new Properties();
            props.put("bootstrap.servers", "10.1.86.221:9092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            Producer<String, String> producer = new KafkaProducer<String, String>(props);
            for (int i = 0; i < 10000; i++) {
                Test test = new Test();
                test.setName("fuck");
                test.setUse(i);
                test.setTs(formatter.format(new Date()));
                String json = null;
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    json = objectMapper.writeValueAsString(test);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                System.out.println(json);
                producer.send(new ProducerRecord<String, String>("druidTest","MORAN_TEST",json));
            }
            producer.close();
        }
    }
}


