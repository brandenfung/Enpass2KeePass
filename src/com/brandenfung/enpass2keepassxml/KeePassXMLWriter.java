package com.brandenfung.enpass2keepassxml;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.brandenfung.enpass2keepassxml.model.EnpassEntry;

/**
 * Writes KeePass XML files to be imported into KeePass. All {@link EnpassEntry} are 
 * written into a container Group with the name {@link KeePassXMLWriter
 * #KEEPASS_PARENT_GROUP_NAME}.
 * 
 * The following assumptions are made:
 * 	- passwords do not expire
 *  - there are no "Identity Card" Enpass entries
 * 
 * @author Branden
 *
 */
public class KeePassXMLWriter {
	private static final boolean DEBUG = true;

	private static final String KEEPASS_DATABASE_NAME = "Enpass2KeePassXML";
	private static final String KEEPASS_PARENT_GROUP_NAME = "Enpass2KeePassImports";
	private static final String TITLE_TAG = "Title";
	private static final String PASSWORD_TAG = "Password";
	private static final String USERNAME_TAG = "UserName";
	private static final String URL_TAG = "URL";

	private static final String KEEPASS_FILE_TAG = "KeePassFile";
	private static final String ROOT_TAG = "Root";
	private static final String GROUP_TAG = "Group";
	private static final String UUID_TAG = "UUID";
	private static final String NAME_TAG = "Name";
	private static final String NOTES_TAG = "Notes";
	private static final String ENTRY_TAG = "Entry";
	private static final String AUTOTYPE_TAG = "AutoType";
	private static final String ENABLED_TAG = "Enabled";
	private static final String DATA_TRANSFER_OBFUSCATION_TAG = "DataTransferObfuscation";
	private static final String STRING_TAG = "String";
	private static final String KEY_TAG = "Key";
	private static final String VALUE_TAG = "Value";
	private static final String HISTORY_TAG = "History";

//	/**
//	 * Writes a KeePass 1.x XML file given Enpass entries.
//	 * 
//	 * @param enpassEntries Enpass Entries to write to the KeePass xml
//	 * @param outputPath Output path of the KeePassxml
//	 */
//	 // Unfinished, finish if someone needs it. Most likely use KeePass 2.x xml files though
//	public static void writeKeePassXml1(ArrayList<EnpassEntry> enpassEntries, String outputPath) {
//		try {
//			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//			Document doc = docBuilder.newDocument();
//
//			// pwlist element
//			Element pwlist = doc.createElement("pwlist");
//			doc.appendChild(pwlist);
//
//			for(EnpassEntry entry: enpassEntries) {
//
//				// Add pwentry
//				Element pwentry = doc.createElement("pwentry");
//				pwlist.appendChild(pwentry);
//
//				// Add fields to pwentry
//				Element group = doc.createElement("group");
//
//				Element title = doc.createElement("title");
//
//				Element username = doc.createElement("username");
//
//				Element url = doc.createElement("url");
//
//				Element password = doc.createElement("password");
//
//				Element notes = doc.createElement("notes");
//
//				Element uuid = doc.createElement("uuid");
//
//				Element image = doc.createElement("image");
//
//				Element creationtime = doc.createElement("creationtime");
//
//				Element lastmodtime = doc.createElement("lastmodtime");
//
//				Element lastaccesstime = doc.createElement("lastaccesstime");
//
//				Element expiretime = doc.createElement("expiretime");				
//
//			}
//
//
//		} catch (ParserConfigurationException pce) {
//			pce.printStackTrace();
//		} 
//	}

	/**
	 * Writes a KeePass 2.x XML file given Enpass entries.
	 * 
	 * @param enpassEntries Enpass Entries to write to the KeePass xml
	 * @param outputPath Output path of the KeePassxml
	 */
	protected static void writeKeePassXml2(ArrayList<EnpassEntry> enpassEntries, String outputPath) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			// KeePassFile element
			Element keePassFile = doc.createElement(KEEPASS_FILE_TAG);
			doc.appendChild(keePassFile);

			// Root element
			Element root = doc.createElement(ROOT_TAG);
			keePassFile.appendChild(root);

			// Base Group element with UUI, Name, and Notes tags
			Element baseGroup = generateGroupElement(doc, root);

			// Container group containing the Enpass entries
			Element containerGroup = generateContainerGroup(doc, baseGroup);

			// Add entries to general group
			for(EnpassEntry item: enpassEntries) {
				generateEntryElement(doc, item, containerGroup);
			}

