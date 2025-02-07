package com.santho.helpers;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner in = SingletonScanner.getInstance();
    public static String getInput(String prompt){
        System.out.print(prompt);
        String value = in.nextLine();
        try{
            if(Integer.parseInt(value) == -1){
                throw new IllegalStateException("Cancelling");
            }
        }
        catch (NumberFormatException ignored){
        }
        return value;
    }
}
