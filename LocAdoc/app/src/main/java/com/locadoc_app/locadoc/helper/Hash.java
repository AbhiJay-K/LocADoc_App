package com.locadoc_app.locadoc.helper;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Created by AbhiJay_PC on 24/9/2017.
 */

public class Hash {
    public static String SecureRandomGen()
    {
        String Result = new String();
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[20];
            sr.nextBytes(salt);
            for (int i = 0; i < 20; i++) {
                Result += (salt[i] & 0x00FF);
                System.out.print(" ");
            }
        }
        catch (Exception cnse) {
            cnse.printStackTrace();
        }
        return Result;
    }
    public static String Hash(String StrTObeHashed,String salt)
    {
        MessageDigest digest;
        byte[] hash = new byte[]{0};
        try {
            String data = StrTObeHashed + salt;
            digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(data.getBytes("UTF-8"));
        } catch (Exception cnse) {
            cnse.printStackTrace();
        }
        Hex h = new Hex();
        return new String(h.encode(hash));
    }
}
