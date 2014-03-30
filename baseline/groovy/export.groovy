// export to file
new File(doc.name).withWriterAppend{ out ->
	doc.getAnnotations().get("tweet").each{ tw ->
		String t = "\t"
		
		// export label
		if(tw.features.label != null) {
			out.write(tw.features.label + t);
		}
		
		// export tweet
		out.write(doc.stringFor(tw) + t);
		
		Map<String, Integer> features = new HashMap<String, Integer>();
		
		tw.features.each{ fName, fValue ->
			if(!fName.equals("label")) {
				if(fValue instanceof Collection && !(fValue instanceof String)) {
					
					fValue.each{ val ->
					//	out.write(val.toString() + t);
						if(features.containsKey(val)) {
							int i = features.get(val);
							++i;
							features.put(val, i);
						} else {
							features.put(val, 1);
						}
					}
				} else {
					String newFeat = fName + "=" + fValue.toString();
				//	out.write(newFeat + t);
					
					if(features.containsKey(newFeat)) {
						int i = features.get(newFeat);
						++i;
						features.put(newFeat, i);
					} else {
						features.put(newFeat, 1);
					}
				}
			}
		}
		
		features.each{ fName, fValue ->
			if(fValue.intValue() == 1) {
				out.write(fName + t);
			} else {
				out.write(fName + "=" + String.valueOf(fValue) + t);
			}
        }
		out.write("\n");
	}
}
