package com.santho.services.user;

import com.santho.proto.ZUser;

import java.io.IOException;

public interface UserService {
    void addUser(ZUser.Buyer user);

    ZUser.Buyer getUserById(String email);

    void changePassword(String email, String encode);

    boolean alreadyExists(String email);

    ZUser.Buyer getByEmail(String email);

    boolean isOldPassword(String email, String password);
}
