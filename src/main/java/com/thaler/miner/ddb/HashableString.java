package com.thaler.miner.ddb;

import org.bouncycastle.util.Strings;

import com.thaler.miner.merkle.Hashable;

public class HashableString implements Hashable{

	private String _string;
	
	public static HashableString create(String string){
		return new HashableString(string);
	}
	
	public HashableString(String string) {
		this._string = string;
	}
	
	public String getString(){
		return this._string;
	}
	
	@Override
	public byte[] getBytes() {
		return Strings.toUTF8ByteArray(this._string);
	}
	


}
