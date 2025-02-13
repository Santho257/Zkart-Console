package com.santho.services.product;

import com.santho.proto.Order;
import com.santho.proto.ZProduct;

import java.io.IOException;
import java.util.Map;

public interface ProductService {
    void displayProductsByCategory(String category);

    void displayWithDeal();

    void displayProducts();

    ZProduct.Product existsInCategory(String prodId, String category);

    ZProduct.Product getProductById(String prod);

    void reOrder(ZProduct.Product product, int quantity);

    void showLessThan(int treshold);

    void addProduct();

    String getDealOfTheMoment();

    void setDeal();

    void removeProduct();

    void displayByCategoryWithCart(String category, Map<ZProduct.Product, Integer> cart);

    void displayDealOfTheMoment();
}
