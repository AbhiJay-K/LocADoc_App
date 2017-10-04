package com.locadoc_app.locadoc.Model;

/**
 * Created by Admin on 10/3/2017.
 */

public class Credential {
    private static Password PASSWORD;
    private static String email;

    public static Password getPassword (){
        return PASSWORD;
    }

    public static void setPassword (Password pass){
        PASSWORD = pass;
    }

    public static String getEmail (){
        return email;
    }

    public static void setEmail (String userEmail){
        email = userEmail;
    }
}
