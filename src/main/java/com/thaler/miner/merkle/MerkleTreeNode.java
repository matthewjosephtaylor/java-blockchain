package com.thaler.miner.merkle;

import com.thaler.miner.ddb.Bits256;
import com.thaler.miner.util.CryptoUtil;


public class MerkleTreeNode {
	
	public static  MerkleTreeNode create(Bits256 left, Bits256 right, String name) {
		return new MerkleTreeNode(left, right,  name);
	}
	
	private MerkleTreeNode(Bits256  left, Bits256  right, String name) {
		this.left = left;
		this.right = right;
		this.name = name;
		this.combinedHash = CryptoUtil.hash(left,right);
	}
	public Bits256 combinedHash;
	public Bits256 left;
	public Bits256 right;
	public String name;
	
	
	@Override
	public String toString() {
		return name  + " (" + combinedHash + " | " + left + " , " + right + ")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((combinedHash == null) ? 0 : combinedHash.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MerkleTreeNode other = (MerkleTreeNode) obj;
		if (combinedHash == null) {
			if (other.combinedHash != null) return false;
		} else if (!combinedHash.equals(other.combinedHash)) return false;
		if (left == null) {
			if (other.left != null) return false;
		} else if (!left.equals(other.left)) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (right == null) {
			if (other.right != null) return false;
		} else if (!right.equals(other.right)) return false;
		return true;
	}

	
	
}
