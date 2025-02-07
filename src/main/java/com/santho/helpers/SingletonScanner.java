package com.santho.helpers;

import java.util.Scanner;

public class SingletonScanner {
    private static Scanner instance;
    private SingletonScanner(){

    }
    public static Scanner getInstance(){
        if(instance == null){
            instance = new Scanner(System.in);
        }
        return instance;
    }
}
