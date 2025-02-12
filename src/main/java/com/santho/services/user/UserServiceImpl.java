package com.santho.services.user;

import com.santho.proto.ZUser;
import com.santho.repos.UserRepository;
import com.santho.repos.UserRepositoryImpl;
import com.santho.services.baseuser.BaseUserService;

import java.io.IOException;
import java.util.List;

public class UserServiceImpl extends BaseUserService implements UserService {
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
    public void addUser(ZUser.Buyer user) throws IOException {
        userRepository.addUser(user);
    }

    @Override
    public ZUser.Buyer getUserById(String email) throws IOException {
        return userRepository.getByEmail(email);
    }

    @Override
    public void changePassword(String email, String newPassword) throws IOException {
        ZUser.Buyer user = userRepository.getByEmail(email);
        List<ZUser.Buyer> allUsers = userRepository.getUsers();
        int changeIndex = allUsers.indexOf(user);
        ZUser.Buyer updatedBuyer = user.toBuilder()
                .setProfile(super.checkPassword(user.getProfile(), newPassword))
                .build();
        userRepository.updateUser(changeIndex, updatedBuyer);
    }

    @Override
    public boolean alreadyExists(String email) throws IOException {
        List<ZUser.Buyer> available = userRepository.getUsers();
        for(ZUser.Buyer usr : available){
            if(usr.getProfile().getEmail().equalsIgnoreCase(email)){
                return true;
            }
        }
        return false;
    }

    @Override
    public ZUser.Buyer getByEmail(String email) throws IOException {
        return userRepository.getByEmail(email);
    }
}
