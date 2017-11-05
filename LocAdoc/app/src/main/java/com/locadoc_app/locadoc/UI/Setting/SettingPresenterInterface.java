package com.locadoc_app.locadoc.UI.Setting;

import android.view.View;

import com.locadoc_app.locadoc.Model.User;

/**
 * Created by Dainomix on 11/2/2017.
 */

public interface SettingPresenterInterface {

    void profileName(String firstName,String lastName);
    int validName(String firstName, String lastName, View builderView);
    void changeToNewName(String firstName, String lastName);

    User getUser();
    String getEmail();


}
