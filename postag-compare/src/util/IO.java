package util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;


public class IO {
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
