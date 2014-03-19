package util;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.persist.SerialDataStore;
import gate.util.GateException;

import java.io.File;

public class GateUtil {

    public static boolean initGate(String gatePath) {
        File gateHome = new File(gatePath);
        Gate.setGateHome(gateHome);
        Gate.setPluginsHome(gateHome);
        try {
            Gate.init();
        } catch (GateException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static SerialDataStore createSerialDataStore(String dataStorePath) {
        File file = new File(dataStorePath);
        file.mkdirs();
        SerialDataStore sds = null;
        try {
            if (file.listFiles().length == 0) {
                sds = (SerialDataStore) Factory.createDataStore("gate.persist.SerialDataStore", file.toURI().toURL().toString());
            } else {
                sds = (SerialDataStore) Factory.openDataStore("gate.persist.SerialDataStore", file.toURI().toURL().toString());
            }
            return sds;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addDocumentToCorpus(String docText, Corpus corpus, String docName) throws Exception {
        FeatureMap params = Factory.newFeatureMap();
        params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, docText);
        params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "UTF-8");
        Document doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
        doc.setName(docName);
        corpus.add(doc);
    }
}
