package com.locadoc_app.locadoc.UI.Setting;

import android.view.View;

import com.locadoc_app.locadoc.Model.User;

/**
 * Created by user on 11/2/2017.
 */

public interface SettingActivityViewInterface {
    void changeUserName();
    void openResetPasswordActivity();
    void confirmRecover();
    void recoverAllMissingFiles();
    void updateDownloadProgress();
    void beginDownload(String key, java.io.File file);

    void exit();

    void setProfileInitial(String inital);
    void setUserNameTextView(String str, User user);
    void setLabelFirstName(String str, View v);
    void setLabelLastName(String str, View v);
    void setLabelFirstLastName(String str, View v);
    void setLabelNameOK(String str, View v);
    void showProgressDialog(String title, String msg);
    void dismissProgresDialog();




}
