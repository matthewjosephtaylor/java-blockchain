package com.thaler.miner.merkle;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thaler.miner.ddb.Bits256;
import com.thaler.miner.util.CryptoUtil;

public class MerkleUtil {

	private static final Logger logger = Logger.getLogger(MerkleUtil.class);


	public static MerkleTreeNode createMerkleTree(FluentIterable<Hashable> baseData, Map<Bits256, MerkleTreeNode> merkleTreeMap) {
		Preconditions.checkArgument(baseData.size() >= 2);

		FluentIterable<MerkleTreeNode> intermediateNodes = createInitialTreeNodes(baseData);
		addNodesToMap(intermediateNodes, merkleTreeMap);
		BigInteger column = BigInteger.ZERO;
		while (intermediateNodes.size() > 1) { // TODO 2 Billion node limit!!!
			//intermediateNodes = graphParents(intermediateNodes);
			intermediateNodes = createTreeNodes(intermediateNodes, column.toString());
			addNodesToMap(intermediateNodes, merkleTreeMap);
			column = column.add(BigInteger.ONE);
			//logger.info("intermediateNodesSize: " + intermediateNodes.size());
		}

		return intermediateNodes.first().get();

	}

	private static void addNodesToMap(FluentIterable<MerkleTreeNode> intermediateNodes, Map<Bits256, MerkleTreeNode> merkleTreeMap) {
		if(merkleTreeMap == null){
			return;
		}
		for(MerkleTreeNode merkleTreeNode : intermediateNodes) {
			merkleTreeMap.put(merkleTreeNode.combinedHash, merkleTreeNode);
		}
	}

	private static <T> FluentIterable<MerkleTreeNode> createTreeNodes(FluentIterable<MerkleTreeNode> singles, String columnName) {
		List<MerkleTreeNode> result = Lists.newArrayList();
		Iterator<MerkleTreeNode> iterator = singles.iterator();
		BigInteger row = BigInteger.ZERO;
		while (iterator.hasNext()) {
			Bits256 left = iterator.next().combinedHash;
			Bits256 right;
			if (iterator.hasNext()) {
				right = iterator.next().combinedHash;
			} else {
				right = left;
			}
			MerkleTreeNode pair = MerkleTreeNode.create(left, right, columnName + "," + row);
			result.add(pair);
			row = row.add(BigInteger.ONE);
		}
		return FluentIterable.from(result);
	}

	
	private static FluentIterable<MerkleTreeNode> createInitialTreeNodes(FluentIterable<Hashable> nodeDataItems) {
		List<MerkleTreeNode> nodes = Lists.newArrayList();
		BigInteger row = BigInteger.ZERO;
		for (Hashable nodeDataItem : nodeDataItems) {
			//TODO MJT quadruple  hash of base data uneeded
			MerkleTreeNode treeNode = MerkleTreeNode.create( CryptoUtil.hash(nodeDataItem), CryptoUtil.hash(nodeDataItem),  "initial data row: " + row);
			row = row.add(BigInteger.ONE);
			nodes.add(treeNode);
		}
		return FluentIterable.from(nodes);
	}

	public static List<MerkleTreeNode> createMerkePath(Map<Bits256,MerkleTreeNode> merkleMap, MerkleTreeNode merkleRoot, Bits256 targetHash) {
		List<MerkleTreeNode> result = Lists.newArrayList();

		Map<Bits256,MerkleTreeNode> childToParentMap = Maps.newHashMap();
		for(MerkleTreeNode merkleTreeNode: merkleMap.values()){
			childToParentMap.put(merkleTreeNode.left, merkleTreeNode);
			childToParentMap.put(merkleTreeNode.right, merkleTreeNode);
		}
		
		MerkleTreeNode node = null;
		while(node == null || !node.equals(merkleRoot)){
			
			node = childToParentMap.get(targetHash);
			targetHash = node.combinedHash;
			//logger.info("stepping node: " + node);
			result.add(node);
		}
		
		return result;
	}

	public static boolean validateMerklePath(List<MerkleTreeNode> merklePath, MerkleTreeNode merkleRoot){
		List<MerkleTreeNode> reverseMerklePath = Lists.reverse(merklePath);
		if(reverseMerklePath.get(0).combinedHash.equals(merkleRoot)){
			logger.info("merkle root not found");
			return false;
		}
		FluentIterable<MerkleTreeNode> reverseMerklePathIterable = FluentIterable.from(reverseMerklePath);
		
		MerkleTreeNode previousNode = reverseMerklePathIterable.first().get();
		logger.info("Merkle Root: " + previousNode);
		for(MerkleTreeNode merkleTreeNode : reverseMerklePathIterable.skip(1)){
			logger.info(merkleTreeNode);
			if(!previousNode.isChild(merkleTreeNode)){
				logger.warn("Invalid merkle path detected.\npreviousNode: " + previousNode + "\nmerkleTreeNode: " + merkleTreeNode);
				return false;
			}
			previousNode = merkleTreeNode;

		}
		return true;
		
	}
}
