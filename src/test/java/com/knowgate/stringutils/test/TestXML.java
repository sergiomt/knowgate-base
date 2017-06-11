package com.knowgate.stringutils.test;

import org.junit.Test;

import static org.junit.Assert.assertNull;

import static org.junit.Assert.assertEquals;

import com.knowgate.stringutils.XML;

public class TestXML {

	@Test
	public void testCData() {
		
		assertNull(XML.toCData(null));
		
		assertEquals("<![CDATA[]]]]><![CDATA[>]]>", XML.toCData("]]>"));

		assertEquals("<![CDATA[]]]]><![CDATA[>]]]]><![CDATA[>]]>", XML.toCData("]]>]]>"));
		
		assertEquals("<![CDATA[]]]]><![CDATA[>]]]]><![CDATA[>]]]]><![CDATA[>]]>", XML.toCData("]]>]]>]]>"));

		assertEquals("<![CDATA[plain text]]>", XML.toCData("plain text"));

		System.out.println(XML.toCData("]]>plain text"));

		assertEquals("<![CDATA[]]]]><![CDATA[>plain text]]>", XML.toCData("]]>plain text"));

		assertEquals("<![CDATA[plain text]]]]><![CDATA[>]]>", XML.toCData("plain text]]>"));
			
		assertEquals("<![CDATA[plain text]]]]><![CDATA[>]]]]><![CDATA[>]]>", XML.toCData("plain text]]>]]>"));

		assertEquals("<![CDATA[plain]]]]><![CDATA[>text]]]]><![CDATA[>provided]]>", XML.toCData("plain]]>text]]>provided"));		
		
		assertEquals("<![CDATA[]]]]><![CDATA[>plain]]]]><![CDATA[>text provided]]>", XML.toCData("]]>plain]]>text provided"));

		assertEquals("<![CDATA[plain]]]]><![CDATA[>text provided]]]]><![CDATA[>]]>", XML.toCData("plain]]>text provided]]>"));
		
		assertEquals("<![CDATA[]]]]><![CDATA[>plain]]]]><![CDATA[>text provided]]]]><![CDATA[>]]>", XML.toCData("]]>plain]]>text provided]]>"));

	}
}
