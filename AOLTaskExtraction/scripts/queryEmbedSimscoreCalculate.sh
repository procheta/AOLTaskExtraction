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
#for J in 0.1 0.2 0.5 0.6 0.7 0.8 1.0
#Do
 for I in 0 
 do 
    java -cp $CP LucheseImplementation.SimScoreCalCulationTfIdf $I 0.3 
  done
#done 
