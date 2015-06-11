package com.thaler.miner.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.lambdaworks.crypto.SCrypt;
import com.thaler.miner.ddb.Bits256;
import com.thaler.miner.merkle.Hashable;

public class CryptoUtil {

	private static final String SHA_256 = "SHA-256";

	public static Bits256 hash(byte[] bytes) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(SHA_256);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		md.update(bytes);
		return Bits256.create(md.digest());

	}
	
	public static Bits256 scrypt(byte[] P){
		
		//byte[] P, S;
        int N, r, p, dkLen;
        String DK;
        
        
        
        //P = "password2".getBytes("UTF-8");
        //S = "NaCl".getBytes("UTF-8");
        byte[] S = P;
        N = (int)Math.pow(2, 15); // cpu 15 == 1s/h
        //r = 8; // memory
        r = 8;
        //p = 16; // parallel
        p = 1; // parallel
        dkLen = 32;

		byte[] result;
		try {
			result = SCrypt.scrypt(P, S, N, r, p, dkLen);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
		return Bits256.create(result);
	}

	public static Bits256 hash(Hashable... hashables) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(SHA_256);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		for (Hashable hashable : hashables) {
			md.update(hashable.getBytes());
		}

		return Bits256.create(md.digest());
	}

}
