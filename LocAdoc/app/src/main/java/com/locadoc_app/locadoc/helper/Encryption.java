package com.locadoc_app.locadoc.helper;

import android.util.Base64;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
	public SecretKey key;
	public Cipher AES;
	public SecureRandom randomSecureRandom;
	public static Encryption encryption;
	
	private Encryption(String pass, String salt)
	{
		try{
			AES = Cipher.getInstance("AES/CBC/PKCS5Padding");
			randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
		} catch(Exception e){}
		setKey(pass,salt);
	}
	
	public static Encryption getInstance(String pass, String salt)
	{
		if (encryption == null) {
            encryption = new Encryption(pass, salt);
        }

        return encryption;
	}
	
	public void setKey(String password, String salt)
	{
		try
		{
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 128);
			SecretKey tmp = factory.generateSecret(spec);
			key = new SecretKeySpec(tmp.getEncoded(), "AES");
		} catch (Exception e)
		{
			
		}
	}
	
	public IvParameterSpec generateIV ()
	{
		
		byte[] iv = new byte[AES.getBlockSize()];
		randomSecureRandom.nextBytes(iv);
		IvParameterSpec ivParams = new IvParameterSpec(iv);
		return ivParams;
	}
	
	public void encryptFile (String fileName, String fileName2)
	{
		try{
			IvParameterSpec ivspec = generateIV();
			AES.init(Cipher.ENCRYPT_MODE, key, ivspec);
			FileInputStream fin = new FileInputStream(fileName);
			FileOutputStream fout = new FileOutputStream(fileName2);
			CipherInputStream cis = new CipherInputStream(fin, AES);
			
			// attach random IV
			fout.write(ivspec.getIV());
			
			byte [] buffer = new byte [8192];  
			int r;  
			while ((r = cis.read(buffer)) > 0) 
			{  
				fout.write(buffer, 0, r);  
			}  
			cis.close();  
			fin.close();  
			fout.close();
		}
		catch (Exception e){e.printStackTrace();}
	}
	
	public void decryptFile (String fileName, String fileName2)
	{
		try{
			FileInputStream fin = new FileInputStream(fileName);
			// read attached IV
			byte[] iv = new byte[AES.getBlockSize()];
			fin.read(iv);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			AES.init(Cipher.DECRYPT_MODE, key, ivspec);
			FileOutputStream fout = new FileOutputStream(fileName2);
			CipherOutputStream cos = new CipherOutputStream(fout, AES);
			byte [] buffer = new byte [8192];  
			int r;  
			while ((r = fin.read(buffer)) > 0) 
			{  
				cos.write(buffer, 0, r);  
			}  
			cos.close();  
			fin.close();  
			fout.close();
		}
		catch (Exception e){}
	}
	
	public String encryptString (String input)
	{
		IvParameterSpec ivspec = generateIV();
		String cText = Base64.encodeToString(ivspec.getIV(), Base64.DEFAULT);
		try
		{
			AES.init(Cipher.ENCRYPT_MODE, key, ivspec);
			
			byte[] result = AES.doFinal(input.getBytes());
			cText += Base64.encodeToString(result, Base64.DEFAULT);
		}
		catch (Exception e) {}
		
		return cText;
	}
	
	public String decrypttString (String input)
	{
		String pText = "";
		try
		{

			byte[] b = input.substring(0, 24).getBytes("UTF-8");
			byte[] iv = Base64.encodeToString(b,Base64.DEFAULT).getBytes("UTF-8");
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			AES.init(Cipher.DECRYPT_MODE, key, ivspec);
			input = input.substring(24);
	        byte[] result = AES.doFinal(Base64.encodeToString(input.getBytes("UTF-8"),Base64.DEFAULT).getBytes("UTF-8"));
	        pText = new String(result);
		}
		catch (Exception e) {e.printStackTrace();}
		
		return pText;
	}
	
	public static void main (String[] args)
	{
		Encryption e = Encryption.getInstance("pass123", "12364");
		e.encryptFile("locadoc_logo.png", "CipherText1");
		System.out.println("FINISH ENCRYPTION");
		e.decryptFile("CipherText1", "PlainText1.png");
		System.out.println("FINISH DECRYPTION");
		String res = e.encryptString("testing!!!");
		System.out.println("Cipher text: " + res);
		res = e.decrypttString(res);
		System.out.println("Plain text: " + res);
	}
}
