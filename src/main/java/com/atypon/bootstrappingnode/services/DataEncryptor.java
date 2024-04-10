package com.atypon.bootstrappingnode.services;

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
        return bytesToHex(encryptedBytes);
    }

    // Convert bytes to a hexadecimal string for safer filename
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
