package resultbuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultBuilder {

	public static void main(String[] args) throws Exception {
		String officialTrainPath = args[0];
		String scoredFilePath = args[1];

		// train file
		InputStream fisTrain = new FileInputStream(officialTrainPath);
		BufferedReader brTrain = new BufferedReader(new InputStreamReader(
				fisTrain, Charset.forName("UTF-8")));
		String line = "";

		// scored file
		InputStream fisScored = new FileInputStream(scoredFilePath);
		BufferedReader brScored = new BufferedReader(new InputStreamReader(
				fisScored, Charset.forName("UTF-8")));
		String lineScore = "";

		// read parrallel from the two files and write to output
		while (((line = brTrain.readLine()) != null)
				&& ((lineScore = brScored.readLine()) != null)) {
			Pattern pattern = Pattern.compile("(.*)\t(.*)\t(.*)\t(.*)");
			Matcher matcher = pattern.matcher(line);

			if (!matcher.matches()) {
				throw new Exception("invalid input file line");
			}
			String resultLine = String.format("%s\t%s\t%s\t%s", matcher.group(1),
					matcher.group(2), lineScore, matcher.group(4));
			System.out.println(resultLine);
		}

		// Done with the files
		brTrain.close();
		brTrain = null;
		fisTrain = null;

		brScored.close();
		brScored = null;
		fisScored = null;
	}

}
