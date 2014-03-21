Lexicons for Gate 
=================
TASK
----
8. sentiment lexicons (existing ones): automatically created lexicons (NRC Hashtag Sentiment Lexicon, Sentiment140 Lexicon), manually created sentiment lexicons (NRC Emotion Lexicon, MPQA, Bing Liu Lexicon). For each lexicon and each polarity we calculated:
      
      - total count of tokens in the tweet with score greater than 0;
      - the sum of the scores for all tokens in the tweet;
      - the maximal score;
      - the non-zero score of the last token in the tweet;
      - other ?
      - scores of matches in a hashtag (using gate TWITIE hashtag tokenizer)
      The lexicon features were created for all tokens in the tweet, for each part-of-speech tag, for hashtags, and for all-caps tokens.  
	  
Status
------
	  
##NRC Hashtag Sentiment Lexicon
Created Gate gazetteers from the given raw lexicons. 
However I don't know what to do with the non-continous words lexicon - pairs-pmilexicon.txt

##NRC Hashtag Sentiment Lexicon
Created Gate gazetteers from the given raw lexicons. 
However I don't know what to do with the non-continous words lexicon - pairs-pmilexicon.txt




