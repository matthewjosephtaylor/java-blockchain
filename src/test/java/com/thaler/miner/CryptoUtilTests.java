package com.thaler.miner;


import org.apache.log4j.Logger;
import org.junit.Test;

import com.lambdaworks.crypto.SCrypt;
import com.thaler.miner.ddb.HashableByteArray;

public class CryptoUtilTests {

	private static final Logger logger = Logger.getLogger(CryptoUtilTests.class);
	
	@Test
	public void test() throws Exception{
		
		byte[] P, S;
        int N, r, p, dkLen;
        String DK;
        
        
        
        P = "password2".getBytes("UTF-8");
        S = "NaCl".getBytes("UTF-8");
        //N = (int)Math.pow(2, 15); // cpu 15 == 1s/h
        N = (int)Math.pow(2, 1); // cpu 15 == 1s/h
        logger.info("n:" + N);
        //r = 8; // memory
        r = (int)Math.pow(2, 20);
        logger.info("r:" + r);
        //p = 16; // parallel
        p = 1; // parallel
        dkLen = 32;
        DK = "fdbabe1c9d3472007856e7190d01e9fe7c6ad7cbc8237830e77376634b3731622eaf30d92e22a3886ff109279d9830dac727afb94a83ee6d8360cbdfa2cc0640";
        
        logger.info("start!");
        for(int i=0; i<100000000; i++){
        P = ("password" + i).getBytes("UTF-8");
        byte[] result = SCrypt.scrypt(P, S, N, r, p, dkLen);
        //logger.info("length: " + result.length);
        HashableByteArray hashableByteArray = HashableByteArray.create(result);
        logger.info(hashableByteArray.toHex());
        }
        
        //Assert.assertArrayEquals(decode(DK), SCrypt.scrypt(P, S, N, r, p, dkLen));

	}
	
	public static byte[] decode(String str) {
        byte[] bytes = new byte[str.length() / 2];
        int index = 0;

        for (int i = 0; i < str.length(); i += 2) {
            int high = hexValue(str.charAt(i));
            int low = hexValue(str.charAt(i + 1));
            bytes[index++] = (byte) ((high << 4) + low);
        }

        return bytes;
    }
    public static int hexValue(char c) {
        return c >= 'a' ? c - 87 : c - 48;
    }

}
