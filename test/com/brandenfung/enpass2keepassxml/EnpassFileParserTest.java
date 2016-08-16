package com.brandenfung.enpass2keepassxml;

import static org.junit.Assert.*;

import org.junit.Test;

import com.brandenfung.enpass2keepassxml.EnpassFileParser;

public class EnpassFileParserTest {

	@Test
	public void testContainsKey() {
		assertTrue(EnpassFileParser.containsKey("^(Title : )", "Title : Library"));
		assertFalse(EnpassFileParser.containsKey("^(Title : )", "2Title : Library"));
		assertFalse(EnpassFileParser.containsKey("^(Title : )", "Ti2tle : Library"));
		assertFalse(EnpassFileParser.containsKey("^(Title : )", "Name : Title : Library"));
		assertFalse(EnpassFileParser.containsKey("^(Title : )", "Name : Library : Title"));
		assertFalse(EnpassFileParser.containsKey("^(Title : )", "Library"));
		assertFalse(EnpassFileParser.containsKey("^(Title : )", ""));
	}

	@Test
	public void testParseValue() {
		// assume second argument in parseValue is a valid key-value pair field
		assertEquals(EnpassFileParser.parseValue("Url : ", "Url : www.google.ca"), "www.google.ca");
		assertEquals(EnpassFileParser.parseValue("Name : ", "Url : www.google.ca"), "");
	}

	@Test
	public void testGetCustomField() {
		assertEquals(EnpassFileParser.getCustomField("Name : John").length, 2);
		assertEquals(EnpassFileParser.getCustomField("Name : John")[0], "Name : ");
		assertEquals(EnpassFileParser.getCustomField("Name : John")[1], "John");

		assertEquals(EnpassFileParser.getCustomField("Name: John").length, 1);
		assertEquals(EnpassFileParser.getCustomField("Name: John")[0], "Name: John");
		
		assertEquals(EnpassFileParser.
				getCustomField("2123 32  32131 3213 fdasNagame : John").length, 2);
		assertEquals(EnpassFileParser
				.getCustomField("2123 32  32131 3213 fdasNagame : John")[0],
								"2123 32  32131 3213 fdasNagame : ");
		assertEquals(EnpassFileParser
				.getCustomField("2123 32  32131 3213 fdasNagame : John")[1],
								"John");

		assertEquals(EnpassFileParser.getCustomField("").length, 1);
		assertEquals(EnpassFileParser.getCustomField("")[0], "");
		

		assertEquals(EnpassFileParser.getCustomField("Name : John : Jim : Title").length, 2);
		assertEquals(EnpassFileParser.getCustomField("Name : John : Jim : Title")[0], "Name : ");
		assertEquals(EnpassFileParser.getCustomField("Name : John : Jim : Title")[1], "John : Jim : Title");
	}

}
