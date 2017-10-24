#!/bin/sh

BUILDDIR=/home/procheta/NetBeansProjects/AOLTaskExtraction/build/classes/

cd $BUILDDIR


CP=.
for jarfile in `find /home/procheta/NetBeansProjects/AOLTaskExtraction/lib -name "*.jar"`
do
        CP=$CP:$jarfile
done
echo $PWD 
echo "java -cp $CP LucheseImplementation.SimScoreCalculation /home/procheta/NetBeansProjects/AOLTaskExtraction/src/LucheseImplementation/init.properties"
#for J in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0 
# do 
#  for I in 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0 
#   do 
java -cp $CP LucheseImplementation.LucheseEval /home/procheta/AOLTaskExperimentData/queryEmbeddingData/clusterId/timeSortedWithoutlex/rcaresult.txt /home/procheta/newLabelFile.txt /home/procheta/AOLTaskExperimentData/queryEmbeddingData/evalFIle/timeSortedWithoutLex/rcaFinalFile.txt /home/procheta/AOLTaskExperimentData/queryEmbeddingData/result/timeSortedWithoutLex/output.txt
#   done
# done 
