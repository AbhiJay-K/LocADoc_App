package com.locadoc_app.locadoc.UI.Setting;


import com.locadoc_app.locadoc.Model.User;

/**
 * Created by user on 10/17/2017.
 */

public class SettingActivityPresenter {

    private SettingActivity activity;
    private User user;

    public SettingActivityPresenter(SettingActivity activity) {
        this.activity = activity;
    }

    public void profileName(String firstName,String lastName) {
        String nameInitial = "";
        if(!lastName.isEmpty()) {
            nameInitial = Character.toString(lastName.charAt(0));
        }
        String[] arrayOfFirst =  firstName.split("\\s+");

        for(int i=0; i<arrayOfFirst.length; i++)
            nameInitial = nameInitial.concat(Character.toString(arrayOfFirst[i].charAt(0)));

        activity.setProfileInitial(nameInitial);
    }

}