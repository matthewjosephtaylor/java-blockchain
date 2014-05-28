package com.thaler.miner.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
