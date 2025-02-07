package com.santho.services;

import com.santho.helpers.ArrayToStringHelper;

public class PasswordEncoderService {
    public static String encode(String rawPassword){
        char[] rpArr = rawPassword.toCharArray();
        for (int i = 0; i < rpArr.length; i++) {
            if( (rpArr[i] >= 'A' && rpArr[i] < 'Z') ||
                (rpArr[i] >= 'a' && rpArr[i] < 'z') ||
                (rpArr[i] >= '0' && rpArr[i] < '9')
            )   rpArr[i] += 1;
            else if (rpArr[i] == 'Z')   rpArr[i] = 'A';
            else if (rpArr[i] == 'z')   rpArr[i] = 'a';
            else if(rpArr[i] == '9')    rpArr[i] = '0';
        }
        return ArrayToStringHelper.toString(rpArr);
    }

    public static boolean match(String rawPassword, String encoded){
        String rpEncoded = encode(rawPassword);
        return rpEncoded.equals(encoded);
    }
}
