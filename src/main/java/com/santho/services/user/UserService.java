package com.santho.services.user;

import com.santho.proto.ZUser;

import java.io.IOException;

public interface UserService {
    void addUser(ZUser.Buyer user) throws IOException;

    ZUser.Buyer getUserById(String email) throws IOException;

    void changePassword(String email, String encode) throws IOException;

    boolean alreadyExists(String email) throws IOException;

    ZUser.Buyer getByEmail(String email) throws IOException;
}
