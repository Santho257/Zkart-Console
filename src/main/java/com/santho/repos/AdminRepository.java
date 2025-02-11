package com.santho.repos;

import com.santho.proto.ZAdmin;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface AdminRepository {

    ZAdmin.Admin getAdmin() throws IOException;

    void changePasswordState(boolean change) throws IOException;

    void changePassword(String password) throws IOException;
}
