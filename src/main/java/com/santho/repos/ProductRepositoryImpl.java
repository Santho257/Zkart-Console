package com.santho.repos;

import com.santho.proto.ZCategory;
import com.santho.proto.ZProduct;
import com.santho.proto.ZUser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductRepositoryImpl implements ProductRepository{
    private static int count = 1;
    private static ProductRepositoryImpl instance;
    private final File productFile;
    private final File categoryFile;
    private String dealOfTheMoment;

    private ProductRepositoryImpl() throws IOException {
        this.productFile = new File("public/db/zproducts_db.protobuf");
        this.categoryFile = new File("public/db/zcategories_db.protobuf");
        if(!this.productFile.exists()){
            readFromInitial();
        }
        else {
            count = getProducts().size() + 1;
        }
        dealOfTheMoment = findDeal();
    }

    private String findDeal() throws IOException {
        try(FileInputStream discountIS = new FileInputStream(productFile)){
            ZProduct.Product dealProd = ZProduct.AllProducts.parseFrom(discountIS).getProductsList()
                    .stream()
                    .reduce((res, prod) -> (res.getStock() < prod.getStock()) ? prod : res)
                    .orElseThrow(() -> new IllegalStateException("No product for deal available"));
            return dealProd.getId();
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
    public List<ZProduct.Product> getProductsByCategory(String category) throws IOException {
        try(FileInputStream prodIS = new FileInputStream(productFile)){
            return ZProduct.AllProducts.parseFrom(prodIS)
                    .getProductsList().stream()
                    .filter(product -> product.getCategoryId().equals(category))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void updateStock(ZProduct.Product product, int quantity) throws IOException {
        try(FileInputStream prodIS = new FileInputStream(productFile)){
            List<ZProduct.Product> productList = getProducts();
            int updateIndex = productList.indexOf(product);
            ZProduct.AllProducts allProducts = ZProduct.AllProducts.parseFrom(prodIS).toBuilder()
                    .setProducts(updateIndex, product.toBuilder().setStock(quantity).build()).build();
            try(FileOutputStream prodOS = new FileOutputStream(productFile)){
                allProducts.writeTo(prodOS);
            }
        }
    }

    @Override
    public List<ZProduct.Product> productsLessThan(int treshold) throws IOException{
        try (FileInputStream prodIS = new FileInputStream(productFile)){
            return ZProduct.AllProducts.parseFrom(prodIS)
                    .getProductsList().stream()
                    .filter(prod -> prod.getStock() < treshold)
                    .collect(Collectors.toList());
        }
    }

    private void readFromInitial() throws IOException {
        StringBuilder sb = new StringBuilder();
        Set<ZCategory.Category> cates = new HashSet<>();
        try(FileInputStream prodIS = new FileInputStream("src/main/resources/z-kart_db.txt")){
            int ch;
            while((ch = prodIS.read())!=-1) sb.append((char) ch);
        }
        String[] prodArr = sb.toString().split("\n");
        List<ZProduct.Product> productList = new ArrayList<>();
        for (int i = 1; i < prodArr.length; i++) {
            String[] prodDet = prodArr[i].split(" ");
            cates.add(ZCategory.Category.newBuilder().setName(prodDet[0].toUpperCase()).build());
            ZProduct.Product prod = ZProduct.Product.newBuilder()
                    .setId((count +
                            prodDet[1].substring(0, Math.min(prodDet[1].length(), 3)) +
                            prodDet[2].substring(0, Math.min(prodDet[2].length(), 3))).toUpperCase())
                    .setCategoryId(prodDet[0].toUpperCase())
                    .setBrand(prodDet[1])
                    .setModel(prodDet[2])
                    .setPrice(Double.parseDouble(prodDet[3]))
                    .setStock(Integer.parseInt(prodDet[4]))
                    .build();
            productList.add(prod);
            count += 1;
        }
        ZCategory.AllCategories categories = ZCategory.AllCategories.newBuilder().addAllCategories(cates).build();
        ZProduct.AllProducts prods = ZProduct.AllProducts.newBuilder().addAllProducts(productList).build();
        try(FileOutputStream prodOS = new FileOutputStream(this.productFile)){
            prods.writeTo(prodOS);
        }
        try(FileOutputStream categoryOS = new FileOutputStream(this.categoryFile)){
            categories.writeTo(categoryOS);
        }
    }

    @Override
    public String getDealOfTheMoment() {
        return dealOfTheMoment;
    }

    @Override
    public void setDealOfTheMoment() throws IOException {
        this.dealOfTheMoment = findDeal();
    }

    @Override
    public void addProduct(String category, String brand, String model, double price, int stock) throws IOException{
        try(FileInputStream prodIS = new FileInputStream(productFile)) {
            String id = count + brand.substring(0,3).toUpperCase() + model.substring(0,3).toUpperCase();
            ZProduct.Product newProd = ZProduct.Product.newBuilder()
                    .setId(id)
                    .setCategoryId(category)
                    .setBrand(brand)
                    .setModel(model)
                    .setPrice(price)
                    .setStock(stock)
                    .build();
            ZProduct.AllProducts products = ZProduct.AllProducts.newBuilder()
                    .mergeFrom(prodIS).addProducts(newProd).build();
            try(FileOutputStream userOS = new FileOutputStream(productFile)) {
                products.writeTo(userOS);
            }
        }
        count++;
    }

    @Override
    public void removeProduct(String productId) throws IOException {
        try(FileInputStream productIS = new FileInputStream(productFile)){
            ZProduct.AllProducts allProducts = ZProduct.AllProducts.parseFrom(productIS);
            List<ZProduct.Product> prodList = allProducts.getProductsList();
            ZProduct.Product product = prodList.stream().filter(prod -> prod.getId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Product Not found"));
            int remIndex = prodList.indexOf(product);
            ZProduct.AllProducts updated = allProducts.toBuilder().removeProducts(remIndex).build();
            try (FileOutputStream categoryOS = new FileOutputStream(productFile)){
                updated.writeTo(categoryOS);
            }
        }
    }
}
