package com.santho.repos;

import com.santho.proto.ZUser;

import java.util.List;

public interface UserRepository {
    List<ZUser.User> getUsers();
}
