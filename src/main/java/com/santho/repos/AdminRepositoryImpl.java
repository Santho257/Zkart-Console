package com.santho.repos;

import com.santho.proto.ZAdmin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AdminRepositoryImpl implements AdminRepository{
    private static AdminRepositoryImpl instance;
    private final File adminFile;
    private AdminRepositoryImpl() throws IOException {
        this.adminFile = new File("public/db/zadmin_db.protobuf");
        if(!adminFile.exists()){
            try(FileOutputStream adminOS = new FileOutputStream(adminFile)){
                ZAdmin.Admin admin = ZAdmin.Admin.newBuilder().setEmail("admin@zoho.com").setPassword("yzaaz").build();
                admin.writeTo(adminOS);
            }
        }
    }
    public static AdminRepositoryImpl getInstance() throws IOException{
        if (instance == null) {
            instance = new AdminRepositoryImpl();
        }
        return instance;
    }

    @Override
    public ZAdmin.Admin getAdmin() throws IOException{
        try(FileInputStream adminIS = new FileInputStream(adminFile)){
            return ZAdmin.Admin.parseFrom(adminIS);
        }
    }
}
