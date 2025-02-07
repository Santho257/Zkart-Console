package com.santho;

import com.santho.services.MenuService;

import java.io.IOException;

public class Main {
    public static void main(String[] args){
        System.out.println("***Welcome to Z-cart - Ecommerce for Gadgets***");
        int result;
        try{
            do{
                result = MenuService.authMenu();
            }while(result != 1);
        }
        catch (IOException exception){
            System.out.println(exception.getMessage());
        }
    }
}