package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {

    private final String userId, orderId;
    private final BigDecimal amount;
    private final String email;
    Order(String userId, String orderId, BigDecimal amount, String email){
        this.amount = amount;
        this.userId = userId;
        this.orderId = orderId;
        this.email = email;
    }

    public BigDecimal getAmount() {
        return amount;
    }


    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Order {" +
                "userId= '" + userId +'\'' +
                ", orderId= '" + orderId + '\'' +
                ", amount= " + amount +
                '}';
    }


    public String getEmail(){
        return"Email";
    }

}
