package com.santho.repos;

import com.santho.proto.ZUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class AdminRepositoryImpl implements AdminRepository {
    private static AdminRepositoryImpl instance;
    private final File adminFile;

    private AdminRepositoryImpl() throws IOException {
        this.adminFile = new File("public/db/ZUser_db.protobuf");
        if (!adminFile.exists()) {
            try (FileOutputStream adminOS = new FileOutputStream(adminFile)) {
                ZUser.Admin admin = ZUser.Admin.newBuilder()
                        .setProfile(ZUser.BaseUser.newBuilder()
                                .setEmail("admin@zoho.com")
                                .setPassword("yzaaz")
                                .addAllOldPasswords(List.of(new String[]{"yzaaz"}))
                                .build())
                        .setChangePassOnLogin(true)
                        .build();
                admin.writeTo(adminOS);
            }
        }
    }

    public static AdminRepositoryImpl getInstance() throws IOException {
        if (instance == null) {
            instance = new AdminRepositoryImpl();
        }
        return instance;
    }

    @Override
    public ZUser.Admin getAdmin() throws IOException {
        try (FileInputStream adminIS = new FileInputStream(adminFile)) {
            return ZUser.Admin.parseFrom(adminIS);
        }
    }

    @Override
    public void update(ZUser.Admin updated) throws IOException {
        try (FileOutputStream adminOS = new FileOutputStream(adminFile)) {
            updated.writeTo(adminOS);
        }
    }
}
