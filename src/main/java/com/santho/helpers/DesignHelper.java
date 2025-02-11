package com.santho.helpers;

public class DesignHelper {
    public static String printDesign(int len){
        return "=".repeat(len);
    }

    public static String printDesign(int len, char ch){
        return String.valueOf(ch).repeat(len);
    }

    public static String printDesign(int len, char ch, String str){
        StringBuilder design = new StringBuilder();
        int n = (len - str.length())/2;
        design.append(String.valueOf(ch).repeat(n));
        design.append(str);
        design.append(String.valueOf(ch).repeat(n));
        return design.toString();
    }
}
