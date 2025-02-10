package com.santho.repos;

import com.santho.proto.ZProduct;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ProductRepository {
    List<ZProduct.Product> getProducts() throws IOException;

    List<ZProduct.Product> getProductsByCategory(ZProduct.Category category) throws IOException;

    void updateStock(String prodId, int quantity) throws IOException;

    List<ZProduct.Product> productsLessThan(int treshold) throws IOException;
}
