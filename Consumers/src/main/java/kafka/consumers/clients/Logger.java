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
 * Logger Consumer Class:
 * Initialize a Seismic Logger that reads from Kafka Stream
 */

public class Logger {
    private ConsumerConfig loggerConfig = new ConsumerConfig("Logger");
    private KafkaConsumer<String, String> logger = new KafkaConsumer<>(loggerConfig.getProperties());

    /**
     * Constructor to Subscribe Logger to the Kafka Topics
     */
    public Logger() {
        logger.subscribe((Arrays.asList("severe_seismic_events", "minor_seismic_events")));
        System.out.println("-> Subscribed to Kafka Topic..");
    }

    /**
     * Initializes Console Logs that consumes from the Kafka Stream
     */
    public void consume() {
        String RED = "\033[0;31m";
        String YELLOW = "\033[0;33m";
        String GREEN = "\033[0;32m";
        String CYAN = "\033[0;36m";
        String BOLD = "\033[1m";
        String RESET = "\033[0m";

        System.out.println("\n" + CYAN + "Open Interactive Seismic Map and Access API at http://localhost:5000/\n" + RESET);
        System.out.println(GREEN+BOLD + "Log is Live.. \n" + RESET);

        while (true) {
            ObjectMapper mapper = new ObjectMapper();
            ConsumerRecords<String, String> records = logger.poll(Duration.ofSeconds(1));

            // Log Each Record to Console
            for (ConsumerRecord<String, String> record : records) {
                try {
                    Map<String, Map<String, Object>> recordVal = mapper.readValue(record.value(),
                            new TypeReference<Map<String, Map<String, Object>>>() {
                            });
                    if (Float.parseFloat(recordVal.get("payload").get("magnitude").toString()) >= 3.5) {
                        System.out.printf("  %s: %sMag %.1f%s > %s\n",
                                recordVal.get("payload").get("time").toString().replaceAll("T", " at ").replaceAll("Z",
                                        "-UTC"),
                                RED + BOLD,
                                recordVal.get("payload").get("magnitude"), RESET,
                                recordVal.get("payload").get("region"));
                    } else {
                        System.out.printf("  %s: %sMag %.1f%s > %s\n",
                                recordVal.get("payload").get("time").toString().replaceAll("T", " at ").replaceAll("Z",
                                        "-UTC"),
                                YELLOW,
                                recordVal.get("payload").get("magnitude"), RESET,
                                recordVal.get("payload").get("region"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
