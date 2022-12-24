package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/user_database.db";

        this.connection = DriverManager.getConnection(url);
        try{
            connection.createStatement().execute("create table  Users (" +
                    "uuid varchar(200) primary key," +
                    "email varchar(200 )" +
                    ")");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        try(var service = new KafkaService<>("ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                CreateUserService.class.getSimpleName(),
                Order.class,
                Map.of())) {

            service.run();
        }

    }
    private void parse(ConsumerRecord<String, Order> record) throws SQLException {
        System.out.println("-------------------------------- Result --------------------------------");
        System.out.println("Progressing new order, checking for new user");

        System.out.println("Value: " + record.value());

        var order = record.value();

        if(isNewUser(order.getEmail())){
            insertNewUser(order.getUserId(), order.getEmail());

        }
    }

    private void insertNewUser(String uuid, String email) throws SQLException {
        var insert = connection.prepareStatement("insert  into Users (uuid, email) " +
                                         "values (?, ?)");

        insert.setString(1, uuid);
        insert.setString(2, email);
        insert.execute();

        System.out.println("Usuário uuid foi " + email + "adicionado");
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("select uuid from USER " +
                "where email = ? limit 1");
        exists.setString(1, email);
        var results = exists.executeQuery();
        return !results.next();
    }

}
