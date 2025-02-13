package com.santho;

import com.santho.helpers.DesignHelper;
import com.santho.services.MenuService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println(DesignHelper.printDesign(75,'*', "Welcome to Z-kart"));
        int result;
        try {
            result = MenuService.authMenu();
            if (result == -1)   return;
            result = MenuService.mainMenu();
            if(result == -2)    main(args);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }
}