package com.santho.repos;

import com.santho.proto.ZProduct;

import java.io.IOException;
import java.util.List;

public interface ProductRepository {
    List<ZProduct.Product> getProducts() throws IOException;
}
