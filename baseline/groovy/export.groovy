// export to file
new File(doc.name).withWriterAppend{ out ->
    
    tweets = doc.getAnnotations().get("tweet")
    sortedTweets = new ArrayList<Annotation>(tweets);
    Collections.sort(sortedTweets, new OffsetComparator());

    // doc.getAnnotations().get("tweet").each{ tw ->
    sortedTweets.each{ tw ->
        String t = "\t"
        
        
        // export id1
        if(tw.features.id1 != null) {
            out.write(tw.features.id1 + t);
        }
        
        // export id2
        if(tw.features.id2 != null) {
            out.write(tw.features.id2 + t);
        }
        
        // export label
        if(tw.features.label != null) {
            out.write(tw.features.label + t);
        }
        
        // export tweet
        out.write(doc.stringFor(tw) + t);
        
        Map<String, Integer> features = new HashMap<String, Integer>();
        
        tw.features.each{ fName, fValue ->
            if(!fName.equals("label") && !fName.equals("id1") && !fName.equals("id2")) {
                if(fValue instanceof Collection && !(fValue instanceof String)) {
                    
                    fValue.each{ val ->
                    //    out.write(val.toString() + t);
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
                //    out.write(newFeat + t);
                    
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
                String cleanName = fName.replace("=", "eq.");
                out.write(cleanName + "=" + String.valueOf(fValue) + t);
            }
        }
        out.write("\n");
    }
}
