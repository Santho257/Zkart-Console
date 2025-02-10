package com.santho.repos;

import com.santho.proto.ZAdmin;

import java.io.IOException;

public interface AdminRepository {

    ZAdmin.Admin getAdmin() throws IOException;
}
