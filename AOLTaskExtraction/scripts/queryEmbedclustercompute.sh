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
for J in 0 
 do 
  for I in  0.3  
  do 
   java -cp $CP LucheseImplementation.WeightedClustering /home/procheta/NetBeansProjects/AOLTaskExtraction/src/LucheseImplementation/init.properties $I $J 
  done
 done 
