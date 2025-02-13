package com.santho.services;

import com.santho.helpers.DesignHelper;
import com.santho.helpers.InputHelper;
import com.santho.proto.ZUser;
import com.santho.repos.login.LoginRepository;
import com.santho.repos.login.LoginRepositoryImpl;
import com.santho.services.admin.AdminService;
import com.santho.services.admin.AdminServiceImpl;
import com.santho.services.user.UserService;
import com.santho.services.user.UserServiceImpl;

import com.santho.helpers.ValidateHelper;

public class AuthenticationService {
    private static AuthenticationService instance;
    private final AdminService adminService;
    private final UserService userService;
    private final LoginRepository loginRepo;

    private AuthenticationService(){
        userService = UserServiceImpl.getInstance();
        adminService = AdminServiceImpl.getInstance();
        loginRepo = LoginRepositoryImpl.getInstance();
    }

    public static AuthenticationService getInstance(){
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public boolean signup(){
        System.out.println(DesignHelper.printDesign(75, '*', "Enter -1 to cancel"));
        String email;
        do {
            email = InputHelper.getInput("Enter Email: ").toLowerCase();
            if (ValidateHelper.validateEmail(email)) break;
            else System.out.println("Not an Valid email!!");
        } while (true);
        if (userService.alreadyExists(email)) {
            System.out.println(DesignHelper.printDesign(75, '#', "User Already Exist! -> Moves to Sign-in"));
            return this.login(email);
        }
        return signup(email);
    }

    private boolean signup(String email){
        String password;
        do{
            password = InputHelper.getInput("Enter Password: ");
            if(ValidateHelper.validatePassword(password))  break;
            else System.out.println("Password should contain atleast:\n2 Captial Alphabet\n2 Small Alphabet\n2 Number");
        }while (true);
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
            if (ValidateHelper.validateMobile(number)) break;
            else System.out.println("Invalid Number:: Should be 10 numbers that starts with 6 | 7 | 8 | 9");
        } while (true);
        userService.addUser(ZUser.Buyer.newBuilder()
                        .setProfile(ZUser.BaseUser.newBuilder()
                                .setEmail(email)
                                .setOldPasswords(0, PasswordEncoderService.encode(password))
                                .setPassword(PasswordEncoderService.encode(password))
                                .setName(name)
                                .setMobile(number)
                                .build())
                .build());
        loginRepo.login(email, false);
        return true;
    }

    public boolean login(){
        System.out.println(DesignHelper.printDesign(75, '*', "Enter -1 to cancel"));
        String email = InputHelper.getInput("Enter Email: ").toLowerCase();
        if(userService.alreadyExists(email))
            return this.login(email);
        else {
            System.out.println(DesignHelper.printDesign(75, '#', "User Doesn't Exists\n Creating new account"));
            return this.signup(email);
        }
    }

    private boolean login(String email){
        String password = InputHelper.getInput("Enter Password: ");
        if (PasswordEncoderService.match(password, userService.getByEmail(email).getProfile().getPassword())) {
            loginRepo.login(email, false);
            return true;
        }
        else {
            System.out.println("Password Doesn't Match");
            return login(email);
        }
    }

    public void logout(){
        loginRepo.logout();
    }

    public boolean adminLogin(){
        System.out.println(DesignHelper.printDesign(75, '*', "Enter -1 to cancel"));
        String email;
        do{
            email = InputHelper.getInput("Enter email: ");
            if(email.equalsIgnoreCase(adminService.getAdmin().getProfile().getEmail()))  break;
            System.out.println("Invalid Admin Email!!");
        }while (true);
        do{
            String password = InputHelper.getInput("Enter Password: ");
            ZUser.Admin adminDet = adminService.getAdmin();
            if(PasswordEncoderService.match(password, adminDet.getProfile().getPassword())){
                loginRepo.login(email, true);
                if(adminDet.getChangePassOnLogin()){
                    System.out.println("You logged in with generated password! Please change your password");
                    try{
                        adminPassChange(true);
                        adminService.changePasswordState(false);
                    }
                    catch (IllegalStateException ex){
                        System.out.println(ex.getMessage());
                        return false;
                    }
                }
                return true;
            }
            System.out.println("Incorrect Password!");
        }while (true);
    }

    public String getLoggedIn(){
        return loginRepo.getInfo().getLoggedIn();
    }

    public boolean isAdmin(){
        return loginRepo.getInfo().getAdmin();
    }

    public void adminPassChange(boolean firstChange){
        if(!firstChange){
            do{
                String password = InputHelper.getInput("Enter Password: ");
                ZUser.Admin adminDet = adminService.getAdmin();
                if(PasswordEncoderService.match(password, adminDet.getProfile().getPassword())){
                    break;
                }
                System.out.println("Incorrect Password!");
            }while (true);
        }
        String password;
        do{
            password = InputHelper.getInput("Enter Password: ");
            if(ValidateHelper.validatePassword(password)) {
                if(!adminService.isOldPassword(password))
                    break;
                else
                    System.out.println("Your password shouldn't be equal to your last 3 passwords!");
            }
            else System.out.println("Password should contain atleast:\n2 Captial Alphabet\n2 Small Alphabet\n2 Number");
        }while (true);
        String rePass;
        do {
            rePass = InputHelper.getInput("Enter Password Again: ");
            if (password.equals(rePass)) break;
            else System.out.println("Password Doesn't match");
        } while (true);
        try {
            adminService.changePassword(password);
        }
        catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
            adminPassChange(firstChange);
        }
    }

    public void changePassword(){
        do{
            String curPass = InputHelper.getInput("Current Password: ");
            ZUser.Buyer user = userService.getUserById(getLoggedIn());
            if(PasswordEncoderService.match(curPass, user.getProfile().getPassword())){
                break;
            }
            System.out.println("Incorrect Password!");
        }while (true);
        String password;
        do{
            password = InputHelper.getInput("New Password: ");
            if(ValidateHelper.validatePassword(password)) {
                if(!userService.isOldPassword(getLoggedIn(), password))
                    break;
                else
                    System.out.println("Your password shouldn't be equal to your last 3 passwords!");
            }
            else System.out.println("Password should contain atleast:\n2 Captial Alphabets\n2 Small Alphabets\n2 Numbers\nNo space");
        }while (true);
        String rePass;
        do {
            rePass = InputHelper.getInput("New Password Again: ");
            if (password.equals(rePass)) break;
            else System.out.println("Password Doesn't match");
        } while (true);
        try{
            userService.changePassword(getLoggedIn(), PasswordEncoderService.encode(password));
            System.out.println("Password Changed Successfully");
        }
        catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
            changePassword();
        }
    }
}
