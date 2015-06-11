package com.thaler.miner;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thaler.miner.ddb.Bits256;
import com.thaler.miner.ddb.HashableString;
import com.thaler.miner.merkle.Hashable;
import com.thaler.miner.merkle.MerkleUtil;
import com.thaler.miner.merkle.MerkleTreeNode;
import com.thaler.miner.util.CryptoUtil;

public class MerkleTests {

	public String TSA = "Test String A";
	public String TSB = "Test String B";
	public String TSC = "Test String C";
	public String TSD = "Test String D";

	private FluentIterable<Hashable> createBaseData() {
		List<Hashable> baseData = Lists.newArrayList();
		for(int i=0; i< 100160; i++){
			baseData.add(HashableString.create("Test data: " + i));
		}
		return FluentIterable.from(baseData);
	}

	private static final Logger logger = Logger.getLogger(MerkleTests.class);

	@Test
	public void merkleRootTest() {

		{
			Map<Bits256, MerkleTreeNode> merkleMap = Maps.newHashMap();
			FluentIterable<Hashable> baseData = createBaseData();
			MerkleTreeNode merkleRoot = MerkleUtil.createMerkleTree(baseData, merkleMap);
			logger.info("merkle tree Root: " + merkleRoot.combinedHash.toString());
			logger.info("tree root: " + merkleMap.get(merkleRoot.combinedHash));
			
			Hashable firstDataItem = baseData.first().get();
			Bits256 doubleHashOfFirstDataItem = CryptoUtil.hash(CryptoUtil.hash(firstDataItem), CryptoUtil.hash(firstDataItem));
			logger.info("first data item: " + merkleMap.get(doubleHashOfFirstDataItem));
		}

	}
	
	@Test
	public void merklePathCreationTest() {
		FluentIterable<Hashable> baseData = createBaseData();
		Hashable firstDataItem = baseData.last().get();
		Bits256 doubleHashOfFirstDataItem = CryptoUtil.hash(CryptoUtil.hash(firstDataItem), CryptoUtil.hash(firstDataItem));
		Map<Bits256, MerkleTreeNode> merkleMap = Maps.newHashMap();
		MerkleTreeNode merkleRoot = MerkleUtil.createMerkleTree(baseData, merkleMap);
		List<MerkleTreeNode> merklePath = MerkleUtil.createMerkePath(merkleMap, merkleRoot, doubleHashOfFirstDataItem);
		
		logger.info("merkle tree Root: " + merkleRoot.combinedHash.toString());
		logger.info("first data item node: " + merkleMap.get(doubleHashOfFirstDataItem));
		
		logger.info("----path----");
		for(MerkleTreeNode merkleTreeNode : merklePath){
			logger.info(merkleTreeNode);
		}
		logger.info("----path end----");
	}

	@Test
	public void merklePathValidationTest() {
		FluentIterable<Hashable> baseData = createBaseData();
		Hashable firstDataItem = baseData.last().get();
		Bits256 doubleHashOfFirstDataItem = CryptoUtil.hash(CryptoUtil.hash(firstDataItem), CryptoUtil.hash(firstDataItem));
		Map<Bits256, MerkleTreeNode> merkleMap = Maps.newHashMap();
		MerkleTreeNode merkleRoot = MerkleUtil.createMerkleTree(baseData, merkleMap);
		List<MerkleTreeNode> merklePath = MerkleUtil.createMerkePath(merkleMap, merkleRoot, doubleHashOfFirstDataItem);
		
		logger.info("merkle tree Root: " + merkleRoot.combinedHash.toString());
		logger.info("first data item node: " + merkleMap.get(doubleHashOfFirstDataItem));
		
		logger.info("----path----");
		for(MerkleTreeNode merkleTreeNode : merklePath){
			logger.info(merkleTreeNode);
		}
		logger.info("----path end----");
		
		Assert.assertTrue(MerkleUtil.validateMerklePath(merklePath, merkleRoot));
	}

}
