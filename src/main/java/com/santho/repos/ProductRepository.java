package com.santho.repos;

import com.santho.proto.ZProduct;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ProductRepository {
    List<ZProduct.Product> getProducts();

    List<ZProduct.Product> getProductsByCategory(String category);

    void updateStock(ZProduct.Product product, int quantity);

    List<ZProduct.Product> productsLessThan(int treshold);

    String getDealOfTheMoment();

    void setDealOfTheMoment();

    void addProduct(String category, String brand, String model, double price, int stock);

    void removeProduct(ZProduct.Product product);
}
