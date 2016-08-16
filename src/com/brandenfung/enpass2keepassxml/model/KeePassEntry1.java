package com.brandenfung.enpass2keepassxml.model;

import com.brandenfung.enpass2keepassxml.KeePassXMLWriter;

/**
 * Immutable KeePass 1.x XML entry
 * 
 * @author Branden
 *
 */
public class KeePassEntry1 {
	public final String group;
	public final String title;
	public final String username;
	public final String url;
	public final String password;
	public final String notes;
	public final String uuid = KeePassXMLWriter.generateStringUUID(); // we can actually use any uuid KeePass ... 
	public final String image;													 // .. can generate a new one on import
	public final String creationTime; // on creation, creation, last mod, and last access time are the same
	public final String lastModTime;
	public final String lastAccessTime;
	public final String expireTime = "2999-12-28T23:59:59"; // assume passwords don't expire

	public static class Builder {
		private String group = "";
		private String title = "";
		private String username = "";
		private String url = "";
		private String password = "";
		private String notes = "";
		private String image = "";
		private String time = "";

		public void setGroup(String group) {
			this.group = group;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public void setTimes(String time) {
			this.time = time;
		}

		public KeePassEntry1 build() {
			return new KeePassEntry1(this.group, this.title, this.username, this.url, 
					this.password, this.notes, this.image, this.time);
		}
	}

	private KeePassEntry1(String group, String title, String username, String url,
			String password, String notes, String image, String time) {
		this.group = group;
		this.title = title;
		this.username = username;
		this.url = url;
		this.password = password;
		this.notes = notes;
		this.image = image;
		this.creationTime = time;
		this.lastModTime = time;
		this.lastAccessTime = time;
	}
}
