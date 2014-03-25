// export to file
new File(doc.name).withWriterAppend{ out ->
	doc.getAnnotations().get("tweet").each{ tw ->
		String t = "\t"
		// export two IDs
		out.write(tw.features.id1 + t);
		out.write(tw.features.id2 + t);
		
		// export tweet
		out.write(doc.stringFor(tw) + t);
		
		// export features
		// export features which are lists
		tw.features.each{ fName, fValue ->
			if(!fName.equals("label")) {
				if(fValue instanceof Collection && !(fValue instanceof String)) {
					fValue.each{ val ->
						out.write(val.toString() + t);
					}
				}
				else {
					out.write(fName + "=" + fValue.toString() + t);
				}
			}
        }
		
		// export label
		if(tw.features.label != null) {
			out.write(tw.features.label);
		}
		out.write("\n");
	}
}
