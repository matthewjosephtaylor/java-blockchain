package com.thaler.miner;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.thaler.miner.ddb.Bits256;
import com.thaler.miner.ddb.HashableByteArray;
import com.thaler.miner.localdb.LocalDatabase;
import com.thaler.miner.merkle.Hashable;
import com.thaler.miner.util.CryptoUtil;

public class LocalDbTests {
	private static final Logger logger = Logger.getLogger(LocalDbTests.class);

	@Test
	public void putGetTest() {
		LocalDatabase ldb = LocalDatabase.getInstance();
		
		Hashable value = HashableByteArray.create("The value".getBytes());
		logger.info("value: " + value);
		Bits256 key = CryptoUtil.hash(value);
		
		ldb.put(key, value);
		{
			Hashable returnedValue = ldb.get(key).get();
			logger.info("returned value: " + returnedValue);
			Assert.assertArrayEquals(value.getBytes(), returnedValue.getBytes());
		}
	}

}
