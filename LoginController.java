package com.flightbooking.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilità per l'hashing delle password con SHA-256.
 * Non usare mai password in chiaro nel database!
 */
public class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Calcola l'hash SHA-256 di una password.
     * @param password la password in chiaro
     * @return stringa esadecimale dell'hash
     */
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 non disponibile", e);
        }
    }

    /**
     * Verifica se una password corrisponde a un hash.
     */
    public static boolean verifica(String password, String hash) {
        return hash(password).equals(hash);
    }
}
