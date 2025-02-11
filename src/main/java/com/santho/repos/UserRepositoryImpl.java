package com.santho.repos;

import com.santho.proto.ZUser;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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
                    .addAllOldPasswords(List.of(new String[]{userDet[1]}))
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

    @Override
    public void changePassword(String email, String password) throws IOException {
        ZUser.User user = getByEmail(email);
        List<ZUser.User> allUsers = getUsers();
        int changeIndex = allUsers.indexOf(user);
        try(FileInputStream userIS = new FileInputStream(userFile)) {
            Queue<String> oldPasswords = new ArrayDeque<>(user.toBuilder().getOldPasswordsList());
            for(String oldPass: oldPasswords){
                if(password.equals(oldPass)){
                    throw new IllegalArgumentException("Your new password cannot be equal to your last 3 passwords");
                }
            }
            if(oldPasswords.size() < 3) oldPasswords.offer(password);
            else{
                oldPasswords.poll();
                oldPasswords.offer(password);
            }
            ZUser.ZUsers users = ZUser.ZUsers.parseFrom(userIS).toBuilder()
                    .setUsers(changeIndex, user
                            .toBuilder()
                            .setPassword(password)
                            .clearOldPasswords()
                            .addAllOldPasswords(oldPasswords)
                            .build())
                    .build();
            try (FileOutputStream userOS = new FileOutputStream(userFile)){
                users.writeTo(userOS);
            }
        }
    }
}
