package com.locadoc_app.locadoc.UI.PasswordRecovery;

/**
 * Created by DainoMix on 9/18/2017.
 */

public interface PasswordRecoveryPresenterInterface {
    void continueTask(String email, String password, String verifiCode);
    int isValidPassword();
    int checkPasswordSame();
    void forgotPassword(String email);


}
