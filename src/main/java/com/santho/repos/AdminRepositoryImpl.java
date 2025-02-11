package com.santho.repos;

import com.santho.proto.ZAdmin;

import java.io.*;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class AdminRepositoryImpl implements AdminRepository {
    private static AdminRepositoryImpl instance;
    private final File adminFile;

    private AdminRepositoryImpl() throws IOException {
        this.adminFile = new File("public/db/zadmin_db.protobuf");
        if (!adminFile.exists()) {
            try (FileOutputStream adminOS = new FileOutputStream(adminFile)) {
                ZAdmin.Admin admin = ZAdmin.Admin.newBuilder().setEmail("admin@zoho.com").setPassword("yzaaz")
                        .setChangePassOnLogin(true)
                        .addAllOldPasswords(List.of(new String[]{"yzaaz"}))
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
    public ZAdmin.Admin getAdmin() throws IOException {
        try (FileInputStream adminIS = new FileInputStream(adminFile)) {
            return ZAdmin.Admin.parseFrom(adminIS);
        }
    }

    @Override
    public void changePasswordState(boolean change) throws IOException {
        try (FileInputStream adminIS = new FileInputStream(adminFile)) {
            ZAdmin.Admin admin = ZAdmin.Admin.parseFrom(adminIS).toBuilder()
                    .setChangePassOnLogin(change).build();
            try (FileOutputStream adminOS = new FileOutputStream(adminFile)) {
                admin.writeTo(adminOS);
            }
        }
    }

    @Override
    public void changePassword(String password) throws IOException {
        try (FileInputStream adminIS = new FileInputStream(adminFile)) {
            ZAdmin.Admin admin = ZAdmin.Admin.parseFrom(adminIS).toBuilder()
                    .setPassword(password).build();
            Queue<String> oldPasswords = new ArrayDeque<>(admin.toBuilder().getOldPasswordsList());
            for (String oldPass : oldPasswords) {
                if (oldPass.equals(password))
                    throw new IllegalArgumentException("Your password cannot be equal to your last 3 password");
            }
            if (oldPasswords.size() >= 3)
                oldPasswords.poll();
            oldPasswords.offer(password);
            try (FileOutputStream adminOS = new FileOutputStream(adminFile)) {
                admin.writeTo(adminOS);
            }
        }
    }
}
