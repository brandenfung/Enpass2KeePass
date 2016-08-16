package com.brandenfung.enpass2keepassxml;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.brandenfung.enpass2keepassxml.EnpassFileParser;
import com.brandenfung.enpass2keepassxml.EnpassFileParser.EnpassFileParserException;
import com.brandenfung.enpass2keepassxml.model.EnpassEntry;

public class EnpassFileParserTest {
	private static final String SAMPLE_TEXT_FILEPATH = "";

	@Test
	public void testParseEnpassFile() {
		try {
			String filepath1 = "resource/sample_enpass_export.txt";
			ArrayList<EnpassEntry> entries1 = EnpassFileParser.parseEnpassFile(filepath1);
			assertEquals(entries1.size(), 60);

			String filepath2 = "resource/test_sample1.txt";
			ArrayList<EnpassEntry> entries2 = EnpassFileParser.parseEnpassFile(filepath2);
			assertEquals(entries2.size(), 7);
			assertEquals(entries2.get(0).title, "Yahoo2");
			assertEquals(entries2.get(0).username, "Harold");
			assertEquals(entries2.get(0).note, "");
			assertEquals(entries2.get(0).password, ".L&Jtx^m4aTk*84-YY");
			assertEquals(entries2.get(0).url, "");
			assertEquals(entries2.get(0).customFields.size(), 1);
			assertEquals(entries2.get(0).customFields.get("Email"), "myyahoo2@yahoo.com");			

			assertEquals(entries2.get(1).title, "Email");
			assertEquals(entries2.get(1).username, "");
			assertEquals(entries2.get(1).note, "mycousinsemail@email.com\n");
			assertEquals(entries2.get(1).password, "");
			assertEquals(entries2.get(1).url, "");
			assertEquals(entries2.get(1).customFields.size(), 0);
			

			assertEquals(entries2.get(2).title, "brady");
			assertEquals(entries2.get(2).note,"brady@brady.com\n\n\nName : Johnson\nCell : 465746554\nHome Phone : 1324453\nBlood Type: O\n");
			assertEquals(entries2.get(2).username, "");
			assertEquals(entries2.get(2).password, "");
			assertEquals(entries2.get(2).url, "");
			assertEquals(entries2.get(2).customFields.size(), 0);

			assertEquals(entries2.get(3).title, "myhotmail");
			assertEquals(entries2.get(3).username, "hotmail");
			assertEquals(entries2.get(3).note, "some notes for hotmail\n");
			assertEquals(entries2.get(3).password, "6nf(4wY2XChQU!]q]*");
			assertEquals(entries2.get(3).url, "");
			assertEquals(entries2.get(3).customFields.size(), 4);
			assertEquals(entries2.get(3).customFields.get("Email"), "yogurt@hotmail.coooom");
			assertEquals(entries2.get(3).customFields.get("Phone"), "gdfsgdfsg");
			assertEquals(entries2.get(3).customFields.get("Security question"), "what is my cats name");
			assertEquals(entries2.get(3).customFields.get("Security answer"), "jon");
			

			assertEquals(entries2.get(4).title, "myyahoo");
			assertEquals(entries2.get(4).username, "yahoo");
			assertEquals(entries2.get(4).note, "");
			assertEquals(entries2.get(4).password, "6nf(4wY2XChQU!]q]*");
			assertEquals(entries2.get(4).url, "");
			assertEquals(entries2.get(4).customFields.size(), 4);
			assertEquals(entries2.get(4).customFields.get("Email"), "yahoo@yahoo.com");
			assertEquals(entries2.get(4).customFields.get("Phone"), "12345531");
			assertEquals(entries2.get(4).customFields.get("Security question"), "What is my dogs name");
			assertEquals(entries2.get(4).customFields.get("Security answer"), "Joe");
			

			assertEquals(entries2.get(5).title, "Birthday");
			assertEquals(entries2.get(5).username, "");
			assertEquals(entries2.get(5).note, "");
			assertEquals(entries2.get(5).password, "");
			assertEquals(entries2.get(5).url, "");
			assertEquals(entries2.get(5).customFields.size(), 0);
			

			assertEquals(entries2.get(6).title, "Gmail");
			assertEquals(entries2.get(6).username, "brendan");
			assertEquals(entries2.get(6).note, "");
			assertEquals(entries2.get(6).password, ".L&Jtx^m4aTk*84-YY");
			assertEquals(entries2.get(6).url, "gmail.com");
			assertEquals(entries2.get(6).customFields.size(), 1);
			assertEquals(entries2.get(6).customFields.get("Email"), "something@gmail.cooom");

		} catch (EnpassFileParserException e) {
			fail(e.getMessage());
		}
	}

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
