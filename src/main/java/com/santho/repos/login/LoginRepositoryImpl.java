package com.santho.repos.login;

import com.santho.proto.ZLogin;

import java.io.*;

public class LoginRepositoryImpl implements LoginRepository{
    private static LoginRepositoryImpl instance;
    private final File loginFile;
    private LoginRepositoryImpl(){
        loginFile = new File("public/db/zlogin_db.protobuf");
        if(!loginFile.exists()){
            try {
                new FileOutputStream(loginFile).close();
            }
            catch (IOException ex){
                throw new IllegalStateException("Error from our side! Please try again later!");
            }
        }
        
    }
    public static LoginRepositoryImpl getInstance(){
        if (instance == null) {
            instance = new LoginRepositoryImpl();
        }
        return instance;
    }

    @Override
    public ZLogin.Login getInfo(){
        try(FileInputStream loginIS = new FileInputStream(loginFile)) {
            return ZLogin.Login.parseFrom(loginIS);
        }
        catch (IOException e) {
            throw new IllegalStateException("");
        }
    }

    @Override
    public void logout(){
        try(FileOutputStream logOS = new FileOutputStream(loginFile)){
            ZLogin.Login login = ZLogin.Login.newBuilder().build();
            login.writeTo(logOS);
        }
        catch (IOException e) {
            throw new IllegalStateException("Error logging out. Please try again later!");
        }
    }

    @Override
    public void login(String email, boolean admin){
        try(FileOutputStream logOS = new FileOutputStream(loginFile)){
            ZLogin.Login login = ZLogin.Login.newBuilder()
                    .setLoggedIn(email)
                    .setAdmin(admin)
                    .build();
            login.writeTo(logOS);
        }
        catch (IOException e) {
            throw new IllegalStateException("Error while login. Please try again later!");
        }
    }
}
