package it.samvise85.bookshelf.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.codec.Hex;

public class SHA1Digester {
	private static MessageDigest digest;
	static {
	    try {
	        digest = MessageDigest.getInstance("SHA-1");
	    } catch (NoSuchAlgorithmException e) {
	        throw new IllegalStateException("No SHA-1 algorithm available!");
	    }
	}
	
	public static String digest(String message) {
        return new String(Hex.encode(digest.digest(message.getBytes())));
	}
}
