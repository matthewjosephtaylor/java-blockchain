package com.thaler.miner.ddb;

import java.math.BigInteger;

import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.thaler.miner.merkle.Hashable;

// 256-bit unsigned integer (big endian)
public class Bits256UnsignedInteger implements Hashable {

	private static final Logger logger = Logger.getLogger(Bits256UnsignedInteger.class);

	private static final byte[] maxIntegerBytes = new byte[32];
	{
		for (int i = 0; i < maxIntegerBytes.length; i++) {
			maxIntegerBytes[i] = 0xFFFFFFFF;
		}
	}

	private static final BigInteger maxIntergerBigInteger = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639935");

	public static final Bits256UnsignedInteger MAX_VALUE = Bits256UnsignedInteger.create(maxIntegerBytes);

	public static final Bits256UnsignedInteger MIN_VALUE = Bits256UnsignedInteger.create(0);

	private byte[] _bytes;

	public static Bits256UnsignedInteger create(final BigInteger bigInteger) {
		return new Bits256UnsignedInteger(bigInteger);

	}

	public static Bits256UnsignedInteger create(final byte[] bytes) {
		return new Bits256UnsignedInteger(bytes);
	}

	public static Bits256UnsignedInteger create(final long val) {
		return create(BigInteger.valueOf(val));
	}

	public Bits256UnsignedInteger(final byte[] bytes) {
		_bytes = bytes;
	}

	private Bits256UnsignedInteger(final BigInteger bigInteger) {
		Preconditions.checkArgument(bigInteger.compareTo(BigInteger.ZERO) >= 0, "value must be greater than or equal to zero");
		Preconditions.checkArgument(bigInteger.compareTo(maxIntergerBigInteger) <= 0, "value is too large");

		_bytes = new byte[32];
		final byte[] bigIntByteArray = bigInteger.toByteArray();
		int startIndex;
		if (bigIntByteArray.length == 33) {
			startIndex = 1;
		} else {
			startIndex = 0;
		}
		final int offset = _bytes.length - bigIntByteArray.length;
		for (int i = startIndex; i < bigIntByteArray.length; i++) {
			_bytes[i + offset] = bigIntByteArray[i];
		}

		//logger.info(this._bytes.length);
		//Preconditions.checkArgument(this._bytes.length == 32);

	}

	@Override
	public byte[] getBytes() {
		return _bytes;
	}

	public BigInteger toBigInteger() {
		return new BigInteger(1, _bytes);
	}

	public String toHex() {
		return Hex.encodeHexString(this._bytes);
	}

	public String toBinary() {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< this._bytes.length; i++){
			Byte b = this._bytes[i];
			sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
		}
		return sb.toString();
	}

	
	@Override
	public String toString() {
		return toBigInteger().toString();
	}

}
