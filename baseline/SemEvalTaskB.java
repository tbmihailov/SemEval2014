package com.sm.ner.file.sentiment;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;
import gate.util.GateException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

public class SemEvalTaskB {

	/* Example:
	 * 264183816548130816	15140428	positive	Gas by my house hit $3.39!!!! I\u2019m going to Chapel Hill on Sat. :)
	 */
	public static void main(String[] args) throws Exception {
		// gatePath "/home/yasen/programs/gate-7.0"
		String gatePath = args[0];
		// dataStorePath "/home/yasen/projects/nlp_analysis/corpora/semeval2014/"
		String dataStorePath = args[1];
		// tweetsPath "/home/yasen/projects/SU/text-mining-2014/data/no_download/train/cleansed/B"
		String tweetsPath = args[2];
		
		SerialDataStore sds = startGate(gatePath, dataStorePath);

		Corpus c = Factory.newCorpus("semEvalB");
		Corpus corpus = (Corpus) sds.adopt(c, null);
		BufferedReader reader = null;
		File tweetsDir = new File(tweetsPath);
		if(!tweetsDir.isDirectory()) {
			System.out.println("tweetsPath must be directory: " + tweetsPath);
			return;
		}
		for(File tweets : tweetsDir.listFiles()) {
			try {
				reader = new BufferedReader(new FileReader(tweets));
				String line = null;
				int count = 0;
				List<Anno> docAnns = new ArrayList<Anno>();
				StringBuilder docText = new StringBuilder();
				int i = 0;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.length() < 4) {
						continue;
					}
					int docStartPos = docText.length();
					String[] parts = line.split("\t");
					if(parts.length != 4) {
						System.out.println("ERROR: " + line);
						continue;
					}
					String id1 = parts[0];
					String id2 = parts[1];
					String label = parts[2];
					String text = parts[3];
					
					if (label.equals("objective-OR-neutral") || label.equals("neutral")|| label.equals("objective")) {
						label = "neutral";
					}
					text = StringEscapeUtils.unescapeJava(text);
					
					Anno sentAnn = new Anno(docStartPos, docStartPos + text.length(), label, id1, id2);
					docAnns.add(sentAnn);
					docText.append(text).append(" \n\n");
					count++;
					if(count % 500 == 0) {
						addDocument(docAnns, docText.toString(), sds, corpus, tweets.getName() + i);
						++i;
						docText = new StringBuilder();
						docAnns = new ArrayList<Anno>();
					}
				}
				addDocument(docAnns, docText.toString(), sds, corpus, tweets.getName() + i);
				System.out.println(count);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				reader.close();
			}
		}
		sds.close();
	}

	private static void addDocument(List<Anno> docAnnots, String docText, SerialDataStore ds, Corpus cor, String docName)
			throws Exception {
		FeatureMap params = Factory.newFeatureMap();
		params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, docText);
		params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "UTF-8");
		Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
				doc.setName(docName);
		for (Anno ann : docAnnots) {
			FeatureMap fm = Factory.newFeatureMap();
			fm.put("id1", ann.id1);
			fm.put("id2", ann.id2);
			doc.getAnnotations("GOLD").add((long) ann.start, (long) ann.end, ann.type, fm);
			FeatureMap twfm = Factory.newFeatureMap();
			twfm.put("id1", ann.id1);
			twfm.put("id2", ann.id2);
			twfm.put("label", ann.type);
			doc.getAnnotations("GOLD").add((long) ann.start, (long) ann.end, "tweet", twfm);
		}
		cor.add(doc);
		ds.sync(cor);
	}

	private static SerialDataStore startGate(String gatePath, String dataStorePath) throws GateException,
			MalformedURLException, PersistenceException {
		File gateHome = new File(gatePath);
		Gate.setGateHome(gateHome);
		Gate.setPluginsHome(gateHome);
		Gate.init();

		File file = new File(dataStorePath);
		file.mkdirs();
		SerialDataStore sds = null;
		if (file.listFiles().length == 0) {
			sds = (SerialDataStore) Factory.createDataStore("gate.persist.SerialDataStore", file.toURI().toURL()
					.toString());
		} else {
			sds = (SerialDataStore) Factory.openDataStore("gate.persist.SerialDataStore", file.toURI().toURL()
					.toString());
		}
		return sds;
	}
	
	private static class Anno {
		public Anno(int start, int end, String type, String id1, String id2) {
			this.type = type;
			this.start = start;
			this.end = end;
			this.id1 = id1;
			this.id2 = id2;
		}

		public String type;
		public int start;
		public int end;
		public String id1;
		public String id2;
	}
}
