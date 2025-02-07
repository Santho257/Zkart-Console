package com.santho;

import com.santho.repos.UserRepository;
import com.santho.repos.UserRepositoryImpl;

import java.io.IOException;

public class Main {
    private static final UserRepository userRepository;

    static {
        try {
            userRepository = UserRepositoryImpl.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println("***Welcome to Z-cart - Ecommerce for All***");
        System.out.println(userRepository.getUsers());
    }
}