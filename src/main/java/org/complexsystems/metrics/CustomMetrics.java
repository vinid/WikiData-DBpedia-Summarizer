package org.complexsystems.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.complexsystems.tools.StringStaticTools;

public class CustomMetrics {

	/**
	 * Metodo per ottenere la percentuale di parole uguali
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static double customSameAs(String s1, String s2) {
		s1 = StringStaticTools.splitCamelCase(s1);
		s2 = StringStaticTools.splitCamelCase(s2);
		
		List<String> w1 = Arrays
				.asList(StringUtils.splitPreserveAllTokens(s1, null, 0));
		List<String> w2 = Arrays
				.asList(StringUtils.splitPreserveAllTokens(s2, null, 0));
		
		List<String> common = new ArrayList<String>(w1);
		common.retainAll(w2);
		
		double result = 0;
		if (w1.size() > w2.size())
			result = (double) common.size() / w1.size();
		else
			result = (double) common.size() / w2.size();
		
		return result;
	}
	
	/**
	 * Algoritmo di edit-distance
	 * @param word1
	 * @param word2
	 * @return numero di modifiche effettuate
	 */
	public int minDistance(String word1, String word2) {
		int len1 = word1.length();
		int len2 = word2.length();
	 
		// len1+1, len2+1, because finally return dp[len1][len2]
		int[][] dp = new int[len1 + 1][len2 + 1];
	 
		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}
	 
		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}
	 
		//iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < len2; j++) {
				char c2 = word2.charAt(j);
	 
				//if last two chars equal
				if (c1 == c2) {
					//update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;
	 
					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}
	 
		return dp[len1][len2];
	}
	
}
