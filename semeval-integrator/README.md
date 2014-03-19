Tweet POS tagger comparator. As a result prints for each tagger the predicted tags for each tweet on a new line.

Requirements:
	- valid installation of yasen's CMU pos tagger gate plugin
	- export of application that executes yasen's CMU wrapper in xgapp format
		- this application must consist of a sentence splitter and the CMU wrapper.
Usage:
expects 4 mandatory arguments in the launch configuration in this order
	- gate root path
	- tweets file path
	- twitie xgapp (path to gate plugins/twitie/*.xgapp)
	- cmu xgapp (path to yasen's gate plugin wrapper of the CMU wrapper saved as xgapp)
optional arguments
	- output file (if not specified, prints in the console by default)