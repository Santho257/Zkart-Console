package com.santho.services;

import com.santho.helpers.DesignHelper;
import com.santho.helpers.InputHelper;
import com.santho.helpers.SingletonScanner;
import com.santho.proto.Order;
import com.santho.proto.ZProduct;
import com.santho.services.category.CategoryService;
import com.santho.services.category.CategoryServiceImpl;
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
    private static final CategoryService categoryService;
    private static final OrderService orderService;
    private static final DiscountService discountService;
    private static final Scanner in = SingletonScanner.getInstance();

    static {
        try {
            authService = AuthenticationService.getInstance();
            prodService = ProductServiceImpl.getInstance();
            orderService = OrderServiceImpl.getInstance();
            discountService = DiscountServiceImpl.getInstance();
            categoryService = CategoryServiceImpl.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int authMenu() throws IOException {
        int choice;
        while (true) {
            System.out.println("Menu\n1. Signup\n2. Signin\n3. Admin Login\n0. Exit");
            System.out.print("Enter: ");
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
                    System.out.println(DesignHelper.printDesign(50, '~', "Thank you for using z-kart"));
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
        int res;
        if (authService.isAdmin()) {
            res = adminMenu();
        } else {
            res = userMenu();
        }
        if (res < 0) return res;
        return mainMenu();
    }

    private static int userMenu() throws IOException {
        try{

            int choice;
            do {
                System.out.println("Menu\n1. All Products\n2. Buy Product\n3. Order History");
                System.out.println("4. Change Password\n5. Logout\n0. Exit");
                System.out.print("Enter your choice: ");
                try {
                    choice = Integer.parseInt(in.nextLine());
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Enter Valid Input");
                }
            } while (true);
            switch (choice) {
                case 1:
                    prodService.displayWithDeal();
                    break;
                case 2:
                    buy();
                    break;
                case 3:
                    orderService.showAllOrder(authService.getLoggedIn());
                    break;
                case 4:
                    authService.changePassword();
                    break;
                case 5:
                    authService.logout();
                    return -2;
                case 0:
                    System.out.println(DesignHelper.printDesign(50, '~', "Thank you for using z-kart"));
                    return -1;
                default:
                    System.out.println("Invalid Input");
            }
        }
        catch (IllegalStateException ex){
            System.out.println(ex.getMessage());
        }
        return userMenu();
    }

    private static int adminMenu() throws IOException {
        try {
            int choice;
            do {
                System.out.println("Menu:\n1. Restock\n2. All Products\n3. Add Product");
                System.out.println("4. Remove Product\n5. Add Category\n6. Remove Category");
                System.out.println("9. Logout\n0. Exit");
                System.out.print("Enter: ");
                try {
                    choice = Integer.parseInt(in.nextLine());
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Enter Valid Number!");
                }
            } while (true);
            switch (choice) {
                case 1:
                    restock();
                    break;
                case 2:
                    prodService.displayProducts();
                    break;
                case 3:
                    prodService.addProduct();
                    System.out.println(DesignHelper.printDesign(50, '=', "Product Added"));
                    break;
                case 4:
                    prodService.removeProduct();
                    System.out.println(DesignHelper.printDesign(50, '=', "Product Removed"));
                    break;
                case 5:
                    categoryService.addCategory();
                    System.out.println(DesignHelper.printDesign(50, '=', "Category Added"));
                    break;
                case 6:
                    categoryService.removeCategory();
                    System.out.println(DesignHelper.printDesign(50, '=', "Category Removed"));
                    break;
                case 7:
                    categoryService.displayAll();
                    break;
                case 9:
                    authService.logout();
                    return -2;
                case 0:
                    System.out.println(DesignHelper.printDesign(50, '~', "Thank you for using z-kart"));
                    return -1;
                default:
                    System.out.println("Invalid Option");
                    return adminMenu();
            }
        }
        catch (IllegalStateException ex){
            System.out.println(DesignHelper.printDesign(50, '#', ex.getMessage()));
            return adminMenu();
        }
        return 0;
    }

    private static int buy() throws IOException {
        Map<ZProduct.Product, Integer> cart = new HashMap<>();
        prodService.displayDealOfTheMoment();
        int res = buyProducts(cart);
        if (res == 0) {
            return res;
        }
        String code = "";
        String logged = authService.getLoggedIn();
        if (discountService.hasCoupon(logged)) {
            System.out.print("Want to add Discount Code?(yes) ");
            String coup = in.nextLine();
            boolean valid = false;
            if (coup.equalsIgnoreCase("yes")) {
                discountService.showUserCoupon(logged, orderService.getOrderCount(logged));
                System.out.println(DesignHelper.printDesign(50, '*', "Enter -1 to cancel"));
                do {
                    code = InputHelper.getInput("Enter Code: ");
                    try {
                        valid = discountService.isValid(code, orderService.getOrderCount(authService.getLoggedIn()));
                        break;
                    } catch (IllegalArgumentException | IllegalStateException ex) {
                        System.out.println(ex.getMessage());
                    }
                } while (!valid);
                if (!valid)
                    code = "";
            }
        }
        Order.OrderDetail thisOrder = orderService.checkout(cart, code);
        orderService.showOrder(thisOrder);
        return 1;
    }

    private static int restock() throws IOException {
        try{

            System.out.println(DesignHelper.printDesign(50, '*', "Enter -1 to cancel"));
            prodService.showLessThan(10);
            ZProduct.Product product;
            do {
                try {
                    String prodId = InputHelper.getInput("Enter ID to restock: ");
                    product = prodService.getProductById(prodId);
                    break;
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                }
            } while (true);
            int additionalQuantity;
            do{
                additionalQuantity = Integer.parseInt(InputHelper.getInput("Enter additional Quantity: "));
                if(additionalQuantity > 0) break;
                System.out.println("Quantity cannot be less than 0");
            }while (additionalQuantity > 0);
            prodService.reOrder(product, product.getStock() + additionalQuantity);
            System.out.println(DesignHelper.printDesign(50, '=',"Restocked Successfully"));
        }
        catch (IllegalArgumentException ex){
            System.out.println(DesignHelper.printDesign(50, '#', ex.getMessage()));
            return restock();
        }
        return 1;
    }

    private static int buyProducts(Map<ZProduct.Product, Integer> cart) throws IOException {
        System.out.println(DesignHelper.printDesign(50, '-', "Buy Product!?"));
        System.out.println(DesignHelper.printDesign(50, '*', "Enter -1 to cancel"));
        try {
            categoryService.displayAll();
            String category;
            do {
                category = InputHelper.getInput("Enter Category: ").trim().toUpperCase();
                if(categoryService.alreadyExists(category)) break;
                System.out.println("Enter Valid Category!");
            }while (true);
            prodService.displayProductsByCategory(category);
            String prodId = InputHelper.getInput("Enter Product Id: ").trim().toUpperCase();
            ZProduct.Product prod = prodService.existsInCategory(prodId, category);
            int quantity;
            do {
                try {
                    quantity = Integer.parseInt(InputHelper.getInput("Enter Quantity: "));
                    if(quantity > 0) break;
                    System.out.println("Quantity Should be greater than 0");
                } catch (NumberFormatException ex) {
                    System.out.println("Enter Valid Numerical!!");
                }
            } while (true);
            cart.put(prod, cart.getOrDefault(prod, 0) + quantity);
            System.out.print("Continue Buying?(yes)");
            String conti = in.nextLine();
            if (conti.equalsIgnoreCase("yes")) return buyProducts(cart);
            return 1;
        } catch (IllegalArgumentException ex) {
            System.out.println(DesignHelper.printDesign(50, '#', ex.getMessage()));
            return buyProducts(cart);
        }
        catch (IllegalStateException ex){
            System.out.println(DesignHelper.printDesign(50, '#', ex.getMessage()));
            return 0;
        }
    }
}
