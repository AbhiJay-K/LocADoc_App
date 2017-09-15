package com.locadoc_app.locadoc;

/**
 * Created by AbhiJay_PC on 12/9/2017.
 */

public interface SignUPPresenterInterface {
    int isValidPassword();
    int checkPasswordSame();
    int checkEmail();
    boolean checkFName();
    boolean checkLName();
    int CheckContactNum();
    void SignUpUser();
}
