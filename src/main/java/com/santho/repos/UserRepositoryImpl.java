package com.santho.repos;

import com.santho.proto.ZUser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository{
    private static UserRepositoryImpl instance;
    private final File userFile;

    private UserRepositoryImpl(){
        this.userFile = new File("public/db/zusers_db.protobuf");
        if(!this.userFile.exists()){
            this.readFromFirstFile();
        }
    }

    public static UserRepositoryImpl getInstance(){
        if (instance == null) {
            instance = new UserRepositoryImpl();
        }
        return instance;
    }

    private void readFromFirstFile(){
        File user = new File("src/main/resources/zusers_db.txt");
        StringBuilder sb = new StringBuilder();
        try(FileReader fr = new FileReader(user)) {
            int ch;
            while ((ch = fr.read()) != -1) {
                sb.append((char) ch);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Error From our side. Please try again later!");
        }
        String[] users = sb.toString().split("\n");
        List<ZUser.Buyer> userList = new ArrayList<>();
        for (int i = 1; i < users.length; i++) {
            String[] userDet = users[i].split(" ");
            ZUser.Buyer tempUser = ZUser.Buyer
                    .newBuilder()
                    .setProfile(ZUser.BaseUser.newBuilder()
                            .setEmail(userDet[0])
                            .setPassword(userDet[1])
                            .addAllOldPasswords(List.of(new String[]{userDet[1]}))
                            .setName(userDet[2])
                            .setMobile(userDet[3])
                            .build())
                    .build();
            userList.add(tempUser);
        }
        ZUser.ZBuyers zuser = ZUser.ZBuyers
                .newBuilder()
                .addAllBuyer(userList)
                .build();
        try(FileOutputStream userOS = new FileOutputStream(this.userFile)){
            zuser.writeTo(userOS);
        }
        catch (IOException e) {
            throw new IllegalStateException("Error from our side. Please try again later!");
        }
    }

    @Override
    public List<ZUser.Buyer> getUsers(){
        try(FileInputStream userIS = new FileInputStream(userFile)){
            return new ArrayList<>(ZUser.ZBuyers.parseFrom(userIS).getBuyerList());
        }
        catch (IOException e) {
            throw new IllegalStateException("Error Fetching users. Please try again later!");
        }
    }

    @Override
    public ZUser.Buyer getByEmail(String email){
        try(FileInputStream userIS = new FileInputStream(userFile)){
            List<ZUser.Buyer> available = new ArrayList<>(ZUser.ZBuyers.parseFrom(userIS).getBuyerList());
            for(ZUser.Buyer usr : available){
                if(usr.getProfile().getEmail().equalsIgnoreCase(email)){
                    return usr;
                }
            }
            throw new IllegalStateException(email + " doesn't exists");
        }
        catch (IOException e) {
            throw new IllegalStateException("Error Fetching user. Please try again later!");
        }
    }

    @Override
    public void addUser(ZUser.Buyer user) {
        try(FileInputStream userIS = new FileInputStream(userFile)) {
            ZUser.ZBuyers users = ZUser.ZBuyers.newBuilder()
                    .mergeFrom(userIS).addBuyer(user).build();
            try(FileOutputStream userOS = new FileOutputStream(userFile)) {
                users.writeTo(userOS);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Error adding user. Please try again later!");
        }
    }

    @Override
    public void updateUser(int index, ZUser.Buyer updated){
        try(FileInputStream userIS = new FileInputStream(userFile)) {
            ZUser.ZBuyers users = ZUser.ZBuyers.parseFrom(userIS).toBuilder()
                    .setBuyer(index, updated)
                    .build();
            try (FileOutputStream userOS = new FileOutputStream(userFile)){
                users.writeTo(userOS);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Error updating user. Please try again later!");
        }
    }
}
