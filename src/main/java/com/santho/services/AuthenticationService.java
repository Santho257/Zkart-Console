package com.santho.services;

import com.santho.helpers.InputHelper;
import com.santho.helpers.SingletonScanner;
import com.santho.proto.ZUser;
import com.santho.repos.AdminRepository;
import com.santho.repos.AdminRepositoryImpl;
import com.santho.repos.UserRepository;
import com.santho.repos.UserRepositoryImpl;
import com.santho.services.user.UserService;
import com.santho.services.user.UserServiceImpl;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AuthenticationService {
    private static AuthenticationService instance;
    private final Scanner in;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final UserService userService;
    private String loggedIn;
    private boolean admin = false;

    private AuthenticationService() throws IOException {
        this.in = SingletonScanner.getInstance();
        userRepository = UserRepositoryImpl.getInstance();
        userService = UserServiceImpl.getInstance();
        adminRepository = AdminRepositoryImpl.getInstance();
    }

    public static AuthenticationService getInstance() throws IOException {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public boolean signup() throws IOException {
        System.out.println("!!**Enter -1 to cancel at any point**!!");
        String email;
        do {
            email = InputHelper.getInput("Enter Email: ").toLowerCase();
            if (validateEmail(email)) break;
            else System.out.println("Not an Valid email!!");
        } while (true);
        if (userRepository.alreadyExists(email)) {
            System.out.println("User Already Exist! -> Moves to Sign-in");
            return this.login(email);
        }
        return signup(email);
    }

    private boolean signup(String email) throws IOException {
        String password = InputHelper.getInput("Enter Password: ");
        String rePass;
        do {
            rePass = InputHelper.getInput("Enter Password Again: ");
            if (password.equals(rePass)) break;
            else System.out.println("Password Doesn't match");
        } while (true);
        String name = InputHelper.getInput("Enter Name: ");
        String number;
        do {
            number = InputHelper.getInput("Enter Number: ");
            if (validateMobile(number)) break;
            else System.out.println("Invalid Number:: Should be 10 numbers that starts with 6 | 7 | 8 | 9");
        } while (true);
        userService.addUser(ZUser.User.newBuilder()
                .setEmail(email)
                .setPassword(PasswordEncoderService.encode(password))
                .setName(name)
                .setMobile(number)
                .build());
        this.loggedIn = email;
        return true;
    }

    private boolean validateMobile(String number) {
        return Pattern
                .compile("^[6789]{1}[0-9]{9}$", Pattern.CASE_INSENSITIVE)
                .matcher(number).matches();
    }

    private boolean validateEmail(String email) {
        return Pattern
                .compile("^[a-z\\d]([a-z\\d]+\\.)*[a-z\\d]+@([a-z\\d]+\\.)+[a-z]{2,4}$", Pattern.CASE_INSENSITIVE)
                .matcher(email).matches();
    }

    public boolean login() throws IOException {
        System.out.println("!!**Enter -1 to cancel at any point**!!");
        String email = InputHelper.getInput("Enter Email: ").toLowerCase();
        if(userRepository.alreadyExists(email))
            return this.login(email);
        else {
            System.out.println("User Doesn't Exists\n Creating new account");
            return this.signup(email);
        }
    }

    private boolean login(String email) throws IOException {
        String password = InputHelper.getInput("Enter Password: ");
        if (PasswordEncoderService.match(password, userRepository.getByEmail(email).getPassword())) {
            this.loggedIn = email;
            return true;
        }
        else {
            System.out.println("Password Doesn't Match");
            return login(email);
        }
    }

    public void logout() {
        this.loggedIn = null;
        if (admin) admin = false;
    }

    public boolean adminLogin() throws IOException {
        System.out.println("Enter -1 to Go back");
        String email;
        do{
            email = InputHelper.getInput("Enter email: ");
            if(email.equals("admin@zoho.com"))  break;
            System.out.println("Invalid Admin Email!!");
        }while (true);
        do{
            String password = InputHelper.getInput("Enter Password: ");
            if(PasswordEncoderService.match(password, adminRepository.getAdmin().getPassword())){
                loggedIn = email;
                admin = true;
                return true;
            }
            System.out.println("Incorrect Password!");
        }while (true);
    }

    public String getLoggedIn() {
        return loggedIn;
    }

    public boolean isAdmin() {
        return admin;
    }
}
