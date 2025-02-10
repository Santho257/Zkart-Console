package com.santho.repos;

import com.santho.Main;
import com.santho.proto.ZProduct;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductRepositoryImpl implements ProductRepository{
    private static int count = 1;
    private static ProductRepositoryImpl instance;
    private final File productFile;

    private ProductRepositoryImpl() throws IOException {
        this.productFile = new File("public/db/zproducts_db.protobuf");
        if(!this.productFile.exists()){
            readFromInitial();
        }
    }

    public static ProductRepositoryImpl getInstance() throws IOException {
        if (instance == null) {
            instance = new ProductRepositoryImpl();
        }
        return instance;
    }

    @Override
    public List<ZProduct.Product> getProducts() throws IOException {
        try(FileInputStream prodIs = new FileInputStream(productFile)){
            return new ArrayList<>(ZProduct.AllProducts.parseFrom(prodIs).getProductsList());
        }
    }

    @Override
    public List<ZProduct.Product> getProductsByCategory(ZProduct.Category category) throws IOException {
        try(FileInputStream prodIS = new FileInputStream(productFile)){
            return ZProduct.AllProducts.parseFrom(prodIS)
                    .getProductsList().stream().filter(product -> product.getCategory().equals(category))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void updateStock(String prodId, int quantity) throws IOException {
        try(FileInputStream prodIS = new FileInputStream(productFile)){
            List<ZProduct.Product> allProducts = ZProduct.AllProducts.parseFrom(prodIS)
                    .getProductsList().stream()
                    .map(product -> {
                        if(!product.getId().equalsIgnoreCase(prodId))
                            return product;
                        return product.toBuilder().setStock(quantity).build();
                    })
                    .collect(Collectors.toList());
        }
    }

    private void readFromInitial() throws IOException {
        StringBuilder sb = new StringBuilder();
        try(FileInputStream prodIS = new FileInputStream("src/main/resources/z-kart_db.txt")){
            int ch;
            while((ch = prodIS.read())!=-1) sb.append((char) ch);
        }
        String[] prodArr = sb.toString().split("\n");
        List<ZProduct.Product> productList = new ArrayList<>();
        for (int i = 1; i < prodArr.length; i++) {
            String[] prodDet = prodArr[i].split(" ");
            ZProduct.Product prod = ZProduct.Product.newBuilder()
                    .setId((count +
                            prodDet[1].substring(0, Math.min(prodDet[1].length(), 3)) +
                            prodDet[2].substring(0, Math.min(prodDet[2].length(), 3))).toUpperCase())
                    .setCategory(ZProduct.Category.valueOf(prodDet[0].toUpperCase()))
                    .setBrand(prodDet[1])
                    .setModel(prodDet[2])
                    .setPrice(Double.parseDouble(prodDet[3]))
                    .setStock(Integer.parseInt(prodDet[4]))
                    .build();
            productList.add(prod);
            count += 1;
        }
        ZProduct.AllProducts prods = ZProduct.AllProducts.newBuilder().addAllProducts(productList).build();
        try(FileOutputStream prodOS = new FileOutputStream(this.productFile)){
            prods.writeTo(prodOS);
        }
    }

}
