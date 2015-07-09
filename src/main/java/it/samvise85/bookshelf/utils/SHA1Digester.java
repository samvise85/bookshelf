package it.samvise85.bookshelf.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.codec.Hex;

public class SHA1Digester {

	public static String digest(String message) {
		MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No SHA-1 algorithm available!");
        }

        return new String(Hex.encode(digest.digest(message.getBytes())));
	}
}
