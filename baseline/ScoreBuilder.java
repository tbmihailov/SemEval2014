package scorebuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ScoreBuilder {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String trainPath = args[0];
        String testPath = args[1];
        String predTemplatePath = args[2];
        String predPath = args[3];

        System.out.print("Reading training instances...");
        // train data
        DataSource trainSource = new DataSource(trainPath);
        Instances trainData = trainSource.getDataSet();
        if (trainData.classIndex() == -1)
            trainData.setClassIndex(trainData.numAttributes() - 1);
        System.out.println("DONE");

        System.out.print("Reading test instances...");
        // test data
        DataSource testSource = new DataSource(testPath);
        Instances testData = testSource.getDataSet();
        if (testData.classIndex() == -1)
            testData.setClassIndex(testData.numAttributes() - 1);
        Instances labeled = new Instances(testData);
        System.out.println("DONE");

        System.out.print("Building classifier...");
        // train model
        LibSVM classifier = new LibSVM();
        classifier.setOptions(weka.core.Utils.splitOptions("-S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1 -seed 1"));
        classifier.buildClassifier(trainData);
        System.out.println("DONE");

        System.out.print("Exporting...");
        // classify
        FileWriter writer = null;
        try {
            writer = new FileWriter(predPath);
            String[] instanceTemplates = splitStringToArray(predTemplatePath, "\n", StandardCharsets.UTF_8);
            int index = 0;
            for (String instanceTemplate : instanceTemplates) {
                double clsLabel = classifier.classifyInstance(testData.instance(index));
                labeled.instance(index).setClassValue(clsLabel);
                String labeledInstance = instanceTemplate.replaceFirst("unknwn", labeled.classAttribute().value((int) clsLabel));
                writer.write(labeledInstance);
                writer.write("\n");
                index++;
            }
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
        System.out.println("DONE");
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
