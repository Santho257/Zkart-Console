package com.santho;

import com.santho.services.MenuService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("***Welcome to Z-cart - Ecommerce for Gadgets***");
        int result;
        try {
            result = MenuService.authMenu();
            if (result == -1) return;
            result = MenuService.mainMenu();
            if(result == -2)    main(args);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}