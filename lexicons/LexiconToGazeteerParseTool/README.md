LexiconToGazetteerParseTool
===========================
##About
Command line tool for turning LARGE raw lexicon files into GATE gazetteer format.

##How to

The tool should be executed with 4 parameters:
1.input file
2.regex for matching SINGLE LINE
3.regex replace
4.output file

##Sample usage
```
LexiconToGazetteer bigrams-pmilexicon.txt "(.+)\t(.+)\t(.+)\t(.+)" "$1	sentimentScore=$2	numPositive=$3	numNegative=$4" bigrams-pmilexicon.lst
```
where $1, 2$ are regex groups

