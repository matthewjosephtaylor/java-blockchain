package com.thaler.miner.block;

import com.thaler.miner.merkle.MerkleTreeNode;
import com.thaler.miner.merkle.MerkleUtil;

public class BlockUtil {
	
	public static void mine(Block block){
		MerkleTreeNode merkleRoot = MerkleUtil.createMerkleTree(block.getBaseDataBytes(), null);
		block.merkleRoot = merkleRoot.combinedHash;
	}

}
