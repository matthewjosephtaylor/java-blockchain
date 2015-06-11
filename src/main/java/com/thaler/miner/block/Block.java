package com.thaler.miner.block;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.thaler.miner.ddb.Bits256;
import com.thaler.miner.ddb.Bits256Timestamp;
import com.thaler.miner.ddb.Bits256UnsignedInteger;
import com.thaler.miner.merkle.Hashable;
import com.thaler.miner.merkle.MerkleTreeNode;
import com.thaler.miner.merkle.MerkleUtil;
import com.thaler.miner.util.CryptoUtil;

/**
 * The block, the heart and soul of the system.
 * 
 * Any data that can't fit inside of 256 bits is represented by its Merkle root.
 * 
 * nonceMerkleRoot: nonce for proof of work is the coin base transaction
 * signature.
 * 
 * maxIndex: the last index referenced in the spentIndexBitmap.
 * 
 * targetDifficulty: recalculated periodically based on a set of moving averages
 * 
 * spentIndexBitmap: 1=spent, 0=unspent. Use maxIndex to determine length of
 * bitmap
 *
 */
public class Block {

	private static final Logger logger = Logger.getLogger(Block.class);

	public enum Version {
		TEST(Bits256UnsignedInteger.create(0)),
		MAIN(Bits256UnsignedInteger.create(1));
		private final Bits256UnsignedInteger versionNumber;

		Version(Bits256UnsignedInteger versionNumber) {
			this.versionNumber = versionNumber;
		}

		public Bits256UnsignedInteger value() {
			return this.versionNumber;
		}
	}

	public Bits256 merkleRoot; // also proof of work.
	public Bits256 previousBlockMerkleRoot;
	public Bits256UnsignedInteger version;
	public Bits256UnsignedInteger sequence;
	public Bits256Timestamp timeStamp;
	public Bits256UnsignedInteger maxIndex;
	public Bits256UnsignedInteger targetDifficulty;
	public Bits256 newIndexAddresseAmountTupleListMerkleRoot; // Index,Address,Amount,HashOfIAA
	public Bits256 nonceMerkleRoot; // nonce must be a valid merklePath to a given address/index
	public Bits256 newTransactionTuplesMerkleRoot;
	public Bits256 spentIndexBitmapMerkleRoot; // 1=spent, 0=unspent

	public static Block create(Bits256 previousBlockMerkleRoot, Version version, Bits256UnsignedInteger sequence,
			Bits256Timestamp timeStamp, Bits256UnsignedInteger maxIndex, Bits256UnsignedInteger targetDifficulty) {
		Block result = new Block();
		result.previousBlockMerkleRoot = previousBlockMerkleRoot;
		result.version = version.value();
		result.sequence = sequence;
		result.timeStamp = timeStamp;
		result.maxIndex = maxIndex;
		result.targetDifficulty = targetDifficulty;

		return result;
	}

	public FluentIterable<Hashable> getBaseDataBytes() {
		List<Hashable> baseData = Lists.newArrayList();
		baseData.add(previousBlockMerkleRoot);
		baseData.add(version);
		baseData.add(sequence);
		baseData.add(timeStamp);
		baseData.add(maxIndex);
		baseData.add(newIndexAddresseAmountTupleListMerkleRoot);
		baseData.add(nonceMerkleRoot);
		baseData.add(newTransactionTuplesMerkleRoot);
		baseData.add(spentIndexBitmapMerkleRoot);
		return FluentIterable.from(baseData);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);

	}

	public boolean isValidMerkleRoot() {
		if(merkleRoot == null){
			return false;
		}
		MerkleTreeNode calculatedMerkleRoot = MerkleUtil.createMerkleTree(getBaseDataBytes(), null);
		logger.info("this mr: " + merkleRoot);
		logger.info("calculated mr: " + calculatedMerkleRoot.combinedHash);
		return merkleRoot.equals(calculatedMerkleRoot.combinedHash);
	}

	public boolean isValidProofOfWork() {
		//BigInteger targetHashAsInteger = Bits256UnsignedInteger.create(this.merkleRoot.getBytes()).toBigInteger();
		Bits256 targetHash = CryptoUtil.scrypt(this.merkleRoot.getBytes()); 
		BigInteger targetHashAsInteger = Bits256UnsignedInteger.create(targetHash.getBytes()).toBigInteger();
		logger.info("t: " + targetDifficulty.toBinary());
		logger.info("a: " + targetHash.toBinary());
		
		return targetHashAsInteger.compareTo(targetDifficulty.toBigInteger()) <= 0;
	}

}
