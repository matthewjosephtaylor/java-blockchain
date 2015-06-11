package com.thaler.miner.ddb;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.threeten.extra.scale.TaiInstant;

import com.google.common.base.Preconditions;
import com.thaler.miner.merkle.Hashable;

/**
 * Timescale is International Atomic Time (TAI)
 * 
 * http://en.wikipedia.org/wiki/International_Atomic_Time
 * 
 * Second resolution
 * 
 * Stored in unsigned 256 bit integer
 *
 */
// 256-bit unsigned integer (big endian)
public class Bits256Timestamp implements Hashable {

	private static final Logger logger = Logger.getLogger(Bits256Timestamp.class);

	private byte[] _bytes;

	public static Bits256Timestamp create(long val) {
		return create(BigInteger.valueOf(val));
	}

	public static Bits256Timestamp now() {
		TaiInstant taiInstant = TaiInstant.of(Instant.now());
		long taiSeconds = taiInstant.getTaiSeconds();
		return create(taiSeconds);

	}

	public static Bits256Timestamp create(BigInteger bigInteger) {
		return new Bits256Timestamp(bigInteger);

	}

	private Bits256Timestamp(BigInteger bigInteger) {
		Bits256UnsignedInteger bits256UnsignedInteger = Bits256UnsignedInteger.create(bigInteger);
		this._bytes = bits256UnsignedInteger.getBytes();
	}

	public byte[] getBytes() {
		return this._bytes;
	}

	public long toTaiSeconds() {
		return getBigInteger().longValue();
	}

	public Instant toInstant() {
		long taiSeconds = toTaiSeconds();
		TaiInstant taiInstant = TaiInstant.ofTaiSeconds(taiSeconds, 0);
		return taiInstant.toInstant();
	}

	@Override
	public String toString() {
		long taiSeconds = toTaiSeconds();
		TaiInstant taiInstant = TaiInstant.ofTaiSeconds(taiSeconds, 0);
		return taiInstant.toString();
	}

	public BigInteger getBigInteger() {
		return new BigInteger(this._bytes);
	}

	public static Bits256Timestamp create(byte[] bytes) {
		BigInteger bigInteger = Bits256UnsignedInteger.create(bytes).toBigInteger();
		return create(bigInteger);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(_bytes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Bits256Timestamp other = (Bits256Timestamp) obj;
		if (!Arrays.equals(_bytes, other._bytes)) return false;
		return true;
	}

}
