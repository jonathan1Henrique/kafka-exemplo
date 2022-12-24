package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

class KafkaService<T> implements Closeable {

    private final KafkaConsumer<String, T> consumer;
    private final ConsumerFunction parse;

     KafkaService(String topic,
                  ConsumerFunction parse,
                  String groupId,
                  Class<T> type,
                  Map<String,String> properties) {

        this(parse, groupId, type, properties);
        consumer.subscribe(Collections.singletonList(topic));
     }

     KafkaService(Pattern topic,
                  ConsumerFunction parse,
                  String groupId,
                  Class<T> type,
                  Map<String,String> properties) {

        this(parse, groupId, type, properties);
        consumer.subscribe(topic);
    }

     private KafkaService(ConsumerFunction parse,
                          String groupId,
                          Class<T> type,
                          Map<String,String> properties) {

        this.parse = parse;
        this.consumer = new KafkaConsumer<>(
                getProperties(groupId, type, properties));
    }

    void run(){
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));

            if (!records.isEmpty()) {
                System.out.println("Encontrei " + records.count() + " registros");

                for (var record : records) {
                    try {
                        parse.consume(record);
                    } catch (Exception e) {
                        // Only catches Exception because no matter with Exception
                        // i want to recover and parse to next one
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Properties getProperties(String groupId, Class<T> type, Map<String, String> overrideProperties){
        var properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
        properties.putAll(overrideProperties);

        return properties;
    }

    @Override
    public void close(){
        consumer.close();
    }
}
