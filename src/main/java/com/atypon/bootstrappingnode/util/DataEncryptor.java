package com.atypon.bootstrappingnode.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class DataEncryptor {

    private static final byte[] KEY = Base64.getUrlDecoder().decode("4sLUBb8wsUTkWx6eof7WdFz9Phf22joOlzYJ_IbWgqq");
    private static final String ALGORITHM = "AES";
    private static final String DELIMITER = "𝄞";

    public static String encrypt(String... data) throws Exception {
        String dataToEncrypt = String.join(DELIMITER, data);
        SecretKeySpec keySpec = new SecretKeySpec(KEY, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(dataToEncrypt.getBytes());
        // Encode the bytes to a Base64 URL-safe string
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
    }

}
