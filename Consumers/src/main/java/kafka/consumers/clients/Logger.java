package kafka.consumers.clients;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kafka.consumers.ConsumerConfig;

/**
 * Logger Consumer Class
 */

public class Logger {
    private ConsumerConfig loggerConfig = new ConsumerConfig("Logger");
    private KafkaConsumer<String, String> logger = new KafkaConsumer<>(loggerConfig.getProperties());

    public Logger() {
        logger.subscribe((Arrays.asList("severe_seismic_events", "minor_seismic_events")));
    }

    public void consume() {
        while (true) {
            ObjectMapper mapper = new ObjectMapper();
            ConsumerRecords<String, String> records = logger.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                try {
                    Map<String, Object> recordVal = mapper.readValue(record.value(),
                            new TypeReference<Map<String, Object>>() {
                            });
                    System.out.printf(" %s - Mag:%f > %s\n",recordVal.get("time"),recordVal.get("magnitude"), recordVal.get("region"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}