package com.santho;

import com.santho.repos.ProductRepository;
import com.santho.repos.ProductRepositoryImpl;

import java.io.IOException;

public class Main {
    private static final ProductRepository productRepository;

    static {
        try {
            productRepository = ProductRepositoryImpl.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("***Welcome to Z-cart - Ecommerce for All***");
        System.out.println(productRepository.getProducts());
    }
}