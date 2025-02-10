package com.santho.services.product;

import com.santho.proto.Order;
import com.santho.proto.ZProduct;

import java.io.IOException;
import java.util.Map;

public interface ProductService {
    void displayProductsByCategory(ZProduct.Category category) throws IOException;

    ZProduct.Product existsInCategory(String prodId, ZProduct.Category category) throws IOException;

    ZProduct.Product getProductById(String prod) throws IOException;

    void reOrder(String prodId, int quantity) throws IOException;
}
