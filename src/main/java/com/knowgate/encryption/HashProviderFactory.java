package com.knowgate.encryption;

public class HashProviderFactory {

	public HashAlgorithm algo;
	public String salt;

	public HashProviderFactory(HashAlgorithm algorithmEnum, String salt) {
		this.algo = algorithmEnum;
		this.salt = salt;
	}

	public HashProviderFactory(String algorithmName, String salt) {
		this.algo = HashAlgorithm.valueOf(algorithmName);
		this.salt = salt;
	}

	public HashProvider createProvider(){
		switch(algo) {
		case MD5:
			return new MD5();
		case SHA512:
			return new SHA512(salt);
		}
		return null;
	}
}
