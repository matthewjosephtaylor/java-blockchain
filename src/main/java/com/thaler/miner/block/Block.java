package com.thaler.miner.block;

import java.util.List;
import java.util.SortedMap;

import com.thaler.miner.ddb.Bits256;
import com.thaler.miner.ddb.Bits256UnsignedInteger;
import com.thaler.miner.transaction.TransactionHash;


// transformed into bit stream and compressed to make spentIndex as small as possible
public class Block {
	private Bits256 merkleRoot;
	private Bits256 maxIndex;
	private Bits256[] newIndexAddresseAmountTuples; // Index,Address,Amount,HashOfIAA
	private Bits256 proofOfWork; // nonce must be a valid merklePath to a given address/index
	private Bits256 timeStamp;
	private Bits256 sequence;
	private Bits256 version; // 0 = testnet
	private Bits256[] transactionHashes;
	private Bits256[] spentIndexList; // 1=spent, 0=unspent
	
}
