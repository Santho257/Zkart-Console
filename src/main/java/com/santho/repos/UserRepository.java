package com.santho.repos;

import com.santho.proto.ZUser;

import java.io.IOException;
import java.util.List;

public interface UserRepository {
    List<ZUser.Buyer> getUsers();

    ZUser.Buyer getByEmail(String email);

    void addUser(ZUser.Buyer user);

    void updateUser(int index, ZUser.Buyer updated);
}