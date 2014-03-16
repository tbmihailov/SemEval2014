package postag;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ConditionalSerialAnalyserController;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.GateUtil;
import util.IO;
import util.TweetNormalizer;

public class TweetPOSTagTest {

    public static void main(String[] args) throws Exception {
        // gatePath "/home/yasen/programs/gate-7.0"
        String gatePath = args[0];

        // tweetsPath "/home/yasen/projects/data/sentiment/pos-neg/reviews-14-products/"
        String tweetsPath = args[1];

        // path to twitie xgapp
        String twitieAppPath = args[2];

        // path to twitie xgapp
        String cmuAppPath = args[3];

        GateUtil.initGate(gatePath);

        Corpus corpus = Factory.newCorpus("POStag");

        String[] tweetLines = IO.splitStringToArray(tweetsPath, "\r\n", StandardCharsets.UTF_8);
        Integer docCount = 0;
        for (String tweetLine : tweetLines) {
            String[] tweetLineFeatures = tweetLine.split("\t");
            String tweet = tweetLineFeatures[3];

            tweet = TweetNormalizer.normalize(tweet);

            GateUtil.addDocumentToCorpus(tweet, corpus, docCount.toString());
            docCount++;
        }
        Map<String, String> taggersMap = new HashMap<String, String>();
        taggersMap.put("twitie", twitieAppPath);
        taggersMap.put("CMU", cmuAppPath);

        HashMap<String, List<Map<String, Integer>>> taggersResultMap = executePOSTaggersOverCorpus(corpus, taggersMap);

        if (args.length == 5) {
            String output = args[4];
            PrintStream writer = new PrintStream(new FileOutputStream(output, false));
            try {
                printResults(writer, taggersResultMap);
            } finally {
                writer.flush();
                writer.close();
            }
        } else {
            printResults(System.out, taggersResultMap);
        }

    }

    public static HashMap<String, List<Map<String, Integer>>> executePOSTaggersOverCorpus(Corpus corpus, Map<String, String> taggersMap)
            throws PersistenceException, IOException, ResourceInstantiationException, ExecutionException {
        HashMap<String, List<Map<String, Integer>>> taggersResultMap = new HashMap<String, List<Map<String, Integer>>>();
        for (String tagger : taggersMap.keySet()) {

            ConditionalSerialAnalyserController controller = (ConditionalSerialAnalyserController) PersistenceManager.loadObjectFromFile(new File(taggersMap
                    .get(tagger)));
            controller.setCorpus(corpus);
            controller.execute();

            List<Map<String, Integer>> corpusPOSResults = new ArrayList<Map<String, Integer>>();
            // collect annotations per doc
            Iterator<Document> iter = corpus.iterator();
            while (iter.hasNext()) {
                Map<String, Integer> docPostagMap = new HashMap<String, Integer>();
                Document doc = iter.next();
                docPostagMap.put("#docid#", Integer.valueOf(doc.getName()));
                AnnotationSet defaultAnnotations = doc.getAnnotations();
                AnnotationSet tokenAnnotations = defaultAnnotations.get("Token");
                List<Annotation> posAnnotations = tokenAnnotations.inDocumentOrder();
                for (Annotation annotation : posAnnotations) {
                    FeatureMap features = annotation.getFeatures();
                    // String word = (String) features.get("string");
                    String category = (String) features.get("category");
                    // String kind = (String) features.get("kind");
                    // if (kind == null || kind.equals("word")) { // ignores punctuation in twitie case
                    if (docPostagMap.get(category) != null) {
                        docPostagMap.put(category, docPostagMap.get(category) + 1);
                    } else {
                        docPostagMap.put(category, 1);
                    }
                    // }
                }
                corpusPOSResults.add(docPostagMap);
            }
            taggersResultMap.put(tagger, corpusPOSResults);
        }
        return taggersResultMap;
    }
    
    private static void printResults(PrintStream out, Map<String, List<Map<String, Integer>>> taggersResultMap) {
        for (String tagger : taggersResultMap.keySet()) {
            out.println("Results for " + tagger + " POS tagger:");
            for (Map<String, Integer> docPostagMap : taggersResultMap.get(tagger)) {
                out.print("doc " + docPostagMap.get("#docid#") + " => ");
                for (String postag : docPostagMap.keySet()) {
                    if (!postag.equals("#docid#")) {
                        out.print(postag + "=" + docPostagMap.get(postag) + " ");
                    }
                }
                out.println();
            }
        }
    }
}
