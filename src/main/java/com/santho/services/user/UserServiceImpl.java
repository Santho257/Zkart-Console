package com.santho.services.user;

import com.santho.proto.ZUser;
import com.santho.repos.UserRepository;
import com.santho.repos.UserRepositoryImpl;

import java.io.IOException;

public class UserServiceImpl implements UserService{
    private static UserServiceImpl instance;
    private final UserRepository userRepository;

    private UserServiceImpl() throws IOException {
        userRepository = UserRepositoryImpl.getInstance();
    }
    public static UserServiceImpl getInstance() throws IOException {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }


    @Override
    public void addUser(ZUser.User user) throws IOException {
        userRepository.addUser(user);
    }
}
