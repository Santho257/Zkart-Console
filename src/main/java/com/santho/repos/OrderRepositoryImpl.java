package com.santho.repos;

import com.santho.proto.Order;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class OrderRepositoryImpl implements OrderRepository{
    private static OrderRepositoryImpl instance;
    private final File orderFile;

    private OrderRepositoryImpl(){
        orderFile = new File("public/db/zorders_db.protobuf");
        if(!orderFile.exists()){
            try {
                FileOutputStream orderFos = new FileOutputStream(orderFile);
                orderFos.close();
            }
            catch (IOException e) {
                throw new IllegalStateException("Error From our side. Please try again later!");
            }
        }

    }
    public static OrderRepositoryImpl getInstance(){
        if (instance == null) {
            instance = new OrderRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<Order.OrderDetail> getOrdersByUser(String email){
        try(FileInputStream orderIS = new FileInputStream(orderFile)){
            return Order.AllOrders.parseFrom(orderIS)
                    .getOrdersList().stream()
                    .filter(order -> order.getOrderBy().equalsIgnoreCase(email))
                    .collect(Collectors.toList());
        }
        catch (IOException e) {
            throw new IllegalStateException("Error fetching orders. Please try again later!");
        }
    }

    @Override
    public Order.OrderDetail addOrder(Order.OrderDetail newOrder){
        try(FileInputStream orderIS = new FileInputStream(orderFile)) {
            Order.AllOrders allOrders = Order.AllOrders.newBuilder()
                    .mergeFrom(orderIS).addOrders(newOrder).build();
            try(FileOutputStream orderOS = new FileOutputStream(orderFile)) {
                allOrders.writeTo(orderOS);
                return newOrder;
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Error while placing order. Please try again later!");
        }
    }
}
