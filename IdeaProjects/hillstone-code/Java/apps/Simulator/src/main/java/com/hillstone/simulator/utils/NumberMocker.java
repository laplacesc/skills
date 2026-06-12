package com.hillstone.simulator.utils;

import org.apache.commons.lang.math.RandomUtils;

public class NumberMocker {

	public static long getLong() {
		

		int choice =  RandomUtils.nextInt(5);
		long l = 0L;
		if (choice <3) {
			l= RandomUtils.nextInt(1000) * 10L;
		} else if (choice == 3) {
			l= RandomUtils.nextInt(1000) * 100L;
		} else if (choice == 4) {
			l= RandomUtils.nextInt(1000) * 1000000L;
		}
		
		return l;
	}

	public static int getInt() {
		return RandomUtils.nextInt(Integer.MAX_VALUE);

	}

	public static final int getInt(int n) {
		return RandomUtils.nextInt(n);
	}

	public static final double getDouble() {
		return RandomUtils.nextDouble() * 100D;
	}

}
