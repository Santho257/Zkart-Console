package com.santho.services.user;

import com.santho.proto.ZUser;
import com.santho.repos.UserRepository;
import com.santho.repos.UserRepositoryImpl;
import com.santho.services.PasswordEncoderService;
import com.santho.services.baseuser.BaseUserService;

import java.util.List;

public class UserServiceImpl extends BaseUserService implements UserService {
    private static UserServiceImpl instance;
    private final UserRepository userRepository;

    private UserServiceImpl(){
        userRepository = UserRepositoryImpl.getInstance();
    }

    public static UserServiceImpl getInstance() {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }


    @Override
    public void addUser(ZUser.Buyer user) {
        userRepository.addUser(user);
    }

    @Override
    public ZUser.Buyer getUserById(String email) {
        return userRepository.getByEmail(email);
    }

    @Override
    public void changePassword(String email, String newPassword) {
        ZUser.Buyer user = userRepository.getByEmail(email);
        List<ZUser.Buyer> allUsers = userRepository.getUsers();
        int changeIndex = allUsers.indexOf(user);
        ZUser.Buyer updatedBuyer = user.toBuilder()
                .setProfile(super.checkPassword(user.getProfile(), newPassword))
                .build();
        userRepository.updateUser(changeIndex, updatedBuyer);
    }

    @Override
    public boolean alreadyExists(String email) {
        List<ZUser.Buyer> available = userRepository.getUsers();
        for(ZUser.Buyer usr : available){
            if(usr.getProfile().getEmail().equalsIgnoreCase(email)){
                return true;
            }
        }
        return false;
    }

    @Override
    public ZUser.Buyer getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    @Override
    public boolean isOldPassword(String email, String password){
        ZUser.Buyer user = getUserById(email);
        return super.isOldPassword(user.getProfile(), PasswordEncoderService.encode(password));
    }
}
