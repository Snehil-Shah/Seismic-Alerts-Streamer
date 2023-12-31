package kafka.consumers;

import java.util.Properties;

/**
 * Configuration to initialize Consumers
 */

public class ConsumerConfig
{
    private Properties config = new Properties();
    public ConsumerConfig(String client){
        config.put("bootstrap.servers",System.getenv("KAFKA_SERVER"));
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id",client);
        config.put("auto.offset.reset","earliest");
    }
    public Properties getProperties(){
        return config;
    }
}
