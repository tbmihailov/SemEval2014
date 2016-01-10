inputAS["Sentence"].each { sn ->

	sortedToks = new ArrayList<Annotation>(inputAS.get("Token", sn.start(), sn.end()));
    Collections.sort(sortedToks, new OffsetComparator());
	strings = sortedToks.features.string;
	lookups = inputAS.get("Lookup", sn.start(), sn.end());
	
	// n-grams:
    int minN = 1;
    int maxN = 4;
    String nGram;
    for (int n = minN; n <= maxN; n++) {
        for(int i = 0; i < sortedToks.size() - n + 1; ++i) {
            StringBuilder sb = new StringBuilder();
            String delim = ""
            for (int j = 0; j < n; j++) {
				word = strings[i + j].toLowerCase();
                sb.append(delim).append(word);
				delim = "_";
            }
            fName = n + "gr:" + sb.toString();
            sn.features[fName] = 1;
        }
    }
    
    sn.features["sportCount"] = lookups.findAll {it.features["majorType"].equals("sport") }.size()
    sn.features["scienceCount"] = lookups.findAll {it.features["majorType"].equals("science") }.size()
    
}