			// Write file
			doc.setXmlStandalone(true);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(outputPath));			

			transformer.transform(source, result);			
		} catch (ParserConfigurationException e) {
			if (DEBUG) e.printStackTrace();
		} catch (TransformerException e) {
			if (DEBUG) e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Generates an entry element with populated fields given by the {@link EnpassEntry}.
	 * 
	 * @param doc XML Document
	 * @param item {@link EnpassEntry} with fields to copy to entry element
	 * @param containerGroup Group element that contains all {@link EnpassEntry}
	 * @return Entry element with populated fields
	 */
	protected static Element generateEntryElement(Document doc, EnpassEntry item, Element containerGroup) {
		
		/***************************************************************************************
		 * Entry with UUID, Title, Username, Password, URL, and additional custom fields
		 * 
		 * 
		 ***************************************************************************************/
		
		// Create string elements for fields
		Element entry = doc.createElement(ENTRY_TAG);
		Element entryUUID = doc.createElement(UUID_TAG);
		Element entryNotes = doc.createElement(STRING_TAG);
		Element entryPassword = doc.createElement(STRING_TAG);
		Element entryTitle = doc.createElement(STRING_TAG);
		Element entryURL = doc.createElement(STRING_TAG);
		Element entryUserName = doc.createElement(STRING_TAG);

		// Create key elements for field
		Element notesKey = doc.createElement(KEY_TAG);
		Element passwordKey = doc.createElement(KEY_TAG);
		Element titleKey = doc.createElement(KEY_TAG);
		Element urlKey = doc.createElement(KEY_TAG);
		Element userNameKey = doc.createElement(KEY_TAG);

		// Create value elements for field
		Element notesValue = doc.createElement(VALUE_TAG);
		Element passwordValue = doc.createElement(VALUE_TAG);
		Element titleValue = doc.createElement(VALUE_TAG);
		Element urlValue = doc.createElement(VALUE_TAG);
		Element userNameValue = doc.createElement(VALUE_TAG);

		// Populate UUID and key-value elements
		entryUUID.appendChild(doc.createTextNode(generateStringUUID()));

		notesKey.appendChild(doc.createTextNode(NOTES_TAG));
		passwordKey.appendChild(doc.createTextNode(PASSWORD_TAG));
		titleKey.appendChild(doc.createTextNode(TITLE_TAG));
		urlKey.appendChild(doc.createTextNode(URL_TAG));
		userNameKey.appendChild(doc.createTextNode(USERNAME_TAG));

		notesValue.appendChild(doc.createTextNode(item.note));
		passwordValue.appendChild(doc.createTextNode(item.password));
		titleValue.appendChild(doc.createTextNode(item.title));
		urlValue.appendChild(doc.createTextNode(item.url));
		userNameValue.appendChild(doc.createTextNode(item.username));

		// Attach children
		containerGroup.appendChild(entry);
		entry.appendChild(entryUUID);
		entry.appendChild(entryNotes);
		entry.appendChild(entryPassword);
		entry.appendChild(entryTitle);
		entry.appendChild(entryURL);
		entry.appendChild(entryUserName);
		entryNotes.appendChild(notesKey);
		entryNotes.appendChild(notesValue);
		entryPassword.appendChild(passwordKey);
		entryPassword.appendChild(passwordValue);
		entryTitle.appendChild(titleKey);
		entryTitle.appendChild(titleValue);
		entryURL.appendChild(urlKey);
		entryURL.appendChild(urlValue);
		entryUserName.appendChild(userNameKey);
		entryUserName.appendChild(userNameValue);
		
		// Now add custom fields
		HashMap<String, String> customFields = item.customFields;
		for(Entry<String, String> customField : customFields.entrySet()) {
			// Get custom field key-value pair
		    String key = customField.getKey();
		    String value = customField.getValue();

		    // Create require elements for field
			Element strElement = doc.createElement(STRING_TAG);
			Element keyElement = doc.createElement(KEY_TAG);
			Element valElement = doc.createElement(VALUE_TAG);
			
			// Populate key and value elements
			keyElement.appendChild(doc.createTextNode(key));		
			valElement.appendChild(doc.createTextNode(value));			

			// Attach children
			entry.appendChild(strElement);
			strElement.appendChild(keyElement);
			strElement.appendChild(valElement);
			
		}		
		//***************************************************************************************
		
		// AutoType element
		Element autoType = doc.createElement(AUTOTYPE_TAG);
		Element enabled = doc.createElement(ENABLED_TAG);
		Element dataTransOb = doc.createElement(DATA_TRANSFER_OBFUSCATION_TAG);

		enabled.appendChild(doc.createTextNode("true"));
		dataTransOb.appendChild(doc.createTextNode("0"));

		entry.appendChild(autoType);
		autoType.appendChild(enabled);
		autoType.appendChild(dataTransOb);

		// History element
		Element history = doc.createElement(HISTORY_TAG);
		entry.appendChild(history);		
		
		return entry;
	}
	
	/**
	 * Generates the group element that contains information about the database.
	 * 
	 * @param doc XML Document
	 * @param parent Parent element containing this group element
	 * @return Group element for database information
	 */
	protected static Element generateGroupElement(Document doc, Element parent) {
		Element baseGroup = doc.createElement(GROUP_TAG);
		Element baseUUID = doc.createElement(UUID_TAG);
		Element baseName = doc.createElement(NAME_TAG);
		Element baseNotes = doc.createElement(NOTES_TAG);

		baseUUID.appendChild(doc.createTextNode(generateStringUUID()));
		baseName.appendChild(doc.createTextNode(KEEPASS_DATABASE_NAME));

		parent.appendChild(baseGroup);			
		baseGroup.appendChild(baseUUID);
		baseGroup.appendChild(baseName);
		baseGroup.appendChild(baseNotes);
		
		return baseGroup;
	}
	
	/**
	 * 
	 * Generates the container group element that contains all the entry elements.
	 * 
	 * @param doc XML Document
	 * @param parent Parent element contining this group element
	 * @return Container group element
	 */
	protected static Element generateContainerGroup(Document doc, Element parent) {

		Element generalGroup = doc.createElement(GROUP_TAG);
		Element generalUUID = doc.createElement(UUID_TAG);
		Element generalName = doc.createElement(NAME_TAG);

		generalUUID.appendChild(doc.createTextNode(generateStringUUID()));
		generalName.appendChild(doc.createTextNode(KEEPASS_PARENT_GROUP_NAME));	

		parent.appendChild(generalGroup);
		generalGroup.appendChild(generalUUID);
		generalGroup.appendChild(generalName);
		
		return generalGroup;
	}

	public static String generateStringUUID() {
		UUID uuid = UUID.randomUUID();
		ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
		
		Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(buffer.array());
	}


}
