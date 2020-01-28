package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	//The hash set of all noise words.
	HashSet<String> noiseWords;
	
	//Creates the keyWordsIndex and noiseWords hash tables.
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		if(docFile ==null) {
			throw new FileNotFoundException();
		}
		HashMap<String,Occurrence> keywords = new HashMap<String, Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		//loops through the docfile for keywords
		while (sc.hasNext()) {
			String currword = getKeyword(sc.next());
			//skips empty words
			if(!currword.equals("")) {
				//if word already exists just increment
				if(keywords.containsKey(currword)) {
					Occurrence occ = keywords.get(currword);
					occ.frequency++;
				}
				//makes new word/entry if the keyword doesnt already exists
				else {
					keywords.put(currword,new Occurrence(docFile, 1));
				}
			}
		}
		sc.close();
		return keywords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for (String mergekeys : kws.keySet()) {
			if (keywordsIndex.containsKey(mergekeys)) {
				keywordsIndex.get(mergekeys).add(kws.get(mergekeys));
				insertLastOccurrence(keywordsIndex.get(mergekeys));
			}
			else {
				ArrayList<Occurrence> temp = new ArrayList<Occurrence>();
				temp.add(kws.get(mergekeys));
				keywordsIndex.put(mergekeys,temp);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		word = word.toLowerCase();
		//checks if word starts with illegal character
		if (!Character.isLetter(word.charAt(0))){
			return "";
		}
		//removes puncuation at the end of the word
		if (!Character.isLetter(word.charAt(word.length()-1))){
			for (int i = word.length()-1; i >= 0; i--){
				//terminal condition
				if (Character.isLetter(word.charAt(i))){
					break;
				}
				word = word.substring(0, i);
			}
		}
		//checks for punctuationn within the word and if word is within noisewords
		for(int i = 0; i <= word.length()-1; i++){
			if (!Character.isLetter(word.charAt(i))||noiseWords.contains(word)){
				return "";
			}
		}
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Integer> midpoints = new ArrayList<Integer>();
		int l = 0, h = occs.size()-2,m = (l+h)/2, t = occs.get(occs.size()-1).frequency;
		if(occs.size()<2) {
			return null;
		}
		while (h>=l){
			m=(l+h)/2;
			midpoints.add(m);
			if (t == occs.get(m).frequency) {
				break;
			}
			if(t <occs.get(m).frequency) {
				l = m +1;
			}
			if(t>occs.get(m).frequency) {
				h = m-1;
			}
		}
		occs.add(m+1,occs.remove(occs.size()-1));
		if(h<l)occs.add(l,occs.remove(occs.size()-1));
		return midpoints;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word=sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		kw1=kw1.toLowerCase();
		kw2=kw2.toLowerCase();
		
		ArrayList<String> top5 = new ArrayList<String>();
		ArrayList<Occurrence> kw1occ = new ArrayList<Occurrence>();
		ArrayList<Occurrence> kw2occ = new ArrayList<Occurrence>();

		if(keywordsIndex.get(kw1)!=null) {
			kw1occ = keywordsIndex.get(kw1);
		}
		if(keywordsIndex.get(kw2)!=null) {
			kw1occ = keywordsIndex.get(kw2);
		}
		for (int i = 0; i < kw1occ.size()&&top5.size()<5; i++){
			if(top5.size()<5) {
				int k1 = kw1occ.get(i).frequency;
				String k1name = kw1occ.get(i).document;
				for(int j = 0; j < kw2occ.size();i++) {
					int k2 =kw2occ.get(j).frequency;
					String k2name = kw2occ.get(j).document;
					if(k1>=k2) {
						if(!top5.contains(k1name)&&i<5) {
							top5.add(k1name);
						}
					}
					if(k1<k2) {
						if(!top5.contains(k2name)&&i<5) {
							top5.add(k2name);
						}
					}
				}
			}
		}
		return top5;
	}
}