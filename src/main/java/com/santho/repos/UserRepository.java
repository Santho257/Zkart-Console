package com.santho.repos;

import com.santho.proto.ZUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface UserRepository {
    List<ZUser.User> getUsers() throws IOException;
}
