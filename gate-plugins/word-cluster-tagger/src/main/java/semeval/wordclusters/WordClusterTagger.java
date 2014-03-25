package semeval.wordclusters;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Factory;
import gate.FeatureMap;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.InvalidOffsetException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

@CreoleResource(name = "WordClusterTagger", comment = "Wrapper for NRC word cluster tagging.")
public class WordClusterTagger extends AbstractLanguageAnalyser {
    private static final long serialVersionUID = 1L;

    String clustersFilePath;
    String outputAS;
    String inputAS;
    Map<String, List<Pair>> fiftyMCluster = new HashMap<String, List<Pair>>();

    public Resource init() throws ResourceInstantiationException {
        try {
//            System.err.println(this.clustersFilePath);
            InputStream is = getClass().getResourceAsStream(clustersFilePath);
//            listJar();
            if (is != null) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(is, writer, StandardCharsets.UTF_8);
                String clustersFileContent = writer.toString();

                processClusterFile(clustersFileContent, fiftyMCluster);
            } else {
                System.err.println("No resource!");
            }
        } catch (Exception e) {
            throw new ResourceInstantiationException(e);
        }
        return this;
    }

    public void listJar() throws IOException{ 
        CodeSource src = WordClusterTagger.class.getProtectionDomain().getCodeSource();
        if (src != null) {
          URL jar = src.getLocation();
          ZipInputStream zip = new ZipInputStream(jar.openStream());
          while(true) {
            ZipEntry e = zip.getNextEntry();
            if (e == null)
              break;
            String name = e.getName();
            System.err.println(name);
          }
        } 
        else {
          /* Fail... */
        }
    }

    private void processClusterFile(String fileAsStr, Map<String, List<Pair>> clusterMap) {
        String[] lines = fileAsStr.trim().split("\n");
        for (String linedata : lines) {
            String[] content = linedata.trim().split("\t");
            String word = content[1];
            if (!clusterMap.containsKey(word)) {
                clusterMap.put(word, new ArrayList<Pair>());
            }
            List<Pair> clustersList = clusterMap.get(word);
            String cluster = content[0];
            Integer countInCluster = Integer.valueOf(content[2]);
            Pair current = new Pair(cluster, countInCluster);
            if (clustersList.contains(current)) {
                clustersList.get(clustersList.indexOf(current)).addCount(current.getCountInCluster());
            } else {
                clustersList.add(current);
            }
        }
    }

    public void execute() throws ExecutionException {
        AnnotationSet outAS = getAS(outputAS);
        AnnotationSet inAS = getAS(inputAS);
        try {
            for (Annotation sentAnn : inAS.get("Token")) {
                long start = sentAnn.getStartNode().getOffset();
                long end = sentAnn.getEndNode().getOffset();

                String token = document.getContent().getContent(start, end).toString();
                addToken(outAS, start, end, token, sentAnn.getFeatures());
                
                System.err.println(token);

            }
        } catch (InvalidOffsetException e) {
            throw new ExecutionException(e);
        }
    }

    private AnnotationSet getAS(String name) {
        AnnotationSet as = null;
        if (name != null) {
            as = document.getAnnotations(name);
        } else {
            as = document.getAnnotations();
        }
        return as;
    }

    private void addToken(AnnotationSet out, long sentStart, long sentEnd, String token, FeatureMap fm) throws InvalidOffsetException {
            List<Pair> clusters = this.fiftyMCluster.get(token);
            if (clusters != null) {
            for (Pair cluster : clusters) {
                if (fm.get(cluster.getCluster()) != null) {
                    String label = (String)fm.get(cluster.getCluster());
                    fm.put("wc", label + "\t" + cluster.getCluster());
                } else {
                    fm.put("wc", cluster.getCluster());
                }
            }
//            out.add(sentStart, sentEnd, "Token", fm);
        }
    }

    @Optional
    @RunTime
    @CreoleParameter(comment = "name of the annotationSet used for output")
    public void setOutputAnnotationSetName(String setName) {
        this.outputAS = setName;
    }

    @Optional
    @RunTime
    @CreoleParameter(comment = "name of the annotationSet used for input")
    public void setInputAnnotationSetName(String setName) {
        this.inputAS = setName;
    }

    @CreoleParameter(comment = "Path to the clusters file. Default value points to a resource in this jar", defaultValue = "/clusters/50mpaths2", disjunction = "clustersFilePath")
    public void setClusterFilePath(String clustersFilePath) {
        this.clustersFilePath = clustersFilePath;
    }
    
    public String getClusterFilePath() {
        return this.clustersFilePath;
    }

    public void reInit() throws ResourceInstantiationException {
        init();
    }

    public String getOutputAnnotationSetName() {
        return outputAS;
    }

    public String getInputAnnotationSetName() {
        return inputAS;
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

    class Pair {
        private String cluster;
        private int countInCluster;

        public Pair(String word, int count) {
            this.cluster = word;
            this.countInCluster = count;
        }

        public String getCluster() {
            return this.cluster;
        }

        public int getCountInCluster() {
            return this.countInCluster;
        }

        public void addCount(int additionalCount) {
            this.countInCluster += additionalCount;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((cluster == null) ? 0 : cluster.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Pair other = (Pair) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (cluster == null) {
                if (other.cluster != null)
                    return false;
            } else if (!cluster.equals(other.cluster))
                return false;
            return true;
        }

        private WordClusterTagger getOuterType() {
            return WordClusterTagger.this;
        }

    };

}
