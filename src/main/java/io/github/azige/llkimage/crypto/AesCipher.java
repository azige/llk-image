/*
 * Created 2019-1-18 15:26:28
 */
package io.github.azige.llkimage.crypto;

import java.nio.charset.Charset;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author Azige
 */
public class AesCipher {

    private Cipher cipher;
    private SecretKey key;

    public AesCipher(String password) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(new SecureRandom(password.getBytes(Charset.forName("UTF-8"))));
            key = keygen.generateKey();
            cipher = Cipher.getInstance("AES");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public byte[] encrypt(byte[] data) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public byte[] decrypt(byte[] data) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
