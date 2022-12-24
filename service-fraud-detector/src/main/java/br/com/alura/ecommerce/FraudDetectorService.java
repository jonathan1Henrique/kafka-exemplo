package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

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
    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
        System.out.println("-------------------------------- Result --------------------------------");
        System.out.println("Progressing new order, checking for fraud");
        System.out.println("Key: " + record.key());
        System.out.println("Value: " + record.value());
        System.out.println("Offset: " + record.offset());
        System.out.println("Partition: " + record.partition());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var order = record.value();

        if(isfraud(order)){
            System.out.println("Order is a fraud !!!!!!!!!!");
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getUserId(), order);
        }else{
            System.out.println("Approved: " + order.toString());
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getUserId(), order);
        }


    }

    private boolean isfraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;


    }

}
