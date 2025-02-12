package com.santho.services.product;

import com.santho.helpers.DesignHelper;
import com.santho.helpers.InputHelper;
import com.santho.proto.ZProduct;
import com.santho.repos.ProductRepository;
import com.santho.repos.ProductRepositoryImpl;
import com.santho.services.category.CategoryService;
import com.santho.services.category.CategoryServiceImpl;

import java.io.IOException;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    private static ProductServiceImpl instance;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    private ProductServiceImpl() throws IOException {
        productRepository = ProductRepositoryImpl.getInstance();
        categoryService = CategoryServiceImpl.getInstance();
    }

    public static ProductServiceImpl getInstance() throws IOException {
        if(instance == null)    instance = new ProductServiceImpl();
        return instance;
    }

    @Override
    public void displayWithDeal() throws IOException{
        displayDealOfTheMoment();
        displayProducts();
    }

    @Override
    public void displayProducts() throws IOException{
        List<ZProduct.Product> allProducts = productRepository.getProducts();
        System.out.println(DesignHelper.printDesign(50, '-', "All Products"));
        display(allProducts);
    }

    @Override
    public void displayProductsByCategory(String category) throws IOException {
        List<ZProduct.Product> products = productRepository.getProductsByCategory(category);
        System.out.println(DesignHelper.printDesign(50, '*', category + " Products"));
        System.out.println(DesignHelper.printDesign(50));
        System.out.println("ID | BRAND | MODEL | PRICE | STOCK");
        System.out.println(DesignHelper.printDesign(50,'-'));
        for(ZProduct.Product product:products){
            System.out.printf("%s | %s | %s | ", product.getId(), product.getBrand(), product.getModel());
            double price = product.getPrice();
            if(product.getId().equals(productRepository.getDealOfTheMoment())){
                price -= (price * 0.1);
            }
            int stock = product.getStock();
            System.out.printf("%.2f | %s\n", price, stock > 0 ? stock : "currently unavailable" );
        }
        System.out.println(DesignHelper.printDesign(50));
    }

    private void display(List<ZProduct.Product> products){
        System.out.println(DesignHelper.printDesign(50));
        System.out.println("ID | CATEGORY | BRAND | MODEL | PRICE | STOCK");
        System.out.println(DesignHelper.printDesign(50,'-'));
        for(ZProduct.Product product:products){
            System.out.printf("%s | %s | %s | %s | ", product.getId(), product.getCategoryId(), product.getBrand(), product.getModel());
            double price = product.getPrice();
            if(product.getId().equals(productRepository.getDealOfTheMoment())){
                price -= (price * 0.1);
            }
            int stock = product.getStock();
            System.out.printf("%.2f | %s\n", price, stock > 0 ? stock : "currently unavailable" );
        }
        System.out.println(DesignHelper.printDesign(50));
    }
    @Override
    public void displayDealOfTheMoment() throws IOException {
        ZProduct.Product dealProduct = getProductById(getDealOfTheMoment());
        System.out.println(DesignHelper.printDesign(50));
        System.out.println(DesignHelper.printDesign(50, '*', "Deal Of the Moment"));
        System.out.println("ID | CATEGORY | BRAND | MODEL | ACTUAL_PRICE | DISCOUNT_PRICE | STOCK");
        System.out.printf("%s | %s | %s |", dealProduct.getId(), dealProduct.getCategoryId(), dealProduct.getBrand());
        double price = dealProduct.getPrice();
        double discount = price - (price * 0.1);
        System.out.printf("%s | %.2f | %.2f | %d\n", dealProduct.getModel(), price, discount, dealProduct.getStock());
        System.out.println(DesignHelper.printDesign(50));
    }

    @Override
    public void showLessThan(int threshold) throws IOException {
        List<ZProduct.Product> products = productRepository.productsLessThan(threshold);
        if(products.isEmpty()){
            System.out.println(DesignHelper.printDesign(50, '#', "All Stocks Above " + threshold ));
            displayProducts();
            return;
        }
        display(products);
    }

    @Override
    public ZProduct.Product getProductById(String prodId) throws IOException {
        List<ZProduct.Product> products = productRepository.getProducts();
        return products.stream().filter(prod -> prodId.equalsIgnoreCase(prod.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product Not Found"));
    }

    @Override
    public void addProduct() throws IOException{
        System.out.println(DesignHelper.printDesign(50, '*', "Enter -1 to cancel"));
        try{
            categoryService.displayAll();
            String category, brand, model;
            double price;
            int stock;
            do{
                category = InputHelper.getInput("Enter Category: ");
                if(categoryService.alreadyExists(category)) break;
                String create = InputHelper.getInput("Want to add this as new category?(yes)");
                if(create.equalsIgnoreCase("yes")){
                    categoryService.addCategory(category);
                }
            }while (true);
            do{
                brand = InputHelper.getInput("Enter Brand: ");
                model = InputHelper.getInput("Enter Model: ");
                if(!alreadyExists(brand, model))    break;
                System.out.println("Product with " + brand + model + " already exists");
            }while (true);
            do {
                try{
                    price = Double.parseDouble(InputHelper.getInput("Enter Product price: "));
                    if(price > 0) break;
                    System.out.println("Price cannot be less than 0");
                }
                catch (NumberFormatException ex){
                    System.out.println("Enter valid price [only numbers]");
                }
            }while (true);
            do {
                try{
                    stock = Integer.parseInt(InputHelper.getInput("Enter Stock: "));
                    if(stock > 0) break;
                    System.out.println("Stock cannot be less than 0");
                }
                catch (NumberFormatException ex){
                    System.out.println("Enter valid price [only numbers]");
                }
            }while (true);
            productRepository.addProduct(category, brand, model, price, stock);
        }
        catch (IllegalArgumentException ex){
            System.out.println(DesignHelper.printDesign(50, '#', ex.getMessage()));
            addProduct();
        }
    }

    @Override
    public void reOrder(ZProduct.Product product, int quantity) throws IOException {
        if(quantity < 0)
            throw new IllegalArgumentException("Quantity Cannot be less than zero");
        productRepository.updateStock(product, quantity);
    }

    @Override
    public void removeProduct() throws IOException{
        displayProducts();
        try {
            String productId = InputHelper.getInput("Enter Product ID: ");
            productRepository.removeProduct(productId);
        }
        catch (IllegalArgumentException ex){
            System.out.println(DesignHelper.printDesign(50, '#', ex.getMessage()));
            removeProduct();
        }

    }

    @Override
    public ZProduct.Product existsInCategory(String prodId, String category) throws IOException {
        List<ZProduct.Product> products = productRepository.getProductsByCategory(category);
        return products.stream().filter(prod -> prodId.equals(prod.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product Not Found"));
    }

    private boolean alreadyExists(String brand, String model) throws IOException {
        List<ZProduct.Product> products = productRepository.getProducts();
        return products.stream()
                .anyMatch(prod -> prod.getBrand().equalsIgnoreCase(brand) && prod.getModel().equalsIgnoreCase(model));
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
