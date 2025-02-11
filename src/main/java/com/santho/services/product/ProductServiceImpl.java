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
            System.out.printf("%s %s %s ", product.getId(), product.getBrand(), product.getModel());
            double price = product.getPrice();
            if(product.getId().equals(productRepository.getDealOfTheMoment())){
                price -= (price * 0.1);
            }
            System.out.printf("%.2f %d\n", price, product.getStock());
        }
    }

    @Override
    public void displayProducts() throws IOException{
        List<ZProduct.Product> allProducts = productRepository.getProducts();
        System.out.println("ID " + "CATEGORY" +"BRAND " + "MODEL " + "PRICE " + "STOCK");
        for(ZProduct.Product product:allProducts){
            System.out.printf("%s %s %s %s ", product.getId(), product.getCategory(), product.getBrand(), product.getModel());
            double price = product.getPrice();
            if(product.getId().equals(productRepository.getDealOfTheMoment())){
                price -= (price * 0.1);
            }
            System.out.printf("%.2f %d\n", price, product.getStock());
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

    @Override
    public void showLessThan(int treshold) throws IOException {
        List<ZProduct.Product> products = productRepository.productsLessThan(treshold);
        if(products.size() <= 0){
            System.out.println("Everything is above treshold!");
            return;
        }
        System.out.println("ID " + "CATEGORY" +"BRAND " + "MODEL " + "PRICE " + "STOCK");
        for(ZProduct.Product product : products){
            System.out.print(product.getId() + " " + product.getCategory() + " " + product.getBrand() + " ");
            System.out.print(product.getModel() + " " + product.getPrice() + " ");
            int stock = product.getStock();
            System.out.println((stock > 0) ? stock : "Currently Unavailable");
        }
    }

    @Override
    public String getDealOfTheMoment(){
        return productRepository.getDealOfTheMoment();
    }

    @Override
    public void setDeal() throws IOException {
        productRepository.setDealOfTheMoment();
    }
}
