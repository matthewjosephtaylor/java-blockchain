package com.thaler.miner.ddb;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.thaler.miner.merkle.Hashable;

/**
 * Floating point arithmetic and money does not mix.
 * 
 * Lots of financial operations involve division.
 * 
 * Fractions don't lose precision over the course of calculation.
 * 
 * 128 bit numerator
 * 128 bit denominator
 *
 * zero denominator is allowed as a valid fraction (no math checks)
 *
 */
public class Bits256UnsignedFraction implements Hashable {

	private static final Logger logger = Logger.getLogger(Bits256UnsignedFraction.class);

	private byte[] _numeratorBytes; //numerator/denominator (each 128 bits)
	private byte[] _denominatorBytes; //numerator/denominator (each 128 bits)

	public static Bits256UnsignedFraction create(final BigInteger numerator, final BigInteger denominator) {
		return new Bits256UnsignedFraction(numerator, denominator);

	}

	public static Bits256UnsignedFraction create(final byte[] bytes) {
		return new Bits256UnsignedFraction(bytes);
	}

	public static Bits256UnsignedFraction create(final long numerator, final long denominator) {
		return create(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
	}

	public Bits256UnsignedFraction(final byte[] bytes) {
		_numeratorBytes = new byte[16];
		_denominatorBytes = new byte[16];
		System.arraycopy(bytes, 0, _numeratorBytes, 0, _numeratorBytes.length);
		System.arraycopy(bytes, _numeratorBytes.length, _denominatorBytes, 0, _denominatorBytes.length);
	}

	private Bits256UnsignedFraction(final BigInteger numeratorBigInteger, final BigInteger denominatorBigInteger) {
		_numeratorBytes = UnsignedBitsUtil.bigIntegerToBits(numeratorBigInteger, 128);
		_denominatorBytes = UnsignedBitsUtil.bigIntegerToBits(denominatorBigInteger, 128);
	}

	@Override
	public byte[] getBytes() {
		byte[] result = new byte[_numeratorBytes.length + _denominatorBytes.length];
		System.arraycopy(_numeratorBytes, 0, result, 0, _numeratorBytes.length);
		System.arraycopy(_denominatorBytes, 0, result, _numeratorBytes.length, _denominatorBytes.length);
		return result;
	}

	public BigFraction toBigFraction() {
		return new BigFraction(getNumerator(), getDenominator());
	}

	public BigInteger getNumerator() {
		return UnsignedBitsUtil.bitsToBigInteger(_numeratorBytes);
	}

	public BigInteger getDenominator() {
		return UnsignedBitsUtil.bitsToBigInteger(_denominatorBytes);
	}

	public String toHex() {
		return Hex.encodeHexString(getBytes());
	}

	public String toBinary() {
		StringBuilder sb = new StringBuilder();
		byte[] bytes = getBytes();
		for (int i = 0; i < bytes.length; i++) {
			Byte b = bytes[i];
			sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return getNumerator() + "/" + getDenominator();
	}

}
