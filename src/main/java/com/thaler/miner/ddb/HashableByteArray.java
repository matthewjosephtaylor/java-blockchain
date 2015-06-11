package com.thaler.miner.ddb;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.thaler.miner.merkle.Hashable;

public class HashableByteArray implements Hashable{
	
	
	private byte[] _bytes;
	
	public static HashableByteArray create(byte[] bytes){
		return new HashableByteArray(bytes);
	}
	
	public HashableByteArray(byte[] bytes) {
		this._bytes = bytes;
	}

	@Override
	public String toString(){
		return Base64.encodeBase64URLSafeString(this._bytes);
	}
	
	public String toHex(){
		return Hex.encodeHexString(this._bytes);
	}

	
	@Override
	public byte[] getBytes() {
		return this._bytes;
	}

}
