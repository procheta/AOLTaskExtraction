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
for J in 0.0 
 do 
  for I in 0.3 
   do 
    java -cp $CP LucheseImplementation.LucheseEval /home/procheta/AOLTaskExperimentData/queryEmbeddingData/clusterId/timeSortedWithoutlex/qcluster_ita_ /home/procheta/labelFile.txt /home/procheta/AOLTaskExperimentData/fastTextqueryEmbeddingData/finalFile/qfinalfile_ita_$J"_alpha_"$I".txt" /home/procheta/AOLTaskExperimentData/fastTextqueryEmbeddingData/results/qresult_alpha_$I"_ita_"$J".txt" 
   done
 done 
