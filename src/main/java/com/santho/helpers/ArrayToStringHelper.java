package com.santho.helpers;

public class ArrayToStringHelper{
    public static String toString(char[] array) {
        StringBuilder sb = new StringBuilder();
        for(char element:array){
            sb.append(element);
        }
        return sb.toString();
    }
}
