package com.brandenfung.enpass2keepassxml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.brandenfung.enpass2keepassxml.model.EnpassEntry;

/**
 * Parses an exported Enpass txt file. 
 * 
 * The following assumptions are made:
 * 	- Enpass entries start with a "Title : " tag
 *  - Enpass entries can only contain one entry called "Title", as a result
 *  	the "Identity Card" entry option provided by Enpass will not work since
 *  	it has two "Title" fields
 *  - All field keys contain the regex pattern (?<=^.*?\s:\s) 
 *  	(eg. "Name : ", "Email : " are field keys. An example field key-value 
 *  	pair is: "Name : John" where "Name" is the key and "John" is the value )
 * 
 * Notes:
 * 	- You can not assume that Enpass entries are separated using a new line 
 * 		delimiter. There is a bug (maybe?) where Enpass entries with an empty
 * 		"Notes" field do not end with a new line, and thus the entries are 
 * 		"clumped" together. The current solution is to used the "Title"
 * 		field as a delimiter as all Enpass entries must have a title.
 *  
 * @author Branden
 *
 */
public final class EnpassFileParser {

	private static final boolean DEBUG = true;
	private static final String DEBUG_TAG = "EnpassFileParser";

	// Delimiter for field values
	private static final String TITLE_DELIM = "Title : ";
	private static final String USERNAME_DELIM = "Username : ";
	private static final String PASSWORD_DELIM = "Password : ";
	private static final String URL_DELIM = "Url : ";
	private static final String NOTE_DELIM = "Note : ";

	// Regex for finding field keys
	private static final String TITLE_REGEX = "^(Title : )";
	private static final String USERNAME_REGEX = "^(Username : )";
	private static final String PASSWORD_REGEX = "^(Password : )";
	private static final String URL_REGEX = "^(Url : )";
	private static final String NOTE_REGEX = "^(Note : )";
	private static final String CUSTOM_FIELD_REGEX = "(?<=^.*?\\s:\\s)";

	/**
	 *  Parse an exported Enpass .txt file into {@link EnpassEntry} 
	 *  items.
	 *  
	 * @param filepath Path of the .txt file.
	 * @return List of {@link EnpassEntry} populated with data from
	 * 			the text file.
	 */
	public static ArrayList<EnpassEntry> parseEnpassFile(String filepath) 
			throws EnpassFileParserException{

		ArrayList<EnpassEntry> items = new ArrayList<>();

		try {

			// Read txt file
			FileReader fr = new FileReader(filepath);

			// Wrap in buffer
			BufferedReader br = new BufferedReader(fr);

			EnpassEntry.Builder builder = new EnpassEntry.Builder();
			String line = "";
			String lastDelim = "";
			while ((line = br.readLine()) != null) {

				if (line.contains(TITLE_DELIM)) { // Title  is used as delimiter for each enpass item
					lastDelim = TITLE_DELIM;
					if (builder.hasTitle()) {
						items.add(builder.build());
					} 
					builder.setTitle(parseTitle(line));		
				} else if (containsUsername(line)) {
					lastDelim = USERNAME_DELIM;
					builder.setUsername(parseUsername(line));
				} else if (line.contains(PASSWORD_DELIM)) {
					lastDelim = PASSWORD_DELIM;
					builder.setPassword(parsePassword(line));					
				} else if (line.contains(URL_DELIM)) {
					lastDelim = URL_DELIM;
					builder.setUrl(parseUrl(line));					
				} else if (line.contains(NOTE_DELIM)) { 
					lastDelim = NOTE_DELIM;
					builder.setNote(parseNote(line));
				} else if (lastDelim.equals(NOTE_DELIM)) { // Check for continuation of a note (ie. multi-line note)
					builder.appendNote(line);
				} else {
					// Check for custom field
					String[] kvPair = getCustomField(line);
					if (kvPair.length == 2) { // assume field keys have this regex format
						String key = kvPair[0].substring(0, kvPair[0].length()-3); // remove colon
						String val = kvPair[1];
						builder.addCustomField(key, val);							
					} else {
						System.out.println(DEBUG_TAG + ": Something went wrong reading custom fields! Field = " +
								kvPair[0] );	
						throw new EnpassFileParserException("Could not parse Enpass text file. " + 
								"Please check the exported Enpass text file.");
					}

				}
			}
			items.add(builder.build()); // add the last item here or we will miss it

			br.close();

		} catch (FileNotFoundException e) {
			if (DEBUG) {
				System.out.println(DEBUG_TAG + ": Input File Not Found");
			}

		} catch (IOException e) {
			if (DEBUG) {
				System.out.println(DEBUG_TAG + ": Error reading file");				
			}
		}

		return items;
	}

	protected static boolean containsTitle(String line) {
		return containsKey(TITLE_REGEX, line);
	}

	protected static boolean containsUsername(String line) {
		return containsKey(USERNAME_REGEX, line);
	}

	protected static boolean containsPassword(String line) {
		return containsKey(PASSWORD_REGEX, line);
	}

	protected static boolean containsUrl(String line) {
		return containsKey(URL_REGEX, line);
	}

	protected static boolean containsNote(String line) {
		return containsKey(NOTE_REGEX, line);
	}

	protected static String parseTitle(String line) {
		return parseValue(TITLE_DELIM, line);
	}

	protected static String parseUsername(String line) {
		return parseValue(USERNAME_DELIM, line);
	}

	protected static String parsePassword(String line) {
		return parseValue(PASSWORD_DELIM, line);
	}

	protected static String parseUrl(String line) {
		return parseValue(URL_DELIM, line);
	}

	protected static String parseNote(String line) {
		return parseValue(NOTE_DELIM, line);
	}

	protected static boolean containsKey(String key, String line) {
		return line.split(key).length == 2;
	}

	protected static String parseValue(String key, String line) {
		if (!line.contains(key)) {
			return "";
		} else {
			return line.substring( + key.length()); 
		}
	}	

	protected static String[] getCustomField(String line) {
		return line.split(CUSTOM_FIELD_REGEX, 2);
	}

	public static class EnpassFileParserException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3893731545826333624L;

		public EnpassFileParserException(String msg) {
			super(msg);
		}

	}

}
