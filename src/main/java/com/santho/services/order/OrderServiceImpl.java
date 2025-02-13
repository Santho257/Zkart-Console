package com.santho.services.order;

import com.santho.helpers.DesignHelper;
import com.santho.helpers.InputHelper;
import com.santho.proto.Order;
import com.santho.proto.ZProduct;
import com.santho.repos.OrderRepository;
import com.santho.repos.OrderRepositoryImpl;
import com.santho.services.AuthenticationService;
import com.santho.services.discount.DiscountService;
import com.santho.services.discount.DiscountServiceImpl;
import com.santho.services.product.ProductService;
import com.santho.services.product.ProductServiceImpl;

import java.util.*;

public class OrderServiceImpl implements OrderService {
    static private OrderServiceImpl instance;
    private final OrderRepository orderRepository;
    private final AuthenticationService authService;
    private final ProductService productService;
    private final DiscountService discountService;

    private OrderServiceImpl(){
        orderRepository = OrderRepositoryImpl.getInstance();
        authService = AuthenticationService.getInstance();
        productService = ProductServiceImpl.getInstance();
        discountService = DiscountServiceImpl.getInstance();
    }

    public static OrderServiceImpl getInstance(){
        if (instance == null) {
            instance = new OrderServiceImpl();
        }
        return instance;
    }

    @Override
    public int getOrderCount(String user){
        return orderRepository.getOrdersByUser(user).size();
    }

    @Override
    public Order.OrderDetail checkout(Map<ZProduct.Product, Integer> cart, String code){
        for (Map.Entry<ZProduct.Product, Integer> entry : cart.entrySet()) {
            ZProduct.Product product = entry.getKey();
            String prodId = product.getId();
            int stock = product.getStock();
            if(stock == 0){
                System.out.printf("%s currently unavailable\n", prodId);
                String conti = InputHelper.getInput("Do you want to continue?(yes)");
                if(conti.equalsIgnoreCase("yes")){
                    cart.remove(product);
                }
            }
            else if (stock < entry.getValue()){
                System.out.printf("We currently have only %d of %s\n", stock, prodId);
                String conti = InputHelper.getInput("Do you want to continue?(yes)");
                if(conti.equalsIgnoreCase("yes")){
                    cart.put(product, stock);
                }
                else{
                    cart.remove(product);
                }
            }
        }
        if(cart.isEmpty())  throw new IllegalStateException("No products left!");
        String loggedIn = authService.getLoggedIn();
        double price = 0, discountPrice = 0;
        List<Order.ProductDetail> products = new ArrayList<>();
        String dealOfTheMoment = productService.getDealOfTheMoment();
        for (Map.Entry<ZProduct.Product, Integer> entry : cart.entrySet()) {
            ZProduct.Product product = entry.getKey();
            int quantity = entry.getValue();
            products.add(Order.ProductDetail.newBuilder()
                    .setProdId(product.getId())
                    .setQuantity(quantity).build());
            if(product.getId().equals(dealOfTheMoment)) discountPrice += (product.getPrice() * 0.1 * quantity);
            price += (product.getPrice() * quantity);
        }
        if (!code.isEmpty()) {
            discountPrice += (price * (Math.random() * 10 + 20) / 100);
        }
        Order.OrderDetail newOrder = Order.OrderDetail.newBuilder()
                .setSaved(discountPrice)
                .setPrice(price - discountPrice)
                .setOrderBy(loggedIn)
                .addAllProductDetails(products)
                .setDiscount(code.toUpperCase())
                .build();
        showOrder(newOrder);
        String conti = InputHelper.getInput("Proceed to Buy?(yes)");
        if(!conti.equalsIgnoreCase("yes")) {
            throw new IllegalArgumentException("Cart emptied without buying!");
        }
        if (!code.isEmpty()) {
            discountService.useDiscount(code);
        }
        Calendar today = Calendar.getInstance();
        int orderCount = getOrderCount(loggedIn) + 1;
        String invoiceNumber = String.format("%4d/%2d/%s/%4d",
                today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1,
                loggedIn, orderCount).replaceAll(" ", "0");
        String date = String.format("%2d-%2d-%4d", today.get(Calendar.DATE), today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR));
        Order.OrderDetail saved = orderRepository.addOrder(newOrder.toBuilder()
                .setInvoiceNumber(invoiceNumber)
                .setOrderAt(date)
                .build());
        for (Map.Entry<ZProduct.Product, Integer> entry : cart.entrySet()) {
            productService.reOrder(entry.getKey(), entry.getKey().getStock() - entry.getValue());
        }
        if (orderCount == 3 || price - discountPrice >= 20000) {
            String discountCode = discountService.generateCoupon(orderCount);
            System.out.println("Rewards: Discount code: " + discountCode);
        }
        productService.setDeal();
        return saved;
    }

    private void showCart(Map<ZProduct.Product, Integer> cart, String code) {
        System.out.println("ProductId | Quantity");
        for (Map.Entry<ZProduct.Product, Integer> entry : cart.entrySet()) {
            System.out.printf("%s | %d\n", entry.getKey().getId(),entry.getValue());
        }
    }

    @Override
    public void showOrder(Order.OrderDetail order){
        System.out.println();
        if(!order.getInvoiceNumber().isEmpty()){
            System.out.println("Invoice Number: " + order.getInvoiceNumber());
            System.out.println("Date: " + order.getOrderAt());
        }
        System.out.println(DesignHelper.printDesign(75));
        System.out.println("Category | Brand | Model | Price | Quantity");
        System.out.println(DesignHelper.printDesign(75, '-'));
        List<Order.ProductDetail> products = order.getProductDetailsList();
        for (Order.ProductDetail prod : products) {
            try{
                ZProduct.Product product = productService.getProductById(prod.getProdId());
                System.out.printf("%s %s %s %.2f %d%n", product.getCategoryId(), product.getBrand(), product.getModel(), product.getPrice(), prod.getQuantity());
            }
            catch (IllegalArgumentException ex){
                System.out.println(DesignHelper.printDesign(75,'#',ex.getMessage()));
            }
        }
        System.out.println(DesignHelper.printDesign(75));
        System.out.printf("Total: %.2f\n", order.getPrice() + order.getSaved());
        if(order.getSaved() > 0){
            if(!order.getDiscount().isEmpty())
                System.out.println("Used Code : " + order.getDiscount());
            System.out.printf("Saved Discount : %.2f\n", order.getSaved());
        }
        System.out.printf("Billable - %.2f\n", order.getPrice());
        System.out.println();
    }

    @Override
    public void showAllOrder(String email){
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
