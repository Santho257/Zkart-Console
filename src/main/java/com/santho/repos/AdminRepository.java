package com.santho.repos;

import com.santho.proto.ZUser;

import java.io.IOException;

public interface AdminRepository {

    ZUser.Admin getAdmin() throws IOException;

    void update(ZUser.Admin updated) throws IOException;
}
