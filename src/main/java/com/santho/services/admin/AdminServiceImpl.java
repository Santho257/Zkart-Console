package com.santho.services.admin;

import com.santho.proto.ZUser;
import com.santho.repos.AdminRepository;
import com.santho.repos.AdminRepositoryImpl;
import com.santho.services.PasswordEncoderService;
import com.santho.services.baseuser.BaseUserService;

public class AdminServiceImpl extends BaseUserService implements AdminService{
    private static AdminServiceImpl instance;
    private final AdminRepository adminRepository;
    private AdminServiceImpl(){
        this.adminRepository = AdminRepositoryImpl.getInstance();
    }

    public static AdminServiceImpl getInstance(){
        if (instance == null) {
            instance = new AdminServiceImpl();
        }
        return instance;
    }
    @Override
    public void changePassword(String newPassword){
        ZUser.Admin admin = adminRepository.getAdmin();
        ZUser.Admin updated = admin.toBuilder()
                .setProfile(super.checkPassword(admin.getProfile(), PasswordEncoderService.encode(newPassword)))
                .build();
        adminRepository.update(updated);
    }

    @Override
    public ZUser.Admin getAdmin(){
        return adminRepository.getAdmin();
    }

    @Override
    public void changePasswordState(boolean change){
        ZUser.Admin admin = adminRepository.getAdmin();
        ZUser.Admin updated = admin.toBuilder()
                .setChangePassOnLogin(change)
                .build();
        adminRepository.update(updated);
    }

    @Override
    public boolean isOldPassword(String password){
        ZUser.Admin admin = adminRepository.getAdmin();
        return super.isOldPassword(admin.getProfile(), PasswordEncoderService.encode(password));
    }
}
