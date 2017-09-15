package com.locadoc_app.locadoc.helper;

/**
 * Created by AbhiJay_PC on 12/9/2017.
 */

public class CheckPassword {
    public static boolean PWDCheck(String password)
    {
        // Check for null, then a length less then 6 (and I really don't like the length()
        // > 10 check, that's a BAD requirement).
        if (password == null || password.length() < 8 || password.length() > 10) {
            return false;
        }
        boolean containsDigit = false;
        boolean containsUpperChar = false;
        boolean containsLowerChar = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                containsUpperChar = true;
            }
            else if (Character.isLowerCase(c)) {
                containsLowerChar = true;
            }
            else if (Character.isDigit(c)) {
                containsDigit = true;
            }

            if (containsUpperChar && containsLowerChar && containsDigit) {
                return true;
            }
        }
        return false;
    }
}
