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
#for J in 0.5 0.6 0.7 0.8 0.9 
# do 
#  for I in 0.2 .3 .4 .5 .6 .7 .8 .9 1 
#  do 
    java -cp $CP preprocessing.ClustEval /home/procheta/NetBeansProjects/AOLTaskExtraction/src/LucheseImplementation/init.properties 0.2 0.2  
#  done
# done 
