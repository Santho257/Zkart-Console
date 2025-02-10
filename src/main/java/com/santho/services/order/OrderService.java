package com.santho.services.order;

import com.santho.proto.Order;
import com.santho.proto.ZProduct;

import java.io.IOException;
import java.util.Map;

public interface OrderService {
    int getOrderCount(String user) throws IOException;

    Order.OrderDetail checkout(Map<ZProduct.Product, Integer> cart, String code) throws IOException;

    void showOrder(Order.OrderDetail thisOrder) throws IOException;
}
