package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

public class FraudDetectorService {

    public static void main(String[] args) {
        var fraudDetectorService = new FraudDetectorService();
       try(var service = new KafkaService<>("ECOMMERCE_NEW_ORDER",
               fraudDetectorService::parse,
               FraudDetectorService.class.getSimpleName(),
               Order.class,
               Map.of())) {

           service.run();
       }
    }

    private void parse(ConsumerRecord<String, Order> record) {
        System.out.println("-------------------------------- Result --------------------------------");
        System.out.println("Progressing new order, checking for fraud");
        System.out.println("Key: " + record.key());
        System.out.println("Value: " + record.value());
        System.out.println("Partition: " + record.partition());
        System.out.println("Offset: " + record.offset());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Order processed");
    }

}
