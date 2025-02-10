package com.santho.repos;

import com.santho.proto.Order;

import java.io.IOException;
import java.util.List;

public interface OrderRepository {
    List<Order.OrderDetail> getOrdersByUser(String email) throws IOException;

    Order.OrderDetail addOrder(Order.OrderDetail newOrder) throws IOException;
}
