package com.brandenfung.enpass2keepassxml.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.brandenfung.enpass2keepassxml.model.EnpassEntry.Builder;

import junit.framework.AssertionFailedError;

public class EnpassEntryTest {

	@Test
	public void testBuilder() {
		// Test empty builder
		Builder builder = new Builder();
		assertTrue(isBuilderCleared(builder));		
		
		// Test populated builder
		builder.setTitle("Title");
		builder.setPassword("Password");
		builder.setUrl("URL");
		builder.setNote("Note");
		builder.setUsername("UserName");
		builder.addCustomField("Field1", "Value1");
		builder.addCustomField("Field2", "Value2");
		builder.addCustomField("Field3", "Value3");
		builder.addCustomField("Field4", "Value4");
		EnpassEntry entry2 = builder.build();
		assertEquals(entry2.customFields.size(), 4);
		assertEquals(entry2.customFields.get("Field1"), "Value1");
		assertEquals(entry2.customFields.get("Field2"), "Value2");
		assertEquals(entry2.customFields.get("Field3"), "Value3");
		assertEquals(entry2.customFields.get("Field4"), "Value4");
		assertEquals(entry2.title, "Title");
		assertEquals(entry2.password, "Password");
		assertEquals(entry2.url, "URL");
		assertEquals(entry2.note, "Note");
		assertEquals(entry2.username, "UserName");
		assertTrue(isBuilderCleared(builder));
		
	}

	public boolean isBuilderCleared(Builder builder) {
		try {
			EnpassEntry entry = builder.build();
			assertEquals(entry.customFields.size(), 0);
			assertEquals(entry.title, "");
			assertEquals(entry.url, "");
			assertEquals(entry.note, "");
			assertEquals(entry.username, "");
			assertEquals(entry.password, "");
			return true;
		} catch (AssertionFailedError e) {
			return false;
		}
	}

}
