package arffbuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.SortedMap;
import java.util.TreeMap;

public class ArffBuilder {

    private static SortedMap<String, Integer> featureToIndex = new TreeMap<String, Integer>();
    private static int index = 0;
    
    private static String[] filterOut = {"id1", "id2", "STEM", "LEMMA", "NGRAM", "URL", "WC_PUNCT_2GRAM", "USR", "PRE5", "SUF5", "PRE4", "SUF4", "NNP", "positive", "negative", "neutral", "unknwn", "PRE", "SUF"};
    private static String[] whilelist = {"mpqa", "nrcEmo", "num"};
    
    static String testGoldPath;

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String trainDocsPath = args[0];
        String testDocsPath = args[1];
        String trainArffPath = args[2];
        String testArffPath = args[3];
        testGoldPath = args[4];

        System.out.print("Extracting global feature list...");
        extractFeaturesFromDocs(trainDocsPath);
        extractFeaturesFromDocs(testDocsPath);
        System.out.println("DONE");
        System.out.println("Global feature count: " + featureToIndex.size());

        System.out.print("Writing train arff...");
        createArff(trainDocsPath, trainArffPath, true, false);
        System.out.println("DONE");

        System.out.print("Writing test arff...");
        createArff(testDocsPath, testArffPath, false, true);
        System.out.println("DONE");
    }

    private static void createArff(String docsPath, String arffPath, boolean classIncluded, boolean addGold) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(arffPath);
            writer.write("@RELATION semeval\n\n");

            for (String featureName : featureToIndex.keySet()) {
                writer.write("@ATTRIBUTE " + featureName + " NUMERIC\n");
            }
            
            writer.write("@ATTRIBUTE class {positive, negative, neutral}\n\n");
            writer.write("@DATA\n");

            writeArffFromDoc(docsPath, writer, classIncluded, addGold);

        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    private static void writeArffFromDoc(String docsPath, FileWriter writer, boolean classIncluded, boolean addGold) throws IOException {
        File docDir = new File(docsPath);
        for (File doc : docDir.listFiles()) { 
            if (!doc.isDirectory()) {
                String[] instances = splitStringToArray(doc.getAbsolutePath(), "\n", StandardCharsets.UTF_8);
                int ind = 0;
                for (String instance : instances) {
                    writer.write("{");
                    String[] instanceData = instance.split("\t");
                    String instanceClass = null;
                    if (classIncluded) {
                        instanceClass = instanceData[instanceData.length - 1];
                    }
                    SortedMap<Integer, String> sortedInstances = new TreeMap<Integer, String>();
                    for (int i = 3; i < instanceData.length; i++) {
                        String featureInstance = normalize(instanceData[i]);
                        String[] featureInstanceData = featureInstance.split("=");
                        String normalizedFeatureLabel = featureInstanceData[0];

                        if (featureToIndex.get(normalizedFeatureLabel) != null) {
                            // add ugly after processing for features without values (like NGRAM,PRE,SUF,LEMMA)
                            // TODO remove check when feature representation is universalized
                            if (featureInstanceData.length == 1) {
                                sortedInstances.put(featureToIndex.get(normalizedFeatureLabel), "1");
                            } else {
                                // replace inconsistent boolean values with 0 and 1
                                // TODO remove check when fixed
                                if (featureInstanceData[1].equals("true")) {
                                    sortedInstances.put(featureToIndex.get(normalizedFeatureLabel), "1");
                                } else {
                                    // generic processing of feature-value pairs
                                    sortedInstances.put(featureToIndex.get(normalizedFeatureLabel), featureInstanceData[1]);
                                }
                            }
                            //normalization debug info
                            try {
                                Integer.parseInt(sortedInstances.get(featureToIndex.get(normalizedFeatureLabel)));
                            } catch (Exception e) {
                                System.err.println("ERROR:");
                                System.err.println(instanceData[i]);
                                System.err.println(featureInstance);
                                System.err.println(featureInstanceData[0]);
                                System.err.println(sortedInstances.get(featureToIndex.get(normalizedFeatureLabel)));
                            }
                        }
                    }
                    boolean isFirst = true;
                    for (Integer index : sortedInstances.keySet()) {
                        if (isFirst) {
                            isFirst = false;
                        } else {
                            writer.write(", ");
                        }
                        writer.write(index + " " + sortedInstances.get(index));
                    }
                    if (classIncluded) {
                        writer.write(", " + featureToIndex.size() + " " + instanceClass);
                    }
                    if (addGold) {
                        String[] goldData = splitStringToArray(testGoldPath, "\n",  StandardCharsets.UTF_8);
                        String[] data = goldData[ind].split("\t");
                        writer.write(", " + featureToIndex.size() + " " + data[2]);
                    }
                    ind++;
                    writer.write("}\n");
                }
            }
        }
    }

    private static void extractFeaturesFromDocs(String docsPath) throws IOException {
        File docDir = new File(docsPath);
        for (File doc : docDir.listFiles()) {
            if (!doc.isDirectory()) {
                String[] instances = splitStringToArray(doc.getAbsolutePath(), "\n", StandardCharsets.UTF_8);
                for (String instance : instances) {
                    String[] instanceData = instance.split("\t");
                    for (int i = 3; i < instanceData.length; i++) {
                        String featureLabel = normalize(instanceData[i]);
                        String[] normalizedFeatureLabel = featureLabel.split("=");
                        if (!matchesFilteredFeatures(normalizedFeatureLabel[0])) {
                            if (!featureToIndex.containsKey(normalizedFeatureLabel[0])) {
                                featureToIndex.put(normalizedFeatureLabel[0], index);
                                index++;
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean matchesFilteredFeatures(String normalizedFeatureLabel) {
        for (String out : filterOut) {
            if (normalizedFeatureLabel.startsWith(out)) return true;
        }
        return false;
    }

    /*
     * This normalization squashes about 20K features
     */
    private static String normalize(String featureLabel) {
        return featureLabel.replaceAll("={2,}", "_EQUAL_=") //normalize multiple equals
                .replaceAll("=\\d*\\D", "_EQGRAD_") //normalize =1st, =2nd, etc
                .replaceAll("(?![=])\\p{Punct}", "_PUNCT_") //normalize punctuation, except =
                .replaceAll("=\\D", "_EQCHAR_"); //normalize chars after equal
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

    public static String[] splitStringToArray(String path, String splitter, Charset encoding) throws IOException {
        String content = readFile(path, encoding);
        String[] words = content.trim().split(splitter);
        return words;
    }
}
