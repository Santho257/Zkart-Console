package com.santho.services.admin;

import com.santho.proto.ZUser;

public interface AdminService {
    void changePassword(String newPassword);

    ZUser.Admin getAdmin();

    void changePasswordState(boolean change);

    boolean isOldPassword(String password);
}
