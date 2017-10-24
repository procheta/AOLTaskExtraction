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
for J in 0.5 0.6 0.7 0.8 0.9 1 
 do 
  for I in 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0 
   do 
    java -cp $CP LucheseImplementation.LucheseEval /home/procheta/clusterId/dbscanClusterId/wordLevelResult/dbscan_clusterId_eps_$J"_alpha_"$I".txt" /home/procheta/labelFile.txt /home/procheta/finalEvalFIle/dbscanEvalFile/wordLevelFIle/dbFinalFile_eps_$J"_alpha_"$I".txt" /home/procheta/clusterResult/dbscanResult/wordLevelResult/dbResult_alpha_$I"_eps_"$J".txt" 
   done
 done 
