package com.thaler.miner;

import java.math.BigInteger;
import java.time.Instant;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.thaler.miner.block.Block;
import com.thaler.miner.block.Block.Version;
import com.thaler.miner.block.BlockUtil;
import com.thaler.miner.ddb.Bits256;
import com.thaler.miner.ddb.Bits256Timestamp;
import com.thaler.miner.ddb.Bits256UnsignedInteger;
import com.thaler.miner.util.CryptoUtil;

public class MineBlockTests {
	private static final Logger logger = Logger.getLogger(MineBlockTests.class);

	@Test
	public void calcProofOfWork() {

		Bits256 previousBlockMerkleRoot = CryptoUtil.hash("Test".getBytes());

		Block.Version version = Version.TEST;
		Bits256UnsignedInteger sequence = Bits256UnsignedInteger.create(999);
		Bits256Timestamp timeStamp = Bits256Timestamp.now();
		Bits256UnsignedInteger maxIndex = Bits256UnsignedInteger.create(1000);
		Bits256UnsignedInteger targetDifficutly = Bits256UnsignedInteger.create(
				Bits256UnsignedInteger.MAX_VALUE.toBigInteger()
						.clearBit(255)
						.clearBit(254)
						.clearBit(253)
						.clearBit(252)
						.clearBit(251)
						.clearBit(250)
						.clearBit(249)
						.clearBit(248)
						.clearBit(247)
				);

		Block b = Block.create(previousBlockMerkleRoot, version, sequence, timeStamp, maxIndex, targetDifficutly);

		b.newIndexAddresseAmountTupleListMerkleRoot = previousBlockMerkleRoot;
		b.nonceMerkleRoot = previousBlockMerkleRoot;
		b.newTransactionTuplesMerkleRoot = previousBlockMerkleRoot;
		b.spentIndexBitmapMerkleRoot = previousBlockMerkleRoot;

		logger.info(b);
		Assert.assertNull(b.merkleRoot);
		BlockUtil.mine(b);
		Assert.assertNotNull(b.merkleRoot);
		logger.info(b);
		Assert.assertTrue(b.isValidMerkleRoot());
		b.maxIndex = Bits256UnsignedInteger.create(maxIndex.toBigInteger().subtract(BigInteger.valueOf(1)));
		Assert.assertFalse(b.isValidMerkleRoot());

		for (int i = 0; i < 1000; i++) {
			b.maxIndex = Bits256UnsignedInteger.create(i);
			BlockUtil.mine(b);
			//logger.info(b.targetDifficulty.toHex());
			//			logger.info(b.targetDifficulty.toBinary());
			//			logger.info(b.merkleRoot.toBinary());

			boolean validPow = b.isValidProofOfWork();
			//			logger.info(validPow);

			if (validPow) {
				logger.info("i=" + i);
				break;
			}
		}

		//Assert.assertTrue(b.isValidProofOfWork());
	}

}
