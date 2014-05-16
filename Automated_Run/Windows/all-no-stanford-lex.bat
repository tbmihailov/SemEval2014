SET filters_file=all-no-stanford-lex.filters

echo Run liblinear conversion with filters
cd liblinear-no-ngrams
copy ..\%filters_file% %filters_file%
call howto-filters.bat %filters_file%
del %filters_file%

copy test2014-GATE.txt.liblinear.scale.predictions.text ..\submissions\test2014-GATE.txt.liblinear.scale.predictions.text

cd ..
echo Generate submission
cd submissions
call eval-B-no-ngrams.bat

rem copy no ngrams
cd ..
copy submissions\subtaskB-no-ngrams.scores submission.%filters_file%.subtaskB-no-ngrams.scores

