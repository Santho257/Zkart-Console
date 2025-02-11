package com.santho.services.order;

import com.santho.proto.Order;
import com.santho.proto.ZProduct;
import com.santho.repos.OrderRepository;
import com.santho.repos.OrderRepositoryImpl;
import com.santho.services.AuthenticationService;
import com.santho.services.discount.DiscountService;
import com.santho.services.discount.DiscountServiceImpl;
import com.santho.services.product.ProductService;
import com.santho.services.product.ProductServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderServiceImpl implements OrderService {
    static private OrderServiceImpl instance;
    private final OrderRepository orderRepository;
    private final AuthenticationService authService;
    private final ProductService productService;
    private final DiscountService discountService;

    private OrderServiceImpl() throws IOException {
        orderRepository = OrderRepositoryImpl.getInstance();
        authService = AuthenticationService.getInstance();
        productService = ProductServiceImpl.getInstance();
        discountService = DiscountServiceImpl.getInstance();
    }

    public static OrderServiceImpl getInstance() throws IOException {
        if (instance == null) {
            instance = new OrderServiceImpl();
        }
        return instance;
    }

    @Override
    public int getOrderCount(String user) throws IOException {
        return orderRepository.getOrdersByUser(user).size();
    }

    @Override
    public Order.OrderDetail checkout(Map<ZProduct.Product, Integer> cart, String code) throws IOException {
        for (Map.Entry<ZProduct.Product, Integer> entry : cart.entrySet()) {
            if (entry.getKey().getStock() < entry.getValue())
                throw new IllegalStateException("Sorry!! " + entry.getKey().getId() + " doesn't has " + entry.getValue() + " Stocks");
        }
        Date today = new Date();
        String invoiceNumber = String.format("%4d/%2d/%s/%4d",
                today.getYear(), today.getMonth(),
                authService.getLoggedIn().split("@")[0], getOrderCount(authService.getLoggedIn()) + 1);
        invoiceNumber = invoiceNumber.replaceAll(" ", "0");
        double price = 0;
        List<Order.ProductDetail> products = new ArrayList<>();
        for (Map.Entry<ZProduct.Product, Integer> entry : cart.entrySet()) {
            ZProduct.Product product = entry.getKey();
            int quantity = entry.getValue();
            products.add(Order.ProductDetail.newBuilder()
                    .setProdId(product.getId())
                    .setQuantity(quantity).build());
            price += (product.getPrice() * quantity);
            productService.reOrder(product.getId(), product.getStock() - quantity);
        }
        String loggedIn = authService.getLoggedIn();
        double discountPrice = 0;
        if (!code.isEmpty()) {
            discountService.useDiscount(code);
            discountPrice = (price * (Math.random() * 10 + 20) / 100);
        }
        if (getOrderCount(loggedIn) == 2 || price >= 20000) {
            String discountCode = discountService.generateCoupon(getOrderCount(loggedIn));
            System.out.println("Rewards: Discount code: " + discountCode);
        }
        Order.OrderDetail newOrder = Order.OrderDetail.newBuilder()
                .setInvoiceNumber(invoiceNumber)
                .setTotal(price)
                .setSaved(discountPrice)
                .setPrice(price - discountPrice)
                .setOrderBy(loggedIn)
                .addAllProductDetails(products)
                .setDiscount(code)
                .setOrderAt(String.format("%2d-%2d-%4d", today.getDate() + 1, today.getMonth() + 1, 1900 + today.getYear()))
                .build();
        return orderRepository.addOrder(newOrder);
    }

    @Override
    public void showOrder(Order.OrderDetail order) throws IOException {
        System.out.println();
        System.out.println("Invoice Number: " + order.getInvoiceNumber());
        System.out.println("Date: " + order.getOrderAt());
        System.out.println("Category Brand Model Price");
        List<Order.ProductDetail> products = order.getProductDetailsList();
        for (Order.ProductDetail prod : products) {
            ZProduct.Product product = productService.getProductById(prod.getProdId());
            System.out.printf("%s %s %s %.2f %d%n", product.getCategory(), product.getBrand(), product.getModel(), product.getPrice(), prod.getQuantity());
        }
        System.out.printf("Total: %.2f\n", order.getTotal());
        if(!order.getDiscount().isEmpty()){
            System.out.printf("Saved Discount : %.2f\n", order.getSaved());
        }
        System.out.printf("Billable - %.2f\n", order.getPrice());
        System.out.println();
    }

    @Override
    public void showAllOrder(String email) throws IOException {
        List<Order.OrderDetail> allOrders = orderRepository.getOrdersByUser(email);
        if(allOrders.isEmpty()){
            System.out.println("You Haven't made any orders yet!");
            return;
        }
        for (Order.OrderDetail order : allOrders){
            showOrder(order);
        }
    }
}
