package com.santho.services;

import com.santho.helpers.InputHelper;
import com.santho.helpers.SingletonScanner;
import com.santho.proto.Order;
import com.santho.proto.ZProduct;
import com.santho.services.discount.DiscountService;
import com.santho.services.discount.DiscountServiceImpl;
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
    private static final DiscountService discountService;
    private static final Scanner in = SingletonScanner.getInstance();

    static {
        try {
            authService = AuthenticationService.getInstance();
            prodService = ProductServiceImpl.getInstance();
            orderService = OrderServiceImpl.getInstance();
            discountService = DiscountServiceImpl.getInstance();
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
                    if (authService.adminLogin()) return 1;
                    return authMenu();
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
        if (authService.isAdmin()) {
            int res = adminMenu();
            if (res < 0) return res;
        } else {
            Map<ZProduct.Product, Integer> cart = new HashMap<>();
            int res = buyProducts(cart);
            if (res < 0) return res;
            String code = "";
            String logged = authService.getLoggedIn();
            if(discountService.hasCoupon(logged)) {
                System.out.print("Want to add Discount Code?(yes) ");
                String coup = in.nextLine();
                boolean valid = false;
                if(coup.equalsIgnoreCase("yes")){
                    discountService.showUserCoupon(logged, orderService.getOrderCount(logged));
                    System.out.println("Enter -1 to optOut");
                    do{
                        code = InputHelper.getInput("Enter Code: ");
                        try{
                            valid = discountService.isValid(code, orderService.getOrderCount(authService.getLoggedIn()));
                            break;
                        }
                        catch (IllegalArgumentException | IllegalStateException ex){
                            System.out.println(ex.getMessage());
                        }
                    }while (!valid);
                    System.out.println("Valid" + valid);
                    if(!valid)
                        code = "";
                }
            }
            Order.OrderDetail thisOrder = orderService.checkout(cart, code);
            orderService.showOrder(thisOrder);
        }

        return mainMenu();
    }

    private static int adminMenu() throws IOException {
        int choice;
        do {
            System.out.println("Enter:\n1. Restock\n2. Logout\n0. Exit");
            try {
                choice = Integer.parseInt(in.nextLine());
                break;
            }
            catch (NumberFormatException ex){
                System.out.println("Enter Valid Number!");
            }
        }while (true);
        switch (choice){
            case 1:
                restock();
                break;
            case 2:
                authService.logout();
                return -2;
            case 0:
                return -1;
            default:
                System.out.println("Invalid Option");
                return adminMenu();
        }
        return 0;
    }

    private static int restock() throws IOException {
        System.out.println("***Enter -1 to exit at any time***");
        prodService.showLessThan(20);
        String prodId = InputHelper.getInput("Enter ID to restock");
        ZProduct.Product product = prodService.getProductById(prodId);
        int additionalQuantity = Integer.parseInt(InputHelper.getInput("Enter updated Quantity"));
        prodService.reOrder(prodId, product.getStock() + additionalQuantity);
        return 1;
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
            do {
                try {
                    quantity = Integer.parseInt(InputHelper.getInput("Enter Quantity: "));
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Enter Valid Numerical!!");
                }
            } while (true);
            cart.put(prod, cart.getOrDefault(prod, 0) + quantity);
            System.out.print("Continue Buying?(yes)");
            String conti = in.nextLine();
            if (conti.equalsIgnoreCase("yes")) return buyProducts(cart);
            return 1;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println(ex.getMessage());
            return buyProducts(cart);
        }
    }
}
