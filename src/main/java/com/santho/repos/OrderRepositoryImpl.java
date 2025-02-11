package com.santho.repos;

import com.santho.proto.Order;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class OrderRepositoryImpl implements OrderRepository{
    private static OrderRepositoryImpl instance;
    private final File orderFile;

    private OrderRepositoryImpl() throws IOException {
        orderFile = new File("public/db/zorders_db.protobuf");
        if(!orderFile.exists()){
            FileOutputStream orderFos = new FileOutputStream(orderFile);
            orderFos.close();
        }
    }
    public static OrderRepositoryImpl getInstance() throws IOException {
        if (instance == null) {
            instance = new OrderRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<Order.OrderDetail> getOrdersByUser(String email) throws IOException {
        try(FileInputStream orderIS = new FileInputStream(orderFile)){
            return Order.AllOrders.parseFrom(orderIS)
                    .getOrdersList().stream()
                    .filter(order -> order.getOrderBy().equalsIgnoreCase(email))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Order.OrderDetail addOrder(Order.OrderDetail newOrder) throws IOException {
        try(FileInputStream orderIS = new FileInputStream(orderFile)) {
            Order.AllOrders allOrders = Order.AllOrders.newBuilder()
                    .mergeFrom(orderIS).addOrders(newOrder).build();
            try(FileOutputStream orderOS = new FileOutputStream(orderFile)) {
                allOrders.writeTo(orderOS);
                return newOrder;
            }
        }
    }
}
