package com.locadoc_app.locadoc.helper;

/**
 * Created by AbhiJay_PC on 12/9/2017.
 */

public class EmailValidation {
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
