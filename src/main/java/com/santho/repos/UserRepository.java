package com.santho.repos;

import com.santho.proto.ZUser;

import java.io.IOException;
import java.util.List;

public interface UserRepository {
    List<ZUser.Buyer> getUsers() throws IOException;

    ZUser.Buyer getByEmail(String email) throws IOException;

    void addUser(ZUser.Buyer user) throws IOException;

    void updateUser(int index, ZUser.Buyer updated) throws IOException;
}