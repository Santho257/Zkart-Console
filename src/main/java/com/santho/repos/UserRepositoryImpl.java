package com.santho.repos;

import com.santho.proto.ZUser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository{
    private static UserRepositoryImpl instance;
    private final File userFile;

    private UserRepositoryImpl() throws IOException {
        this.userFile = new File("public/db/zusers_db.protobuf");
        if(!this.userFile.exists()){
            this.readFromFirstFile();
        }
    }

    public static UserRepositoryImpl getInstance() throws IOException {
        if (instance == null) {
            instance = new UserRepositoryImpl();
        }
        return instance;
    }

    private void readFromFirstFile() throws IOException {
        File user = new File("src/main/resources/zusers_db.txt");
        StringBuilder sb = new StringBuilder();
        try(FileReader fr = new FileReader(user)) {
            int ch;
            while ((ch = fr.read()) != -1) {
                sb.append((char) ch);
            }
        }
        String[] users = sb.toString().split("\n");
        List<ZUser.User> userList = new ArrayList<>();
        for (int i = 1; i < users.length; i++) {
            String[] userDet = users[i].split(" ");
            ZUser.User tempUser = ZUser.User
                    .newBuilder()
                    .setEmail(userDet[0])
                    .setPassword(userDet[1])
                    .setName(userDet[2])
                    .setMobile(userDet[3])
                    .build();
            userList.add(tempUser);
        }
        ZUser.ZUsers zuser = ZUser.ZUsers
                .newBuilder()
                .addAllUsers(userList)
                .build();
        try(FileOutputStream userOS = new FileOutputStream(this.userFile)){
            zuser.writeTo(userOS);
        }
    }

    @Override
    public List<ZUser.User> getUsers() throws IOException {
        try(FileInputStream userIS = new FileInputStream(userFile)){
            return new ArrayList<>(ZUser.ZUsers.parseFrom(userIS).getUsersList());
        }
    }

    @Override
    public boolean alreadyExists(String email) throws IOException {
        try(FileInputStream userIS = new FileInputStream(userFile)){
            List<ZUser.User> available = new ArrayList<>(ZUser.ZUsers.parseFrom(userIS).getUsersList());
            for(ZUser.User usr : available){
                if(usr.getEmail().equalsIgnoreCase(email)){
                    return true;
                }
            }
            return false;
        }
    }
    @Override
    public ZUser.User getByEmail(String email) throws IOException {
        try(FileInputStream userIS = new FileInputStream(userFile)){
            List<ZUser.User> available = new ArrayList<>(ZUser.ZUsers.parseFrom(userIS).getUsersList());
            for(ZUser.User usr : available){
                if(usr.getEmail().equalsIgnoreCase(email)){
                    return usr;
                }
            }
            throw new IllegalArgumentException(email + " doesn't exists");
        }
    }

    @Override
    public void addUser(ZUser.User user) throws IOException {
        try(FileInputStream userIS = new FileInputStream(userFile)) {
            ZUser.ZUsers users = ZUser.ZUsers.newBuilder()
                    .mergeFrom(userIS).addUsers(user).build();
            try(FileOutputStream userOS = new FileOutputStream(userFile)) {
                users.writeTo(userOS);
            }
        }
    }
}
