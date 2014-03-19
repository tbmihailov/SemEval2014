package util;

import org.apache.commons.lang.StringEscapeUtils;

public class TweetNormalizer {
    public static String normalize(String tweet) {
        String normalizedTweet = tweet;
        normalizedTweet = decodeUTF(normalizedTweet);
        return normalizedTweet;
    }
    
    private static String decodeUTF(String tweet) {
        return StringEscapeUtils.unescapeJava(tweet);
    }
}
