package org.complexsystems.interfaces;

import java.util.ArrayList;

import org.complexsystems.tools.Pair;

public interface Retriever {
	
	/**
	 * Metodo per ottenere l'elenco di coppie proprietà-valore di una
	 * determinata entità definita dalla stringa in input
	 * 
	 * @param searchString
	 * @return un array di elementi Pair (proprietà-valore)
	 */
	ArrayList<Pair<String, String>> getAllPairs(String searchString);
	
	String getDescription(String text);
	
	
}
