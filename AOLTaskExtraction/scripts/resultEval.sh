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
for J in 0.5 0.6 0.7 0.8 0.9 
 do 
  for I in 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0 
   do 
    java -cp $CP LucheseImplementation.LucheseEval /home/procheta/AOLTaskExperimentData/LucheseBaseLineData/clusterId/weightedClusterId/wordLevelResult/qCluster__ita_$J"_alpha_"$I".txt" /home/procheta/all-task-trueLabel.txt /home/procheta/AOLTaskExperimentData/LucheseBaseLineData/finalEvalFIle/weightedClusteringEvalFile/wordLevelResult/qfinalFile_ita_$J"_alpha_"$I".txt" /home/procheta/AOLTaskExperimentData/LucheseBaseLineData/clusterResult/weightedClusterResult/wordLevelResult/qresult_alpha_$I"_ita_"$J".txt" 
   done
 done 
