package com.santho.repos;

import com.santho.proto.ZUser;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserRepositoryImpl implements UserRepository{
    private static UserRepositoryImpl instance;
    private final File userFile;
    private final Map<String, ZUser.User> users;

    private UserRepositoryImpl() throws IOException {
        System.out.println("Initializing...");
        this.userFile = new File("public/db/zusers_db.protobuf");
        this.users = new HashMap<>();
        if(this.userFile.exists()){
            FileInputStream userIS = new FileInputStream(this.userFile);
            List<ZUser.User> usrList = ZUser.ZUsers.parseFrom(userIS).getUsersList();
            for(ZUser.User usr: usrList){
                this.users.put(usr.getEmail(), usr);
            }
            userIS.close();
        }
        else{
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
        System.out.println("Creating new File");
        File user = new File("src/main/resources/zusers_db.txt");
        StringBuilder sb = new StringBuilder();
        FileReader fr = new FileReader(user);
        int ch;
        while((ch = fr.read()) != -1){
            sb.append((char) ch);
        }
        String[] users = sb.toString().split("\n");
        for (int i = 1; i < users.length; i++) {
            String[] userDet = users[i].split(" ");
            ZUser.User tempUser = ZUser.User
                    .newBuilder()
                    .setEmail(userDet[0])
                    .setPassword(userDet[1])
                    .setName(userDet[2])
                    .setMobile(userDet[3])
                    .build();
            this.users.put(tempUser.getEmail(), tempUser);
        }
        ZUser.ZUsers zuser = ZUser.ZUsers
                .newBuilder()
                .addAllUsers(this.users.values())
                .build();
        FileOutputStream userOS = new FileOutputStream(this.userFile);
        zuser.writeTo(userOS);
        userOS.close();
    }

    @Override
    public List<ZUser.User> getUsers(){
        return this.users.values().stream().collect(Collectors.toList());
    }
}
