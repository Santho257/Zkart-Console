package com.santho.repos.login;

import com.santho.proto.ZLogin;

public interface LoginRepository {
    ZLogin.Login getInfo();

    void logout();

    void login(String email, boolean admin);
}
