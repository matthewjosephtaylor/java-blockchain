package com.thaler.miner.merkle;

public class Pair<T> {

	private T left;
	private T right;

	public static <T> Pair<T> create(T left, T right) {

		return new Pair<T>(left, right);
	}

	public Pair(T left, T right) {
		this.left = left;
		this.right = right;
	}

	public T getLeft() {
		return left;
	}

	public T getRight() {
		return right;
	}

}
