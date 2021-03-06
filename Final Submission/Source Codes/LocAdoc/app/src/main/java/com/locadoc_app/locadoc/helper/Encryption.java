package com.locadoc_app.locadoc.helper;

import android.util.Base64;
import java.io.InputStream;
import java.io.OutputStream;
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
	public SecretKeyFactory factory;
	
	private Encryption(String pass, String salt)
	{
		try{
			AES = Cipher.getInstance("AES/CBC/PKCS5Padding");
			randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch(Exception e){}
		setKey(pass,salt);
	}

	public SecretKey getCurrentKey(){
		return key;
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
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 15000, 128);
			byte[] hash = factory.generateSecret(spec).getEncoded();
			key = new SecretKeySpec(hash, "AES");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setCurrKey(SecretKey key)
	{
		this.key = key;
	}
	
	public IvParameterSpec generateIV ()
	{
		
		byte[] iv = new byte[AES.getBlockSize()];
		randomSecureRandom.nextBytes(iv);
		IvParameterSpec ivParams = new IvParameterSpec(iv);
		return ivParams;
	}
	
	public void encryptFile (InputStream fin, OutputStream fout)
	{
		try{
			IvParameterSpec ivspec = generateIV();
			AES.init(Cipher.ENCRYPT_MODE, key, ivspec);
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
		}
		catch (Exception e){}
	}
	
	public void decryptFile (InputStream fin, OutputStream fout)
	{
		try{
			// read attached IV
			byte[] iv = new byte[AES.getBlockSize()];
			fin.read(iv);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			AES.init(Cipher.DECRYPT_MODE, key, ivspec);
			CipherOutputStream cos = new CipherOutputStream(fout, AES);
			byte [] buffer = new byte [8192];  
			int r;  
			while ((r = fin.read(buffer)) > 0) 
			{  
				cos.write(buffer, 0, r);  
			}  
			cos.close();
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
			
			byte[] result = AES.doFinal(input.getBytes("UTF-8"));
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
			byte[] iv = Base64.decode(b,Base64.DEFAULT);
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			AES.init(Cipher.DECRYPT_MODE, key, ivspec);
			input = input.substring(24);
	        byte[] result = AES.doFinal(Base64.decode(input.getBytes("UTF-8"),Base64.DEFAULT));
	        pText = new String(result);
		}
		catch (Exception e) {e.printStackTrace();}
		
		return pText;
	}

	public static void clear(){
		encryption = null;
	}
}
