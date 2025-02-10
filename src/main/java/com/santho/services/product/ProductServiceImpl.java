package com.santho.services.product;

import com.santho.proto.ZProduct;
import com.santho.repos.ProductRepository;
import com.santho.repos.ProductRepositoryImpl;
import com.santho.services.AuthenticationService;

import java.io.IOException;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    private static ProductServiceImpl instance;
    private final ProductRepository productRepository;
    private final AuthenticationService authenticationService;

    private ProductServiceImpl() throws IOException {
        productRepository = ProductRepositoryImpl.getInstance();
        authenticationService = AuthenticationService.getInstance();
    }

    public static ProductServiceImpl getInstance() throws IOException {
        if(instance == null)    instance = new ProductServiceImpl();
        return instance;
    }

    @Override
    public void displayProductsByCategory(ZProduct.Category category) throws IOException {
        List<ZProduct.Product> products = productRepository.getProductsByCategory(category);
        System.out.println("*** " + category + " Products ***");
        System.out.println("ID " + "BRAND " + "MODEL " + "PRICE " + "STOCK");
        for(ZProduct.Product product : products){
            System.out.print(product.getId() + " " + product.getBrand() + " ");
            System.out.print(product.getModel() + " " + product.getPrice() + " ");
            int stock = product.getStock();
            System.out.println((stock > 0) ? stock : "Currently Unavailable");
        }
    }

    @Override
    public ZProduct.Product existsInCategory(String prodId, ZProduct.Category category) throws IOException {
        List<ZProduct.Product> products = productRepository.getProductsByCategory(category);
        return products.stream().filter(prod -> prodId.equals(prod.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product Not Found"));
    }

    @Override
    public ZProduct.Product getProductById(String prodId) throws IOException {
        List<ZProduct.Product> products = productRepository.getProducts();
        return products.stream().filter(prod -> prodId.equalsIgnoreCase(prod.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product Not Found"));
    }

    @Override
    public void reOrder(String prodId, int quantity) throws IOException {
        if(quantity < 0)
            throw new IllegalArgumentException("Quantity Cannot be less than zero");
        productRepository.updateStock(prodId, quantity);
    }
}
