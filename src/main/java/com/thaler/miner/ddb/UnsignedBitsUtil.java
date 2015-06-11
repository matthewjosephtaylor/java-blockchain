package com.thaler.miner.ddb;

import java.math.BigInteger;

import com.google.common.base.Preconditions;

public final class UnsignedBitsUtil {

	public static byte[] bigIntegerToBits(BigInteger bigInteger, int bits) {
		Preconditions.checkArgument(bits % 8 == 0);
		Preconditions.checkArgument(bits > 0);

		byte[] bytes = new byte[bits / 8];
		final byte[] bigIntByteArray = bigInteger.toByteArray();
		int startIndex;
		
		if (bigIntByteArray.length == bytes.length + 1) {
			startIndex = 1;
		} else if (bigIntByteArray.length <= bytes.length) {
			startIndex = 0;
		} else {
			throw new RuntimeException("Number to large to fit in byte array. bits: " + bits + " integer value: " + bigInteger + " integer bytes:" + bigIntByteArray.length);
		}
		
		final int offset = bytes.length - bigIntByteArray.length;
		for (int i = startIndex; i < bigIntByteArray.length; i++) {
			bytes[i + offset] = bigIntByteArray[i];
		}
		
		return bytes;
	}
	
	public static BigInteger bitsToBigInteger(byte[] byteArray) {
		return new BigInteger(1, byteArray);
	}

}
