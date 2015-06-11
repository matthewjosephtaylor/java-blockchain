package com.thaler.miner;

import java.math.BigInteger;
import java.time.Instant;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.thaler.miner.ddb.Bits256Timestamp;
import com.thaler.miner.ddb.Bits256UnsignedFraction;
import com.thaler.miner.ddb.Bits256UnsignedInteger;

public class BitManipulationUnitTests {

	private static final Logger logger = Logger.getLogger(BitManipulationUnitTests.class);

	private static final String MAX_VALUE_STRING = "115792089237316195423570985008687907853269984665640564039457584007913129639935";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testBits256Integer() {

		// basic sanity check
		{
			final BigInteger bigInteger = BigInteger.valueOf(1098);
			final Bits256UnsignedInteger bits256UnsignedInteger = Bits256UnsignedInteger.create(bigInteger);
			Assert.assertTrue(bits256UnsignedInteger.toBigInteger().equals(bigInteger));
		}

		// max value check
		{

			final BigInteger max256BitNumberFromString = new BigInteger(MAX_VALUE_STRING);
			Assert.assertTrue(max256BitNumberFromString.equals(Bits256UnsignedInteger.MAX_VALUE.toBigInteger()));
		}

		// min value check
		{
			Assert.assertTrue(Bits256UnsignedInteger.MIN_VALUE.toBigInteger().equals(BigInteger.valueOf(0)));
		}

	}
	
	@Test
	public void testBits256IntegerMaxBigInt(){
		Bits256UnsignedInteger.create(Bits256UnsignedInteger.MAX_VALUE.toBigInteger());
	}

	@Test
	public void testBits256IntegerMaxValueOverflow() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("value is too large");
		final BigInteger max256BitNumberFromString = new BigInteger(MAX_VALUE_STRING);
		final BigInteger tooLarge = max256BitNumberFromString.add(BigInteger.ONE);
		final Bits256UnsignedInteger bits256UnsignedInteger = Bits256UnsignedInteger.create(tooLarge);
		logger.info(bits256UnsignedInteger);
	}

	@Test
	public void testBits256IntegerPositiveValuesOnly() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("value must be greater than or equal to zero");
		Bits256UnsignedInteger.create(BigInteger.valueOf(-1));
	}

	@Test
	public void testBits256TimeStamp() {
		final Bits256Timestamp bits256Timestamp = Bits256Timestamp.now();
		logger.info(bits256Timestamp);
		logger.info(Instant.now().getEpochSecond());
		logger.info(bits256Timestamp.toInstant().getEpochSecond());
		Assert.assertEquals(Instant.now().getEpochSecond(), bits256Timestamp.toInstant().getEpochSecond());
		final byte[] bytes = bits256Timestamp.getBytes();
		final Bits256Timestamp fromRawBytesTimeStamp = Bits256Timestamp.create(bytes);
		logger.info(fromRawBytesTimeStamp);
		Assert.assertEquals(bits256Timestamp, fromRawBytesTimeStamp);

	}
	
	@Test
	public void testBits256Fraction() {
		Bits256UnsignedFraction bits256UnsignedFraction = Bits256UnsignedFraction.create(3, 5);
		logger.info(bits256UnsignedFraction);
		logger.info(bits256UnsignedFraction.toHex());
		logger.info(bits256UnsignedFraction.toBinary());
		Assert.assertEquals(BigInteger.valueOf(3), bits256UnsignedFraction.getNumerator());
		Assert.assertEquals(BigInteger.valueOf(5), bits256UnsignedFraction.getDenominator());
	}

}
