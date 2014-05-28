package com.thaler.miner.address;


// 160 bit hash of public key
public class ImmutableAddress {
	
	private byte[] hash;

	public ImmutableAddress(byte[] hash) {
		this.hash = hash;
	}
	
	public byte[] getBytes(){
		return this.hash;
	}

}
