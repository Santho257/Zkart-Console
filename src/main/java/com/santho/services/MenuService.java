package com.santho.services;

import com.santho.helpers.SingletonScanner;

import java.io.IOException;
import java.util.Scanner;

public class MenuService {
    private static final AuthenticationService authService;
    private static final Scanner in = SingletonScanner.getInstance();

    static {
        try {
            authService = AuthenticationService.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int authMenu() throws IOException {
        int choice;
        while(true){
            System.out.println("Enter\n1. Signup\n2. Signin\n3. Admin Login\n0. Exit");
            try{
                choice = Integer.parseInt(in.nextLine());
                break;
            }
            catch (NumberFormatException ex){
                System.out.println("Enter Valid Number");
            }
        }
        try{
            switch (choice){
                case 1:
                    if(authService.signup()) return 1;
                    return 0;
                case 2:
                    if(authService.login()) return 1;
                    return 0;
                case 3:
                    System.out.println("Admin Login Needs to be implemented");
                case 0:
                    System.out.println("Thank You");
                    return -1;
                default:
                    System.out.println("Enter Valid Option");
                    return 0;
            }
        }
        catch (IllegalStateException ex){
            System.out.println(ex.getMessage());
        }
        return 0;
    }
}
