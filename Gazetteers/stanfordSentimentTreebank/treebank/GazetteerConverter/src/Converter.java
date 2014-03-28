import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {

	private static final String DIR = System.getProperty("user.dir");
	private static Map<Integer, String> phrases = new HashMap<Integer, String>();
	private static Map<Integer, String> scores = new HashMap<Integer, String>();

	private static void readPhrases() {
		BufferedReader br = null;
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(DIR + "\\dictionary.txt"));

			while ((currentLine = br.readLine()) != null) {
				String[] splitLine = currentLine.split("\\|");
				String phrase = splitLine[0];
				String phraseId = splitLine[1];

				String regex = "[a-zA-Z]+";

				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(phrase);

				if (matcher.find()) {
					String[] splitphrase = phrase.split(" ");
					if (splitphrase.length <= 4) {
						Integer id = Integer.parseInt(phraseId);
						phrases.put(id, phrase);
//						System.out.println(id + " : " + phrase);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void readScores() {
		BufferedReader br = null;
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(DIR
					+ "\\sentiment_labels.txt"));

			while ((currentLine = br.readLine()) != null) {
				String[] splitLine = currentLine.split("\\|");
				String score = splitLine[1];
				String scoreId = splitLine[0];
				Integer id = Integer.parseInt(scoreId);
				scores.put(id, score);
				// System.out.println(id + " : " + score);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void write() {
		try {
			File file = new File(DIR + "/result2.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			for (Map.Entry<Integer, String> entry : phrases.entrySet()) {
				Integer key = entry.getKey();
				String value = entry.getValue();
				value += "\t" + "sl = " + scores.get(key);
				bw.write(value);
				bw.newLine();
			}
			bw.close();
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		 readPhrases();
		 readScores();
		 write();
	}

}
