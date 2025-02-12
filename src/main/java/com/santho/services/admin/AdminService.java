package com.santho.services.admin;

import com.santho.proto.ZUser;

import java.io.IOException;

public interface AdminService {
    void changePassword(String newPassword) throws IOException;

    ZUser.Admin getAdmin() throws IOException;

    void changePasswordState(boolean change) throws IOException;

    boolean isOldPassword(String password) throws IOException;
}
