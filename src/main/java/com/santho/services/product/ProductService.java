package com.santho.services.product;

import com.santho.proto.Order;
import com.santho.proto.ZProduct;

import java.io.IOException;
import java.util.Map;

public interface ProductService {
    void displayProductsByCategory(String category) throws IOException;

    void displayProducts() throws IOException;

    ZProduct.Product existsInCategory(String prodId, String category) throws IOException;

    ZProduct.Product getProductById(String prod) throws IOException;

    void reOrder(ZProduct.Product product, int quantity) throws IOException;

    void showLessThan(int treshold) throws IOException;

    void addProduct() throws IOException;

    String getDealOfTheMoment();

    void setDeal() throws IOException;

    void removeProduct() throws IOException;
}
