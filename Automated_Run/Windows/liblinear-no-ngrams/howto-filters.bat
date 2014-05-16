perl generate-LIBLINEAR-filtered.pl %1

SET LIBSVM_PATH=E:\Software\libsvm-3.18\windows
SET LIBLINEAR_PATH=E:\Software\liblinear-1.94\windows

# Cross_validation:
%LIBLINEAR_PATH%\train.exe -c 1 -v 2 train+dev2013-B-GATE.txt.liblinear
	# Cross Validation Accuracy = 49.1302%



# Prediction
%LIBLINEAR_PATH%\train.exe -c 1 train+dev2013-B-GATE.txt.liblinear train+dev2013-B-GATE.txt.liblinear.model
%LIBLINEAR_PATH%\predict.exe test2013-B-Twitter-GATE.txt.liblinear train+dev2013-B-GATE.txt.liblinear.model test2013-B-Twitter-GATE.txt.liblinear.predictions
	# Accuracy = 16.5492% (587/3547)


#############
## Scaling ##
#############

%LIBSVM_PATH%\svm-scale -l 0 -s train+dev2013-B-GATE.txt.range train+dev2013-B-GATE.txt.liblinear > train+dev2013-B-GATE.txt.liblinear.scale
%LIBSVM_PATH%\svm-scale -r train+dev2013-B-GATE.txt.range test2013-B-Twitter-GATE.txt.liblinear > test2013-B-Twitter-GATE.txt.liblinear.scale

# Scaled Cross_validation:
%LIBLINEAR_PATH%\train.exe -c 0.02 -v 2 train+dev2013-B-GATE.txt.liblinear.scale
	# Cross Validation Accuracy = 70.919%


# Scaled Prediction:

%LIBLINEAR_PATH%\train.exe -c 0.012 train+dev2013-B-GATE.txt.liblinear.scale train+dev2013-B-GATE.txt.liblinear.scale.model
%LIBLINEAR_PATH%\predict.exe test2013-B-Twitter-GATE.txt.liblinear.scale train+dev2013-B-GATE.txt.liblinear.scale.model test2013-B-Twitter-GATE.txt.liblinear.scale.predictions
	# Accuracy = 70.764% (2510/3547)

#Жоро: Stoch MaxEnt  Test  Accuracy = 0.6974908373273189


################
### test2014 ###
################

%LIBSVM_PATH%\svm-scale -r train+dev2013-B-GATE.txt.range test2014-GATE.txt.liblinear > test2014-GATE.txt.liblinear.scale
%LIBLINEAR_PATH%\predict.exe test2014-GATE.txt.liblinear.scale train+dev2013-B-GATE.txt.liblinear.scale.model test2014-GATE.txt.liblinear.scale.predictions

perl predictions-to-text.pl test2014-GATE.txt.liblinear.scale.predictions