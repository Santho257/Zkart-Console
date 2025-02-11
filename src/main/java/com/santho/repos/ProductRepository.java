package com.santho.repos;

import com.santho.proto.ZProduct;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ProductRepository {
    List<ZProduct.Product> getProducts() throws IOException;

    List<ZProduct.Product> getProductsByCategory(String category) throws IOException;

    void updateStock(ZProduct.Product product, int quantity) throws IOException;

    List<ZProduct.Product> productsLessThan(int treshold) throws IOException;

    String getDealOfTheMoment();

    void setDealOfTheMoment() throws IOException;

    void addProduct(String category, String brand, String model, double price, int stock) throws IOException;

    void removeProduct(String productId) throws IOException;
}
