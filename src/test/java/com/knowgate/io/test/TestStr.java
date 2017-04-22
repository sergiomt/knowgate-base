package com.knowgate.io.test;

import org.junit.Test;

import com.knowgate.stringutils.Slugs;

import static org.junit.Assert.assertEquals;

public class TestStr {

	@Test
	public void test01Normalize() throws Exception {
		assertEquals("Nacion",Slugs.transliterate("Naçión"));
		assertEquals("La lluvia en Sevilla",Slugs.transliterate("La llúvia en Sevílla"));
	}

}
