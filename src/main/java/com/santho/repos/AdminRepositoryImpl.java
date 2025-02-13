package com.santho.repos;

import com.santho.proto.ZUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AdminRepositoryImpl implements AdminRepository {
    private static AdminRepositoryImpl instance;
    private final File adminFile;

    private AdminRepositoryImpl(){
        this.adminFile = new File("public/db/zadmin_db.protobuf");
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
            catch (IOException ex){
                throw new IllegalStateException("Error in our end. Please try again later!");
            }
        }
    }

    public static AdminRepositoryImpl getInstance(){
        if (instance == null) {
            instance = new AdminRepositoryImpl();
        }
        return instance;
    }

    @Override
    public ZUser.Admin getAdmin(){
        try (FileInputStream adminIS = new FileInputStream(adminFile)) {
            return ZUser.Admin.parseFrom(adminIS);
        }
        catch (IOException ex){
            throw new IllegalStateException("Error while fetching data. Please try again later!");
        }
    }

    @Override
    public void update(ZUser.Admin updated){
        try (FileOutputStream adminOS = new FileOutputStream(adminFile)) {
            updated.writeTo(adminOS);
        }
        catch (IOException ex){
            throw new IllegalStateException("Error while updating. Please try again later!");
        }
    }
}
