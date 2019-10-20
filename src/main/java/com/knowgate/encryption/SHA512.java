package com.knowgate.encryption;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512 implements HashProvider {

	private String inputValue;

	private String salt;

	public SHA512() {
		salt = null;
	}

	public SHA512(final String salt) {
		this.salt = salt;
	}

	@Override
	public void Update(String s)
		throws NullPointerException, IllegalStateException {
		if (null == s)
			throw new NullPointerException("SHA512 no input value provided");
		else if (s.length() == 0)
			throw new IllegalStateException("SHA512 empty input value provided");
		inputValue = s;
	}

	@Override
	public byte[] Final()
		throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		if (null!=salt)
			md.update(salt.getBytes(StandardCharsets.UTF_8));
		return md.digest(inputValue.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String asHex() 
		throws IllegalStateException {
		if (null == inputValue)
			throw new IllegalStateException("SHA512 no input value provided");
		try {
			return asHex(Final());
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No such algorithm SHA-512");
		}
	}

	/**
	 * Turns array of bytes into string representing each byte as unsigned hex number.
	 * @param hash	Array of bytes to convert to hex-string
	 * @return	Generated hex string
	 */
	public static String asHex (byte hash[]) {
		StringBuffer buf = new StringBuffer(hash.length * 2);
		int i;

		for (i = 0; i < hash.length; i++) {
			if (((int) hash[i] & 0xff) < 0x10)
				buf.append("0");

			buf.append(Long.toString((int) hash[i] & 0xff, 16));
		}

		return buf.toString();
	}
}
