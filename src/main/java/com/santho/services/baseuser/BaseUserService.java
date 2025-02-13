package com.santho.services.baseuser;

import com.santho.proto.ZUser;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public abstract class BaseUserService {
    protected BaseUserService(){}
    protected ZUser.BaseUser checkPassword(ZUser.BaseUser profile, String newPassword){
        Queue<String> oldPasswords = new ArrayDeque<>(profile.toBuilder().getOldPasswordsList());
        if (oldPasswords.size() >= 3) oldPasswords.poll();
        oldPasswords.offer(newPassword);
        return profile.toBuilder()
                .setPassword(newPassword)
                .clearOldPasswords()
                .addAllOldPasswords(oldPasswords)
                .build();
    }

    protected boolean isOldPassword(ZUser.BaseUser profile, String password){
        Queue<String> oldPasswords = new ArrayDeque<>(profile.toBuilder().getOldPasswordsList());
        for (String oldPass : oldPasswords) {
            if (password.equals(oldPass)) {
                return true;
            }
        }
        return false;
    }
}
