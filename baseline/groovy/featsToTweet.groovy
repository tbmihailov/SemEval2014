// adds the number of words which start with capital letter as a feature to "tweet" annotation.
// requires Token with "orth" feature - created by the default gate tokenizer
// requires tweet annotation - created by the import scripts
inputAS.get("tweet").each{ tw ->
	int upperSize = inputAS.get("Token", tw.startNode.offset, tw.endNode.offset).findAll{ tok ->
		tok.features.orth.equals("upperInitial")
	}.size()
	tw.features.upperSize = upperSize;
}
 
 
// bag of word n-grams
inputAS.get("tweet").each{ tw ->
	toks = inputAS.get("Token", tw.startNode.offset, tw.endNode.offset);
	List<Annotation> sortedToks = new ArrayList<Annotation>(toks);
	Collections.sort(sortedToks, new OffsetComparator());
	
	strings = sortedToks.features.string;
	// indicate first and last token (probably not a good thing)
	strings = ["B_", "BB_"] + strings + ["EE_", "E_"];
	
	String nGram;
	tw.features.nGram = new HashSet<String>();
	for(int i = 0; i < sortedToks.size() + 2; ++i) {
		nGram = strings[i] + "_" + strings[i+1] + "_" + strings[i+2];
		tw.features.nGram.add(nGram);
	}
}
