package com.baliset.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static final String kCharset = "UTF-8";
    private static final int    kKeyLength = 16;
    private static final String kAlgorithm = "AES";

    private final SecretKeySpec secretKey;
    private final Cipher cipher;

    public static class CryptoException extends RuntimeException
    {
        public CryptoException(Throwable causedBy)
        {
            super(causedBy);
        }
    }

    public Crypto(String secret)
    {
        try {
            byte[] key = new byte[kKeyLength];
            key = fixSecret(secret, kKeyLength);
            secretKey = new SecretKeySpec(key, kAlgorithm);
            cipher = Cipher.getInstance(kAlgorithm);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
        if (s.length() < length) {
            int missingLength = length - s.length();
            for (int i = 0; i < missingLength; i++) {
                s += " ";
            }
        }
        return s.substring(0, length).getBytes(kCharset);
    }


    public String encryptString(String in)
    {
        byte[] inb = in.getBytes();
        byte[] outb = encryptBytes(inb);
        return new String(Base64.getEncoder().encode(outb));
    }

    public String decryptString(String in)
    {
        byte[] inb = Base64.getDecoder().decode(in);
        byte[] outb = decryptBytes(inb);
        return new String(outb);
    }


    public byte[] encryptBytes(byte[] input)
    {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
            return cipher.doFinal(input);
        } catch(Exception e) {
            throw new CryptoException(e);
        }

    }

    public byte[] decryptBytes(byte[] input) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(input);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

}