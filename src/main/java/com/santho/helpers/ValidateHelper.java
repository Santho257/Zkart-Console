package com.santho.helpers;

import java.util.regex.Pattern;

public class ValidateHelper {
    public static boolean validatePassword(String password){
        return Pattern
                .compile("^(?=.*\\d.*\\d)(?=.*[a-z].*[a-z])(?=.*[A-Z].*[A-Z])(?!.*[ ]).{6,}$")
                .matcher(password).matches();
    }

    public static boolean validateMobile(String number) {
        return Pattern
                .compile("^[6789]{1}[0-9]{9}$", Pattern.CASE_INSENSITIVE)
                .matcher(number).matches();
    }

    public static boolean validateEmail(String email) {
        return Pattern
                .compile("^[a-z\\d]([a-z\\d]+\\.)*[a-z\\d]+@([a-z\\d]+\\.)+[a-z]{2,4}$", Pattern.CASE_INSENSITIVE)
                .matcher(email).matches();
    }
}
