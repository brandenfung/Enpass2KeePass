package com.brandenfung.enpass2keepassxml.model;

import java.util.HashMap;

/**
 * Immutable Enpass entry.
 * 
 * @author Branden
 *
 */
public class EnpassEntry {
	public final String title;
	public final String username;
	public final String password;
	public final String url;
	public final String note;
	public final HashMap<String, String> customFields;

	public static class Builder {
		private String title;
		private String username;
		private String password;
		private String url;
		private String note;
		private HashMap<String, String> customFields = new HashMap<String, String>();

		public void setTitle(String title) {
			this.title = title;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void setNote(String note) {
			this.note = note;
		}
		
		public void appendNote(String line) {
			this.note = this.note.concat("\n").concat(line);
		}

		public void addCustomField(String key, String value) {
			customFields.put(key, value);
		}

		/**
		 * Clears all fields in the builder
		 */
		public void clear() {
			this.title  = null;
			this.username  = null;
			this.password  = null;
			this.url  = null;
			this.customFields.clear();
		}

		public boolean hasTitle() {
			return title != null;
		}

		/**
		 * Builds the {@link EnpassEntry} and resets the builder to get ready for
		 * more building.
		 * 
		 * @return
		 * @throws EnpassItemException
		 */
		public EnpassEntry build() {
			EnpassEntry item = new EnpassEntry(this.title, this.username, this.password, 
											   this.url, this.note, this.customFields);	
			clear();
			return item;	
		}

	}

	private EnpassEntry(String title, String username, String password, String url, 
			String note, HashMap<String, String> customFields) {
		this.title = title == null ? "" : title;
		this.username = username == null ? "" : username;
		this.password = password == null ? "" : password;
		this.url = url == null ? "" : url;
		this.note = note == null ? "" : note;
		this.customFields = new HashMap<String, String>(customFields);
	}

	public static class EnpassItemException extends Exception {
		private static final long serialVersionUID = 1124524828218470260L;
		public static final String TAG = "Either title, username, or password not set in EnpassItem";
	}

}
