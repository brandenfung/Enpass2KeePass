package com.brandenfung.enpass2keepassxml;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.brandenfung.enpass2keepassxml.EnpassFileParser.EnpassFileParserException;
import com.brandenfung.enpass2keepassxml.model.EnpassEntry;

public class Enpass2KeePass {

	private static final String ROOT_FILENAME = "Enpass2KeePass";

	/**
	 * @param args args[0] is the input txt file, args[1] is the output 
	 * 			   file directory
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Must specify a valid input " + 
					"Enpass exported .txt file and " + 
					"a valid output directory as arg1 and arg2.");		
			return;
		}

		File inputPath = new File(args[0]);
		File outputDir = new File(args[1]);

		try {
			enpass2KeePassXml(inputPath, outputDir);
		} catch (Enpass2KeePassInputException e) {
			System.out.println(e.getMessage());
		} catch (EnpassFileParserException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Create a KeePass 2.x xml file given an exported Enpass txt file.
	 * 
	 * @param inputPath Path of the exported Enpass txt file
	 * @param outputDir Output directory to store the output xml
	 * @return
	 * @throws Enpass2KeePassInputException
	 */
	public static String enpass2KeePassXml(File inputPath, File outputDir) 
			throws Enpass2KeePassInputException, EnpassFileParserException {	

		if (inputPath == null && outputDir == null) {
			throw new Enpass2KeePassInputException("Must specify a valid input Enpass " + 
					"exported .txt file and valid output directory");			
		} else if (inputPath == null) {
			throw new Enpass2KeePassInputException("Must specify a valid input Enpass exported .txt file");			
		} else if (outputDir == null) {
			throw new Enpass2KeePassInputException("Must specify a valid output directory");			
		} else {

			// Generate the output file path
			File outputPath = new File(outputDir, generateFileName());

			if (!inputPath.isFile()) {
				throw new Enpass2KeePassInputException("Must specify a valid input Enpass exported .txt file");
			}

			if (!outputDir.isDirectory()) {
				throw new Enpass2KeePassInputException("Must specify a valid output directory");
			} 	

			ArrayList<EnpassEntry> enpassEntries = EnpassFileParser.parseEnpassFile(inputPath.getAbsolutePath());
			

			KeePassXMLWriter.writeKeePassXml2(enpassEntries, outputPath.getAbsolutePath());

			return outputPath.getAbsolutePath();
		}
	}

	private static String generateFileName() {
		String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		return ROOT_FILENAME.concat("_").concat(date).concat(".xml");
	}

	public static class Enpass2KeePassInputException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7014266058400513867L;

		public Enpass2KeePassInputException(String msg) {
			super(msg);
		}
	}

}
