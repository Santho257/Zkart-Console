package com.santho.services.user;

import com.santho.proto.ZUser;

import java.io.IOException;

public interface UserService {
    void addUser(ZUser.User user) throws IOException;
}
