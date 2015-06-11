package com.thaler.miner.ddb;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.google.common.base.Preconditions;
import com.thaler.miner.merkle.Hashable;

public class Bits256 implements Hashable {

	private byte[] _bytes;

	public static Bits256 create(byte[] bytes) {

		return new Bits256(bytes);

	}

	private Bits256(byte[] bytes) {
		Preconditions.checkArgument(bytes.length == 32);
		this._bytes = bytes;
	}

	public byte[] getBytes() {
		return this._bytes;
	}

	@Override
	public String toString() {
		return Base64.encodeBase64URLSafeString(this._bytes);
	}

	public String toHex() {
		return Hex.encodeHexString(this._bytes);
	}

	public String toBinary() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this._bytes.length; i++) {
			Byte b = this._bytes[i];
			sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
		}
		return sb.toString();
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
		Bits256 other = (Bits256) obj;
		if (!Arrays.equals(_bytes, other._bytes)) return false;
		return true;
	}

}
