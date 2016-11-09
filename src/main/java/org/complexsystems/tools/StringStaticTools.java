package org.complexsystems.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class StringStaticTools {
	
	private static final String[] STOPWORDS =  {"a", "of", "the", "is", "are", "or", "and", "him", "her", "his"};
	
	/**
	 * Metodo per creare una stringa con le parole separate, partendo
	 * da una stringa in camelCase
	 * @param s
	 * @return
	 */
	public static String splitCamelCase(String s) {
		if (s.contains(" ")) //no camelCase word
			return s.toLowerCase().trim();
		
		return StringUtils.join(
				StringUtils.splitByCharacterTypeCamelCase(s), " ")
				.toLowerCase().trim();
	}
		
	/**
	 * Metodo per eliminare le stopWords da una stringa
	 * @param string
	 * @return
	 */
	public static String removeStopWords(String string)
	{		
		List<String> strList = new ArrayList<String>(Arrays.asList(StringUtils.splitByWholeSeparator(string, " ")));
		strList.removeAll(Arrays.asList(STOPWORDS));
		return StringUtils.join(strList, " ").toLowerCase().trim();
	}
	
	public static void main(String args[]) {
		System.out.println(StringStaticTools.removeStopWords("place of birth"));
	}
}
