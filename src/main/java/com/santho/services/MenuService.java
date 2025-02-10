package com.santho.services;

import com.santho.helpers.InputHelper;
import com.santho.helpers.SingletonScanner;
import com.santho.proto.Order;
import com.santho.proto.ZProduct;
import com.santho.services.order.OrderService;
import com.santho.services.order.OrderServiceImpl;
import com.santho.services.product.ProductService;
import com.santho.services.product.ProductServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuService {
    private static final AuthenticationService authService;
    private static final ProductService prodService;
    private static final OrderService orderService;
    private static final Scanner in = SingletonScanner.getInstance();

    static {
        try {
            authService = AuthenticationService.getInstance();
            prodService = ProductServiceImpl.getInstance();
            orderService = OrderServiceImpl.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int authMenu() throws IOException {
        int choice;
        while (true) {
            System.out.println("Enter\n1. Signup\n2. Signin\n3. Admin Login\n0. Exit");
            try {
                choice = Integer.parseInt(in.nextLine());
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Enter Valid Number");
            }
        }
        try {
            switch (choice) {
                case 1:
                    if (authService.signup()) return 1;
                    return authMenu();
                case 2:
                    if (authService.login()) return 1;
                    return authMenu();
                case 3:
                    System.out.println("Admin Login Needs to be implemented");
                case 0:
                    System.out.println("Thank You");
                    return -1;
                default:
                    System.out.println("Enter Valid Option");
                    return authMenu();
            }
        } catch (IllegalStateException ex) {
            System.out.println(ex.getMessage());
        }
        return authMenu();
    }

    public static int mainMenu() throws IOException {
        Map<ZProduct.Product, Integer> cart = new HashMap<>();
        int res = buyProducts(cart);
        if (res < 0) return res;
        Order.OrderDetail thisOrder = orderService.checkout(cart);
        orderService.showOrder(thisOrder);
        return mainMenu();
    }

    private static int buyProducts(Map<ZProduct.Product, Integer> cart) throws IOException {
        int choice;
        do {
            System.out.println("Categories:\n1. Laptops\n2. Mobile\n3. Tablet");
            System.out.print("4. Logout\n0. Exit\nEnter your choice: ");
            try {
                choice = Integer.parseInt(in.nextLine());
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Enter Valid Input");
            }
        } while (true);
        ZProduct.Category category;
        switch (choice) {
            case 1:
                category = ZProduct.Category.LAPTOP;
                prodService.displayProductsByCategory(category);
                break;
            case 2:
                category = ZProduct.Category.MOBILE;
                prodService.displayProductsByCategory(category);
                break;
            case 3:
                category = ZProduct.Category.TABLET;
                prodService.displayProductsByCategory(category);
                break;
            case 4:
                authService.logout();
                return -2;
            case 0:
                return -1;
            default:
                System.out.println("Invalid Input for Category");
                return buyProducts(cart);
        }
        System.out.println("\n***Enter -1 to Exit at any point***");
        System.out.println("***Buy Product!?***");
        try {
            String prodId = InputHelper.getInput("Enter Product Id: ").trim().toUpperCase();
            ZProduct.Product prod = prodService.existsInCategory(prodId, category);
            int quantity;
            do{
                try{
                    quantity = Integer.parseInt(InputHelper.getInput("Enter Quantity: "));
                    break;
                }
                catch (NumberFormatException ex){
                    System.out.println("Enter Valid Numerical!!");
                }
            }while (true);
            cart.put(prod, cart.getOrDefault(prod, 0) + quantity);
            System.out.print("Continue Buying?(yes)");
            String conti = in.nextLine();
            if(conti.equalsIgnoreCase("yes"))   return buyProducts(cart);
            return 1;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println(ex.getMessage());
            return buyProducts(cart);
        }
    }
}
