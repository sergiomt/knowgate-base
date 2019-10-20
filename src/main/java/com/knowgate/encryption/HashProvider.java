package com.knowgate.encryption;

import java.security.NoSuchAlgorithmException;

public interface HashProvider {

	/**
	 * Update buffer with given string.
	 * @param inputValue String to be hashed
	 * @throws NullPointerException if inputValue is null
	 * @throws IllegalStateException if inputValue is an empty String
	 */
	void Update(String inputValue) throws NullPointerException, IllegalStateException;

	/**
	 * Returns hash value for input String
	 * @return byte[]
	 * @throws NoSuchAlgorithmException
	 */
	byte[] Final() throws NoSuchAlgorithmException;

	/**
	 * Returns hex representation of the hash
	 * @return String
	 * @throws IllegalStateException
	 */
	String asHex() throws IllegalStateException;
}
